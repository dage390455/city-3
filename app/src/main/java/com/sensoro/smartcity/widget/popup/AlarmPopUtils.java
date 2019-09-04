package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.model.SecurityRisksAdapterModel;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.SelectDialog;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.slideverify.SlidePopUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;
import com.sensoro.common.widgets.dialog.TipDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.SecurityRisksActivity;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.adapter.AlarmPopupContentAdapter;
import com.sensoro.smartcity.adapter.AlarmPopupMainTagAdapter;
import com.sensoro.common.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.AlarmPopModel;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sensoro.smartcity.widget.imagepicker.ImagePicker.EXTRA_RESULT_BY_TAKE_PHOTO;


public class AlarmPopUtils implements Constants,
        ImagePickerAdapter.OnRecyclerViewItemClickListener, UpLoadPhotosUtils.UpLoadPhotoListener, SelectDialog.SelectDialogListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    @BindView(R.id.iv_alarm_popup_close)
    ImageView ivAlarmPopupClose;
    @BindView(R.id.tv_alarm_popup_name)
    TextView tvAlarmPopupName;
    @BindView(R.id.tv_alarm_popup_device_type)
    TextView tvAlarmPopupDeviceType;
    @BindView(R.id.tv_alarm_popup_date)
    TextView tvAlarmPopupDate;
    @BindView(R.id.tv_alarm_popup_status)
    TextView tvAlarmPopupStatus;
    @BindView(R.id.rv_alarm_popup_content)
    RecyclerView rvAlarmPopupContent;
    @BindView(R.id.tv_alarm_popup_alarm_security_risks)
    TextView tvAlarmPopupAlarmSecurityRisks;
    @BindView(R.id.tv_alarm_popup_alarm_security_risks_content)
    TextView tvAlarmPopupAlarmSecurityRisksContent;
    @BindView(R.id.iv_alarm_popup_alarm_security_risks_arrow)
    ImageView ivAlarmPopupAlarmSecurityRisksArrow;
    @BindView(R.id.ll_alarm_popup_alarm_security_risks)
    LinearLayout llAlarmPopupAlarmSecurityRisks;
    @BindView(R.id.et_alarm_popup_remark)
    EditText etAlarmPopupRemark;
    @BindView(R.id.rv_alarm_popup_photo)
    RecyclerView rvAlarmPopupPhoto;
    @BindView(R.id.bt_alarm_popup_commit)
    Button btAlarmPopupCommit;
    @BindView(R.id.tv_alarm_popup_alarm_reason)
    TextView tvAlarmPopupAlarmReason;
    @BindView(R.id.rv_alarm_popup_alarm_reason)
    RecyclerView rvAlarmPopupAlarmReason;
    @BindView(R.id.tv_alarm_popup_result_reason_tip)
    TextView tvAlarmPopupResultReasonTip;
    private FixHeightBottomSheetDialog bottomSheetDialog;
    private Unbinder bind;
    private Activity mActivity;
    private View mRoot;
    //
    private OnPopupCallbackListener mListener;
    //
    //
    private ImagePickerAdapter adapter;
    private AlarmPopupContentAdapter alarmPopupContentAdapter;
    private AlarmPopupMainTagAdapter alarmPopupMainTagAdapter;
    private final ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private ProgressDialog progressDialog;
    private UpLoadPhotosUtils upLoadPhotosUtils;
    private AlarmPopupModel mAlarmPopupModel;
    private TipDialogUtils mRealFireDialog;
    private TipDialogUtils mExitDialog;

    public AlarmPopUtils(Activity activity) {
        mActivity = activity;
        initView();
        intData();
    }

    public void onDestroyPop() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.cancel();
        }
        if (progressDialog != null) {
            progressDialog.cancel();
        }

        if (mRealFireDialog != null) {
            mRealFireDialog.destory();
            mRealFireDialog = null;
        }

        if(mSlidePopUtils!=null){
            mSlidePopUtils.destroySlideVerifyDialog();
            mSlidePopUtils=null;
        }

        if (bind != null) {
            bind.unbind();
            bind = null;
        }
        selImageList.clear();
    }

    public void setDescText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvAlarmPopupResultReasonTip.setVisibility(View.GONE);
        } else {
            tvAlarmPopupResultReasonTip.setVisibility(View.VISIBLE);
            tvAlarmPopupResultReasonTip.setText(text);
        }

    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names, String string) {
        SelectDialog dialog = new SelectDialog(mActivity, R.style
                .transparentFrameWindowStyle,
                listener, names, string);
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private void showProgressDialog(String content, double percent) {
        if (progressDialog != null) {
            progressDialog.setProgress((int) (percent * 100));
            progressDialog.setTitle(content);
            progressDialog.show();
        }
    }

    public void SetSecurityRiskVisible(boolean isVisible, boolean isRequire) {
        if (isVisible) {
            if (isRequire) {
                tvAlarmPopupAlarmSecurityRisks.setText(mActivity.getString(R.string.alarm_pop_security_risks) + "(" + mActivity.getString(R.string.required) + ")");
            } else {
                tvAlarmPopupAlarmSecurityRisks.setText(mActivity.getString(R.string.alarm_pop_security_risks));
            }
        }
        llAlarmPopupAlarmSecurityRisks.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        tvAlarmPopupAlarmSecurityRisks.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
    }

    private void intData() {
    }

    private void initView() {
        mRoot = View.inflate(mActivity, R.layout.layout_alarm_popup_test, null);
        bind = ButterKnife.bind(this, mRoot);
        bottomSheetDialog = new FixHeightBottomSheetDialog(mActivity);
        //
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.setOnDismissListener(this);
        bottomSheetDialog.setOnCancelListener(this);

        //
        upLoadPhotosUtils = new UpLoadPhotosUtils(mActivity, this);
        bottomSheetDialog.setContentView(mRoot);
        //以下设置是为了解决：下滑隐藏dialog后，再次调用show方法显示时，不能弹出Dialog----在真机测试时不写下面的方法也未发现问题
        View delegateView = bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        final BottomSheetBehavior<View> sheetBehavior = BottomSheetBehavior.from(delegateView);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            //在下滑隐藏结束时才会触发
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                //禁止滑动
//                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                }
            }

            //每次滑动都会触发
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                try {
                    LogUtils.loge("onSlide = [" + bottomSheet + "], slideOffset = [" + slideOffset + "]");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        //监听back事件
        bottomSheetDialog.setOnBottomSheetDialogBackPressedListener(new FixHeightBottomSheetDialog.OnBottomSheetDialogBackPressedListener() {
            @Override
            public void onBottomSheetDialogBackPressed() {
                exitDialogShow();
            }
        });
        //
        adapter = new ImagePickerAdapter(mActivity, selImageList);
        adapter.setAddTipText(mActivity.getString(R.string.photo_recording));
        adapter.setOnItemClickListener(this);
//        adapter.canVideo(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 4);
        rvAlarmPopupPhoto.setLayoutManager(layoutManager);
        rvAlarmPopupPhoto.setHasFixedSize(true);
        rvAlarmPopupPhoto.setAdapter(adapter);
        //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
        rvAlarmPopupPhoto.setNestedScrollingEnabled(false);
        //主预警类型
        alarmPopupMainTagAdapter = new AlarmPopupMainTagAdapter(mActivity);
        rvAlarmPopupAlarmReason.setAdapter(alarmPopupMainTagAdapter);
        //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setSmoothScrollbarEnabled(true);
        manager.setAutoMeasureEnabled(true);
        //
        rvAlarmPopupAlarmReason.setHasFixedSize(true);
        rvAlarmPopupAlarmReason.setNestedScrollingEnabled(false);
        rvAlarmPopupAlarmReason.setLayoutManager(manager);
        alarmPopupMainTagAdapter.setOnAlarmPopupMainTagAdapterItemClickObserver(new AlarmPopupMainTagAdapter.OnAlarmPopupMainTagAdapterItemClickObserver() {
            @Override
            public void onClick(View v, int position) {
                if (mAlarmPopupModel != null) {
                    final AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mAlarmPopupModel.mainTags.get(position);
                    //TODO 处理sub标签类
                    ThreadPoolManager.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            AlarmPopupConfigAnalyzer.handleAlarmPopupModel(alarmPopupTagModel.id, mAlarmPopupModel);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDescText(mAlarmPopupModel.desc);
                                    SetSecurityRiskVisible(mAlarmPopupModel.securityRiskVisible, mAlarmPopupModel.isSecurityRiskRequire);
                                    alarmPopupContentAdapter.updateData(mAlarmPopupModel.subAlarmPopupModels);
                                    btAlarmPopupCommit.setBackground(mActivity.getResources().getDrawable(mAlarmPopupModel.resButtonBg));
                                }
                            });
                        }
                    });

                }
            }
        });
        //子类型
        alarmPopupContentAdapter = new AlarmPopupContentAdapter(mActivity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAlarmPopupContent.setLayoutManager(linearLayoutManager);
        rvAlarmPopupContent.setAdapter(alarmPopupContentAdapter);
        //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
        rvAlarmPopupContent.setNestedScrollingEnabled(false);

        //
        initRealFireDialog();

        initExitDialog();
    }

    private void initExitDialog() {
        mExitDialog = new TipDialogUtils(mActivity);
        mExitDialog.setTipMessageText(mActivity.getString(R.string.exit_alarm_confirm));
        mExitDialog.setTipCacnleText(mActivity.getString(R.string.cancel), mActivity.getResources().getColor(R.color.c_a6a6a6));
        mExitDialog.setTipConfirmText(mActivity.getString(R.string.confirm_exit), mActivity.getResources().getColor(R.color.c_f34a4a));
        mExitDialog.setTipDialogUtilsClickListener(new TipDialogUtils.TipDialogUtilsClickListener() {

            @Override
            public void onCancelClick() {
                mExitDialog.dismiss();
            }

            @Override
            public void onConfirmClick() {
                mExitDialog.dismiss();
                dismissInputMethodManager(etAlarmPopupRemark);
                etAlarmPopupRemark.clearFocus();
                etAlarmPopupRemark.getText().clear();
                dismiss();
            }
        });
    }

    SlidePopUtils mSlidePopUtils;

    private void initRealFireDialog() {
        mRealFireDialog = new TipDialogUtils(mActivity);
        mRealFireDialog.setTipMessageText(mActivity.getString(R.string.confirm_upload_real_fire));
        mRealFireDialog.setTipCacnleText(mActivity.getString(R.string.cancel), mActivity.getResources().getColor(R.color.c_a6a6a6));
        mRealFireDialog.setTipConfirmText(mActivity.getString(R.string.confirm_upload), mActivity.getResources().getColor(R.color.c_f34a4a));
        mRealFireDialog.setTipDialogUtilsClickListener(new TipDialogUtils.TipDialogUtilsClickListener() {

            @Override
            public void onCancelClick() {
                mRealFireDialog.dismiss();
            }

            @Override
            public void onConfirmClick() {
                mRealFireDialog.dismiss();
                doCommit();
            }
        });


        mSlidePopUtils = new SlidePopUtils();
        mSlidePopUtils.setTitle(mActivity.getResources().getString(R.string.slide_dialog_title))
                .setDesc(mActivity.getResources().getString(R.string.slide_dialog_desc))
                .setListener(new SlidePopUtils.VerifityResultListener() {
            @Override
            public void onAccess(long time) {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.slide_dialog_success), Toast.LENGTH_SHORT).show();
                mSlidePopUtils.dismissDialog();
                doCommit();
            }

            @Override
            public void onFailed(int failCount) {
                mSlidePopUtils.dismissDialog();
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.slide_dialog_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMaxFailed() {
                mSlidePopUtils.dismissDialog();
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.slide_dialog_failed_maxcount), Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void show(final AlarmPopupModel alarmPopupModel) {
        //
        this.etAlarmPopupRemark.getText().clear();
//        mButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        btAlarmPopupCommit.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_button));
        setUpdateButtonClickable(true);
        if (progressDialog != null) {
            progressDialog.setProgress(0);
        }
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
        }
        selImageList.clear();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //TODO 作为默认项展示
        this.mAlarmPopupModel = alarmPopupModel;
        mAlarmPopupModel.mRemark = null;
        if (mAlarmPopupModel.securityRisksList != null) {
            mAlarmPopupModel.securityRisksList.clear();
            mAlarmPopupModel.securityRisksList = null;
        }
        tvAlarmPopupAlarmSecurityRisksContent.setText(mActivity.getString(R.string.text_unfilled));
        SetSecurityRiskVisible(mAlarmPopupModel.securityRiskVisible, mAlarmPopupModel.isSecurityRiskRequire);
        tvAlarmPopupName.setText(mAlarmPopupModel.title);
        String type = mActivity.getString(R.string.unknown);
        if (!TextUtils.isEmpty(mAlarmPopupModel.mergeType)) {
            MergeTypeStyles configMergeType = PreferencesHelper.getInstance().getConfigMergeType(mAlarmPopupModel.mergeType);
            if (configMergeType != null) {
                String name = configMergeType.getName();
                if (!TextUtils.isEmpty(name)) {
                    type = name;
                }
            }

        }
        tvAlarmPopupDeviceType.setText(type);
        tvAlarmPopupDate.setText(DateUtil.getStrTimeTodayByDevice(mActivity, mAlarmPopupModel.updateTime));
        if (1 == mAlarmPopupModel.alarmStatus) {
            tvAlarmPopupStatus.setTextColor(mActivity.getResources().getColor(R.color.c_1dbb99));
            tvAlarmPopupStatus.setText(mActivity.getString(R.string.normal));
        } else {
            tvAlarmPopupStatus.setTextColor(mActivity.getResources().getColor(R.color.color_alarm_pup_red));
            tvAlarmPopupStatus.setText(mActivity.getString(R.string.status_alarm_true));
        }
        alarmPopupMainTagAdapter.updateAdapter(mAlarmPopupModel.mainTags);
        alarmPopupContentAdapter.updateData(mAlarmPopupModel.subAlarmPopupModels);
        setDescText(mAlarmPopupModel.desc);
        btAlarmPopupCommit.setBackground(mActivity.getResources().getDrawable(mAlarmPopupModel.resButtonBg));
    }


    public void setOnPopupCallbackListener(OnPopupCallbackListener listener) {
        this.mListener = listener;
    }

    public void dismiss() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
    }

    private void clearData() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        selImageList.clear();
        if (mAlarmPopupModel != null) {
            mAlarmPopupModel.mRemark = null;
        }

        adapter.setImages(selImageList);
        setUpdateButtonClickable(false);
    }

    private void doAlarmConfirm() {
        if (!AlarmPopupConfigAnalyzer.canGoOnNext(mAlarmPopupModel)) {
            toastShort(mActivity.getString(R.string.please_complete_the_required_fields_first));
            return;
        }
        if (mAlarmPopupModel.resButtonBg == R.drawable.shape_button_alarm_pup) {
            mRealFireDialog.show();
//            mSlidePopUtils.showDialog(mActivity);
            return;
        }
        setUpdateButtonClickable(false);

        String remark = etAlarmPopupRemark.getText().toString();
        if (!TextUtils.isEmpty(remark)) {
            mAlarmPopupModel.mRemark = remark;
        }
        doCommit();
    }

    private void doCommit() {
        if (mListener != null) {
            if (selImageList.size() > 0) {
                setProgressDialog();
                upLoadPhotosUtils.doUploadPhoto(selImageList);
            } else {
                dismissProgressDialog();
                mListener.onPopupCallback(mAlarmPopupModel, null);
            }
        }
    }

    private void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.image_delete) {
            ImageItem imageItem = selImageList.get(position);
            Iterator<ImageItem> iterator = selImageList.iterator();
            while (iterator.hasNext()) {
                ImageItem next = iterator.next();
                if (next.equals(imageItem)) {
                    iterator.remove();
                    break;
                }
            }
            adapter.setImages(selImageList);
        } else if (IMAGE_ITEM_ADD == position) {
            List<String> names = new ArrayList<>();
            names.add(mActivity.getString(R.string.take_photo));
            names.add(mActivity.getString(R.string.shooting_video));
            names.add(mActivity.getString(R.string.album));
            showDialog(this, names, mActivity.getResources().getString(R.string.camera_photo));
        } else {
            //打开预览
            ImageItem imageItem = selImageList.get(position);
            if (imageItem.isRecord) {
                Intent intent = new Intent();
                intent.setClass(mActivity, VideoPlayActivity.class);
                intent.putExtra(Constants.EXTRA_PATH_RECORD, (Serializable) imageItem);
                intent.putExtra(Constants.EXTRA_VIDEO_DEL, true);
                mActivity.startActivityForResult(intent, REQUEST_CODE_PLAY_RECORD);
            } else {
                Intent intentPreview = new Intent(mActivity, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                mActivity.startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
            }

        }

    }

    @Override
    public void onStart() {
        if (progressDialog != null) {
            progressDialog.setTitle(mActivity.getString(R.string.please_wait));
            progressDialog.setProgress(0);
            progressDialog.show();
        }
    }

    @Override
    public void onComplete(List<ScenesData> scenesDataList) {
        StringBuilder s = new StringBuilder();
        for (ScenesData scenesData : scenesDataList) {
            s.append(scenesData.url).append("\n");
        }
        dismissProgressDialog();
        try {
            LogUtils.loge(this, "上传成功---" + s);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //TODO 上传结果
        if (mListener != null) {
            mListener.onPopupCallback(mAlarmPopupModel, scenesDataList);
        }
    }

    @Override
    public void onError(String errMsg) {
        setUpdateButtonClickable(true);
        dismissProgressDialog();
        toastShort(errMsg);
    }

    @Override
    public void onProgress(String content, double percent) {
        showProgressDialog(content, percent);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 直接调起相机
                /**
                 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                 *
                 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                 *
                 * 如果实在有所需要，请直接下载源码引用。
                 */
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(9 - selImageList.size());
                final Intent intent = new Intent(mActivity, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT);
                //
                break;
            case 1:
                Intent intent2 = new Intent(mActivity, TakeRecordActivity.class);
//                                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent2, REQUEST_CODE_RECORD);
                break;
            case 2:
                //打开选择,本次允许选择的数量
                //修改选择逻辑
//                                    ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                ImagePicker.getInstance().setSelectLimit(9);
                Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImageList);
                mActivity.startActivityForResult(intent1, REQUEST_CODE_SELECT);
                break;

            default:
                break;
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        clearData();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        clearData();
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @OnClick({R.id.iv_alarm_popup_close, R.id.ll_alarm_popup_alarm_security_risks, R.id.bt_alarm_popup_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_alarm_popup_close:
                exitDialogShow(view);
                break;
            case R.id.ll_alarm_popup_alarm_security_risks:
                //TODO 安全隐患
                Intent intent = new Intent(mActivity, SecurityRisksActivity.class);
                if (mAlarmPopupModel.securityRisksList != null && mAlarmPopupModel.securityRisksList.size() > 0) {
                    intent.putParcelableArrayListExtra(Constants.EXTRA_SECURITY_RISK, mAlarmPopupModel.securityRisksList);
                }
                mActivity.startActivity(intent);
                break;
            case R.id.bt_alarm_popup_commit:
                dismissInputMethodManager(view);
                doAlarmConfirm();
                break;
        }
    }

    public void exitDialogShow(View view) {
        if (mExitDialog != null) {
            mExitDialog.show();
        } else {
            dismissInputMethodManager(view);
            etAlarmPopupRemark.clearFocus();
            etAlarmPopupRemark.getText().clear();
            dismiss();
        }
    }

    public void exitDialogShow() {
        if (mExitDialog != null) {
            mExitDialog.show();
        } else {
            etAlarmPopupRemark.clearFocus();
            etAlarmPopupRemark.getText().clear();
            dismiss();
        }
    }

    public boolean isShowing() {
        return bottomSheetDialog != null && bottomSheetDialog.isShowing();
    }

    public interface OnPopupCallbackListener {
        void onPopupCallback(AlarmPopupModel alarmPopupModel, List<ScenesData> scenesDataList);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_ALARM_POP_IMAGES:
                if (data instanceof AlarmPopModel) {
                    AlarmPopModel alarmPopModel = (AlarmPopModel) data;
                    if (alarmPopModel.resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                        //添加图片返回
                        if (alarmPopModel.requestCode == REQUEST_CODE_SELECT) {
                            if (alarmPopModel.imageItems != null) {
                                adapter.setMaxImgCount(9);
                                if (!alarmPopModel.fromTakePhoto) {
                                    selImageList.clear();
                                }
                                selImageList.addAll(alarmPopModel.imageItems);
                                adapter.setImages(selImageList);
                            }
                        }
                    } else if (alarmPopModel.resultCode == ImagePicker.RESULT_CODE_BACK) {
                        //预览图片返回
                        if (alarmPopModel.requestCode == REQUEST_CODE_PREVIEW) {
                            if (alarmPopModel.imageItems != null) {
                                adapter.setMaxImgCount(9);
                                selImageList.clear();
                                selImageList.addAll(alarmPopModel.imageItems);
                                adapter.setImages(selImageList);
                            }
                        }
                    } else if (alarmPopModel.resultCode == RESULT_CODE_RECORD) {
                        //拍视频
                        if (alarmPopModel.requestCode == REQUEST_CODE_RECORD) {
                            if (alarmPopModel.imageItems != null) {
                                adapter.setMaxImgCount(9);
                                selImageList.addAll(alarmPopModel.imageItems);
                                adapter.setImages(selImageList);
                            }
                        } else if (alarmPopModel.requestCode == REQUEST_CODE_PLAY_RECORD) {
                            adapter.setMaxImgCount(9);
//                        selImageList.clear();
//                        adapter.updateImages(selImageList);
                        }

                    }
                }
                break;
            case Constants.EVENT_DATA_SECURITY_RISK_TAG:
                if (data instanceof ArrayList) {
                    ArrayList<SecurityRisksAdapterModel> securityRisksList = (ArrayList<SecurityRisksAdapterModel>) data;
                    //分析list
                    String securityRisksText = AlarmPopupConfigAnalyzer.getSecurityRisksText(securityRisksList);
                    if (TextUtils.isEmpty(securityRisksText)) {
                        tvAlarmPopupAlarmSecurityRisksContent.setText(R.string.text_unfilled);
                    } else {
                        tvAlarmPopupAlarmSecurityRisksContent.setText(securityRisksText);
                        mAlarmPopupModel.securityRisksList = securityRisksList;
                    }
                }
                break;
        }
    }

    public void setUpdateButtonClickable(boolean canClick) {
        if (btAlarmPopupCommit != null) {
            if (canClick) {
                btAlarmPopupCommit.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_button));
            } else {
                btAlarmPopupCommit.setBackground(mActivity.getResources().getDrawable(R.drawable.shape_button_normal));
            }
            btAlarmPopupCommit.setEnabled(canClick);
            btAlarmPopupCommit.setClickable(canClick);
        }
    }

    public static void handlePhotoIntent(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (tempImages != null) {
                    boolean fromTakePhoto = data.getBooleanExtra(EXTRA_RESULT_BY_TAKE_PHOTO, false);
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.fromTakePhoto = fromTakePhoto;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (requestCode == REQUEST_CODE_PREVIEW && data != null) {
                ArrayList<ImageItem> tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (tempImages != null) {
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            }
        } else if (resultCode == RESULT_CODE_RECORD) {
            //拍视频
            if (data != null && requestCode == REQUEST_CODE_RECORD) {
                ImageItem imageItem = (ImageItem) data.getSerializableExtra(Constants.EXTRA_PATH_RECORD);
                if (imageItem != null) {
                    try {
                        LogUtils.loge("--- 从视频返回  path = " + imageItem.path);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    ArrayList<ImageItem> tempImages = new ArrayList<>();
                    tempImages.add(imageItem);
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                    AlarmPopModel alarmPopModel = new AlarmPopModel();
                    alarmPopModel.requestCode = requestCode;
                    alarmPopModel.resultCode = resultCode;
                    alarmPopModel.imageItems = tempImages;
                    eventData.data = alarmPopModel;
                    EventBus.getDefault().post(eventData);
                }
            } else if (requestCode == REQUEST_CODE_PLAY_RECORD) {
                EventData eventData = new EventData();
                eventData.code = EVENT_DATA_ALARM_POP_IMAGES;
                AlarmPopModel alarmPopModel = new AlarmPopModel();
                alarmPopModel.requestCode = requestCode;
                alarmPopModel.resultCode = resultCode;
                eventData.data = alarmPopModel;
                EventBus.getDefault().post(eventData);
            }

        }
        //
        try {
            LogUtils.loge("handlerActivityResult requestCode = " + requestCode + ",resultCode = " + resultCode + ",data = " + data);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
