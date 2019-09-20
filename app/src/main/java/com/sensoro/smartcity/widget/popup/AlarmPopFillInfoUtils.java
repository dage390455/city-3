package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sensoro.common.adapter.ImagePickerAdapter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.SelectDialog;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.dialog.TipDialogUtils;
import com.sensoro.common.widgets.slideverify.SlidePopUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlarmPopupMainTagAdapter;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.model.AlarmPopupModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AlarmPopFillInfoUtils implements Constants,
        ImagePickerAdapter.OnRecyclerViewItemClickListener, UpLoadPhotosUtils.UpLoadPhotoListener,
        SelectDialog.SelectDialogListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
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


    @BindView(R.id.et_alarm_popup_remark)
    EditText etAlarmPopupRemark;

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
    private AlarmPopupMainTagAdapter alarmPopupMainTagAdapter;
    private ProgressDialog progressDialog;
    private AlarmPopupModel mAlarmPopupModel;
    private TipDialogUtils mRealFireDialog;
    private TipDialogUtils mExitDialog;

    public AlarmPopFillInfoUtils(Activity activity) {
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

        if (mSlidePopUtils != null) {
            mSlidePopUtils.destroySlideVerifyDialog();
            mSlidePopUtils = null;
        }

        if (bind != null) {
            bind.unbind();
            bind = null;
        }
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
        mRoot = View.inflate(mActivity, R.layout.layout_alarm_popup_fillinfo, null);
        bind = ButterKnife.bind(this, mRoot);
        bottomSheetDialog = new FixHeightBottomSheetDialog(mActivity);
        //
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.setOnDismissListener(this);
        bottomSheetDialog.setOnCancelListener(this);

        //
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
                                    btAlarmPopupCommit.setBackground(mActivity.getResources().getDrawable(mAlarmPopupModel.resButtonBg));
                                }
                            });
                        }
                    });

                }
            }
        });
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

    private SlidePopUtils mSlidePopUtils;

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
                        mSlidePopUtils.dismissDialog();
                        doCommit();
                    }

                    @Override
                    public void onFailed(int failCount) {
                        mSlidePopUtils.dismissDialog();
                        SensoroToast.getInstance().makeText(mActivity, mActivity.getResources().getString(R.string.slide_dialog_failed), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMaxFailed() {
                        mSlidePopUtils.dismissDialog();
                        SensoroToast.getInstance().makeText(mActivity, mActivity.getResources().getString(R.string.slide_dialog_failed_maxcount), Toast.LENGTH_SHORT).show();
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
        if (mAlarmPopupModel != null) {
            mAlarmPopupModel.mRemark = null;
        }
        setUpdateButtonClickable(false);
    }

    private void doAlarmConfirm() {
        if (!AlarmPopupConfigAnalyzer.checkAlarmCanGoOnNext(mAlarmPopupModel)) {
            toastShort(mActivity.getString(R.string.please_complete_the_required_fields_first));
            return;
        }
        if (mAlarmPopupModel.resButtonBg == R.drawable.shape_button_alarm_pup) {
//            mRealFireDialog.show();
            mSlidePopUtils.showDialog(mActivity);
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


            dismissProgressDialog();
            mListener.onPopupCallback(mAlarmPopupModel, null);
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

    @OnClick({R.id.iv_alarm_popup_close,  R.id.bt_alarm_popup_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_alarm_popup_close:
                exitDialogShow(view);
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
}
