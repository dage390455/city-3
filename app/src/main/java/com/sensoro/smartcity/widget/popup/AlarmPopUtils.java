package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.adapter.NothingSelectedSpinnerAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.lzy.imagepicker.ImagePicker.EXTRA_RESULT_BY_TAKE_PHOTO;

public class AlarmPopUtils implements View.OnClickListener, Constants,
        ImagePickerAdapter.OnRecyclerViewItemClickListener, UpLoadPhotosUtils.UpLoadPhotoListener, AdapterView
                .OnItemSelectedListener, SelectDialog.SelectDialogListener, DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    private final FixHeightBottomSheetDialog bottomSheetDialog;
    private Activity mContext;
    private final View mRoot;

    public AlarmPopUtils(Activity context) {
        mContext = context;
        bottomSheetDialog = new FixHeightBottomSheetDialog(context);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setOnDismissListener(this);
        bottomSheetDialog.setOnCancelListener(this);
        mRoot = View.inflate(mContext, R.layout.layout_alarm_popup, null);
        intData();
        init();
        initWidget();
        //以下设置是为了解决：下滑隐藏dialog后，再次调用show方法显示时，不能弹出Dialog----在真机测试时不写下面的方法也未发现问题
        View delegateView = bottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
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
                System.out.println("onSlide = [" + bottomSheet + "], slideOffset = [" + slideOffset + "]");
            }
        });

    }


    //
    private OnPopupCallbackListener mListener;
    private EditText remarkEditText;
    private Button mButton;
    private final List<String> alarmType = new ArrayList<String>();
    private final List<String> alarmPlace = new ArrayList<String>();
    private final List<String> alarmResult = new ArrayList<String>();
    private final List<String> alarmResultInfo = new ArrayList<String>();
    //
    private final int[] resultArr = {-1, 1, 4, 2, 3};
    private final int[] typeArr = {-1, 1, 2, 3, 4, 5, 6, 7, 8, 0};
    private final int[] placeArr = {-1, 1, 7, 2, 3, 4, 5, 6, 0};
    private int selectType;
    private int selectPlace;
    private int selectResult;
    //
    private Spinner spinnerType;
    private Spinner spinnerPlace;
    private Spinner spinnerResult;
    //

    private ImagePickerAdapter adapter;
    private final ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数
    private ArrayList<ImageItem> tempImages = null;
    private Activity mActivity;
    private TextView tvSpinnerResultInfo;
    private ProgressDialog progressDialog;
    private UpLoadPhotosUtils upLoadPhotosUtils;
    private String mRemark;


    public void onDestroyPop() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.cancel();
        }
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        if (tempImages != null) {
            tempImages.clear();
            tempImages = null;
        }
        selImageList.clear();
    }


    private void initWidget() {
        RecyclerView recyclerView = mRoot.findViewById(R.id.recyclerView);
        adapter = new ImagePickerAdapter(mContext, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        adapter.canVideo(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
        recyclerView.setNestedScrollingEnabled(false);
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(mActivity, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    private void showProgressDialogByPhoto(int count, double percent) {
        if (progressDialog != null) {
            if (count == -1) {
                String title = "正在上传视频";
                progressDialog.setProgress((int) (percent * 100));
                progressDialog.setTitle(title);
                progressDialog.show();
            } else {
                String title = "正在上传第" + count + "张，总共" + selImageList.size() + "张";
                progressDialog.setProgress((int) (percent * 100));
                progressDialog.setTitle(title);
                progressDialog.show();
            }

        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void setDialog(Activity activity) {
        mActivity = activity;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat("");
        progressDialog.setCancelable(false);
    }

    private void intData() {
        alarmResult.add("真实火警");
        alarmResult.add("安全隐患");
        alarmResult.add("误报");
        alarmResult.add("测试/巡检");
        //
        alarmResultInfo.add("");
        alarmResultInfo.add("*监测点或附近发生着火，需要立即进行扑救");
        alarmResultInfo.add("*未发生着火，但现场确实存在隐患");
        alarmResultInfo.add("*无任何火情和烟雾");
        alarmResultInfo.add("*相关人员主动测试发出的预警");
        //
        alarmType.add("用电异常");
        alarmType.add("生产作业");
        alarmType.add("吸烟");
        alarmType.add("室内生火");
        alarmType.add("烹饪");
        alarmType.add("燃气泄漏");
        alarmType.add("人为放火");
        alarmType.add("易燃物自燃");
        alarmType.add("其他");
        //
        alarmPlace.add("小区");
        alarmPlace.add("出租房");
        alarmPlace.add("工厂");
        alarmPlace.add("居民作坊");
        alarmPlace.add("仓库");
        alarmPlace.add("商铺店面");
        alarmPlace.add("商场");
        alarmPlace.add("其他");
    }

    private void init() {
        tvSpinnerResultInfo = mRoot.findViewById(R.id.tv_spinner_result_info);
        //
        ImageView closeImageView = mRoot.findViewById(R.id.alarm_popup_close);
        remarkEditText = mRoot.findViewById(R.id.alarm_popup_remark);
        mButton = mRoot.findViewById(R.id.alarm_popup_commit);
        //
        spinnerResult = mRoot.findViewById(R.id.spinner_result);
        spinnerType = mRoot.findViewById(R.id.spinner_type);
        spinnerPlace = mRoot.findViewById(R.id.spinner_place);
        //
        mButton.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        //
        ArrayAdapter<String> alarmResultAdapter = new ArrayAdapter<String>(mContext, android.R.layout
                .simple_spinner_item, alarmResult);
        ArrayAdapter<String> alarmTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout
                .simple_spinner_item, alarmType);
        ArrayAdapter<String> alarmPlaceAdapter = new ArrayAdapter<String>(mContext, android.R.layout
                .simple_spinner_item, alarmPlace);
        //
        spinnerResult.setAdapter(new NothingSelectedSpinnerAdapter(alarmResultAdapter, R.layout
                .spinner_nothing_selected_result, mContext));
        spinnerType.setAdapter(new NothingSelectedSpinnerAdapter(alarmTypeAdapter, R.layout
                .spinner_nothing_selected_type, mContext));
        spinnerPlace.setAdapter(new NothingSelectedSpinnerAdapter(alarmPlaceAdapter, R.layout
                .spinner_nothing_selected_place, mContext));
        //
        alarmTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarmPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarmResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        //
        spinnerResult.setOnItemSelectedListener(this);
        spinnerType.setOnItemSelectedListener(this);
        spinnerPlace.setOnItemSelectedListener(this);
        //
        upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, this);
        bottomSheetDialog.setContentView(mRoot);
    }

    public void show() {
        //
        spinnerResult.setSelection(0, false);
        spinnerType.setSelection(0, false);
        spinnerPlace.setSelection(0, false);
        //
        this.remarkEditText.getText().clear();
        mRemark = null;
//        mButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        mButton.setBackground(mContext.getResources().getDrawable(R.drawable.shape_button));
        setUpdateButtonClickable(true);
        if (progressDialog != null) {
            progressDialog.setProgress(0);
        }
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
        }
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
        selImageList.clear();
        adapter.setImages(selImageList);
        setUpdateButtonClickable(false);
    }

    private void doAlarmConfirm() {
        setUpdateButtonClickable(false);
        mRemark = remarkEditText.getText().toString();
        if (selectResult == -1) {
            toastShort("请选择预警结果类型");
            setUpdateButtonClickable(true);
            return;
        }
        if (selectType == -1) {
            toastShort("请选择预警成因类型");
            setUpdateButtonClickable(true);
            return;
        }
        if (selectPlace == -1) {
            toastShort("请选择预警场所");
            setUpdateButtonClickable(true);
            return;
        }
        if (mListener != null) {
            if (selImageList.size() > 0) {
                upLoadPhotosUtils.doUploadPhoto(selImageList);
            } else {
                //
                dismissProgressDialog();
                mListener.onPopupCallback(selectResult, selectType, selectPlace, null, mRemark);
            }
//
        }
    }

    private void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_popup_close:
                dismissInputMethodManager(v);
                remarkEditText.clearFocus();
                this.remarkEditText.getText().clear();
                dismiss();
                break;
            case R.id.alarm_popup_commit:
                dismissInputMethodManager(v);
                doAlarmConfirm();
                break;
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
//            adapter.notifyItemRemoved(position);
            adapter.setImages(selImageList);
//            updateButton();
        } else if (IMAGE_ITEM_ADD == position) {
            List<String> names = new ArrayList<>();
            names.add("拍照");
            names.add("相册");
            boolean needRecord = true;
            for (ImageItem imageItem : selImageList) {
                if (!imageItem.isRecord) {
                    //只要有一个是照片
                    needRecord = false;
                    break;
                }
            }
            if (needRecord) {
                names.add("拍摄视频");
            }
            showDialog(this, names);
        } else {
            //打开预览
            ImageItem imageItem = selImageList.get(position);
            if (imageItem.isRecord) {
                Intent intent = new Intent();
                intent.setClass(mActivity, VideoPlayActivity.class);
                intent.putExtra("path_record", (Serializable) imageItem);
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
        progressDialog.setTitle("请稍后");
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    public void onComplete(List<ScenesData> scenesDataList) {
        StringBuilder s = new StringBuilder();
        for (ScenesData scenesData : scenesDataList) {
            s.append(scenesData.url).append("\n");
        }
        dismissProgressDialog();
//        toastShort("上传成功---");
        LogUtils.loge(this, "上传成功---" + s);
        //TODO 上传结果
        mListener.onPopupCallback(selectResult, selectType, selectPlace, scenesDataList, mRemark);
    }

    @Override
    public void onError(String errMsg) {
        setUpdateButtonClickable(true);
        dismissProgressDialog();
        toastShort(errMsg);
    }

    @Override
    public void onProgress(int index, double percent) {
        showProgressDialogByPhoto(index, percent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
        switch (parent.getId()) {
            case R.id.spinner_result:
                tvSpinnerResultInfo.setText(alarmResultInfo.get(position));
                selectResult = resultArr[position];
                LogUtils.loge("结果类型：" + selectResult);
                break;
            case R.id.spinner_type:
                selectType = typeArr[position];
                LogUtils.loge("成因类型：" + selectType);
                break;
            case R.id.spinner_place:
                selectPlace = placeArr[position];
                LogUtils.loge("场所类型：" + selectPlace);
                break;
            default:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent = new Intent(mActivity, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            case 1:
                //打开选择,本次允许选择的数量
                //修改选择逻辑
//                                    ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                ImagePicker.getInstance().setSelectLimit(maxImgCount);
                Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImageList);
                mActivity.startActivityForResult(intent1, REQUEST_CODE_SELECT);
                break;
            case 2:
                Intent intent2 = new Intent(mActivity, TakeRecordActivity.class);
//                                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mActivity.startActivityForResult(intent2, REQUEST_CODE_RECORD);
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

    public interface OnPopupCallbackListener {
        void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark);
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.loge("handlerActivityResult requestCode = " + requestCode + ",resultCode = " + resultCode + ",data = " + data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (tempImages != null) {
                    adapter.setMaxImgCount(9);
                    boolean fromTakePhoto = data.getBooleanExtra(EXTRA_RESULT_BY_TAKE_PHOTO, false);
                    if (!fromTakePhoto) {
                        selImageList.clear();
                    }
                    selImageList.addAll(tempImages);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (requestCode == REQUEST_CODE_PREVIEW && data != null) {
                tempImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (tempImages != null) {
                    adapter.setMaxImgCount(9);
                    selImageList.clear();
                    selImageList.addAll(tempImages);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == RESULT_CODE_RECORD) {
            //拍视频
            if (data != null && requestCode == REQUEST_CODE_RECORD) {
                ImageItem imageItem = (ImageItem) data.getSerializableExtra("path_record");
                if (imageItem != null) {
                    LogUtils.loge("--- 从视频返回  path = " + imageItem.recordPath);
                    adapter.setMaxImgCount(1);
                    selImageList.clear();
                    selImageList.add(imageItem);
                    adapter.setImages(selImageList);
                }
            } else if (requestCode == REQUEST_CODE_PLAY_RECORD) {
                adapter.setMaxImgCount(9);
                selImageList.clear();
                adapter.setImages(selImageList);
            }

        }
    }

    public void setUpdateButtonClickable(boolean canClick) {
        if (canClick) {
            mButton.setBackground(mContext.getResources().getDrawable(R.drawable.shape_button));
        } else {
            mButton.setBackground(mContext.getResources().getDrawable(R.drawable.shape_button_normal));
        }
        mButton.setEnabled(canClick);
        mButton.setClickable(canClick);
    }
}
