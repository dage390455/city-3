package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sensoro on 17/11/14.
 */

public class SensoroPopupAlarmViewNew extends LinearLayout implements View.OnClickListener, Constants,
        ImagePickerAdapter.OnRecyclerViewItemClickListener, UpLoadPhotosUtils.UpLoadPhotoListener, AdapterView
                .OnItemSelectedListener {
    private Context mContext;
    private OnPopupCallbackListener mListener;
    private Animation showAnimation;
    private Animation dismissAnimation;
    private View mView;
    private SensoroShadowView mShadowView;
    private ImageView closeImageView;
    private EditText remarkEditText;
    private Button mButton;
    private final List<String> alarmType = new ArrayList<String>();
    private final List<String> alarmPlace = new ArrayList<String>();
    private final List<String> alarmResult = new ArrayList<String>();
    private final List<String> alarmResultInfo = new ArrayList<String>();
    //
    private int selectType;
    private int selectPlace;
    private int selectResult;
    //
    private Spinner spinnerType;
    private Spinner spinnerPlace;
    private Spinner spinnerResult;
    //
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数
    ArrayList<ImageItem> images = null;
    private Activity mActivity;
    private TextView tvSpinnerResultInfo;
    private ProgressDialog progressDialog;
    private UpLoadPhotosUtils upLoadPhotosUtils;
    private String mRemark;

    public SensoroPopupAlarmViewNew(Context context) {
        super(context);
        this.mContext = context;
    }

    public void onDestroyPop() {
        if (showAnimation != null) {
            showAnimation.cancel();
        }
        if (dismissAnimation != null) {
            dismissAnimation.cancel();
        }
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    public SensoroPopupAlarmViewNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        intData();
        init();
        initWidget();
    }

    public SensoroPopupAlarmViewNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void initWidget() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(mContext, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
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

    private void showProgressDialog(int count, double percent) {
        if (progressDialog != null) {
            String title = "正在上传第" + count + "张，总共" + selImageList.size() + "张";
            progressDialog.setProgress((int) (percent * 100));
            progressDialog.setTitle(title);
            progressDialog.show();
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
    }

    private void intData() {
        alarmResult.add("真实预警");
        alarmResult.add("安全隐患");
        alarmResult.add("误报");
        alarmResult.add("巡检测试");
        //
        alarmResultInfo.add("*监测点或附近发生着火，需要立即进行扑救");
        alarmResultInfo.add("*未发生着火，但现场确实存在隐患");
        alarmResultInfo.add("*无任何火情和烟雾");
        alarmResultInfo.add("*相关人员主动测试出发的预警");
        //
        alarmType.add("用电异常");
        alarmType.add("生产作业");
        alarmType.add("吸烟");
        alarmType.add("室内生火");
        alarmType.add("烹饪");
        alarmType.add("燃气泄漏");
        alarmType.add("人为放火");
        alarmType.add("易燃物自爆");
        alarmType.add("其他");
        //
        alarmPlace.add("老旧小区");
        alarmPlace.add("工厂");
        alarmPlace.add("居民作坊");
        alarmPlace.add("仓库");
        alarmPlace.add("商铺店面");
        alarmPlace.add("商场");
        alarmPlace.add("其他");
    }

    private void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_alarm_popup_new, this);
        tvSpinnerResultInfo = (TextView) mView.findViewById(R.id.tv_spinner_result_info);
        //
        closeImageView = (ImageView) mView.findViewById(R.id.alarm_popup_close);
        remarkEditText = (EditText) mView.findViewById(R.id.alarm_popup_remark);
        mButton = (Button) mView.findViewById(R.id.alarm_popup_commit);
        //
        spinnerResult = mView.findViewById(R.id.spinner_result);
        spinnerType = mView.findViewById(R.id.spinner_type);
        spinnerPlace = mView.findViewById(R.id.spinner_place);
        //
        mButton.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        //
        ArrayAdapter<String> alarmTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout
                .simple_spinner_item, alarmType);
        alarmTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> alarmPlaceAdapter = new ArrayAdapter<String>(mContext, android.R.layout
                .simple_spinner_item, alarmPlace);
        alarmPlaceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> alarmResultAdapter = new ArrayAdapter<String>(mContext, android.R.layout
                .simple_spinner_item, alarmResult);
        alarmResultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        spinnerResult.setAdapter(alarmResultAdapter);
        spinnerType.setAdapter(alarmTypeAdapter);
        spinnerPlace.setAdapter(alarmPlaceAdapter);
        //
        spinnerResult.setOnItemSelectedListener(this);
        spinnerType.setOnItemSelectedListener(this);
        spinnerPlace.setOnItemSelectedListener(this);
        //
        showAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alarm_fadein);
        //得到一个LayoutAnimationController对象；
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dismissAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alarm_fadeout);
        //得到一个LayoutAnimationController对象；
        dismissAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mShadowView != null) {
                    mShadowView.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, this);

    }

    public void show(SensoroShadowView shadowView) {
        this.mShadowView = shadowView;
        this.mShadowView.setVisibility(VISIBLE);
        //
        spinnerResult.setSelection(0, false);
        spinnerType.setSelection(0, false);
        spinnerPlace.setSelection(0, false);
        //
        selectResult = 0;
        selectType = 0;
        selectPlace = 0;
        this.remarkEditText.setText("");
//        mButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        mButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
        setVisibility(View.VISIBLE);
        this.startAnimation(showAnimation);
    }

    public void setOnPopupCallbackListener(OnPopupCallbackListener listener) {
        this.mListener = listener;
    }

    public void dismiss() {
        if (this.getVisibility() == VISIBLE) {
            this.startAnimation(dismissAnimation);
            if (selImageList != null) {
                selImageList.clear();
                adapter.setImages(selImageList);
            }
        }
    }

    private void doAlarmConfirm() {
        mRemark = remarkEditText.getText().toString();
        if (mListener != null) {
            if (selImageList.size() > 0) {
                upLoadPhotosUtils.doUploadPhoto(selImageList);
            } else {
                //
//                mListener.onPopupCallback(displayStatus, mRemark);
            }
//
        }
    }

    private void toastShort(String msg) {
        SensoroToast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_popup_close:
                dismissInputMethodManager(v);
                remarkEditText.clearFocus();
                dismiss();
                break;
            case R.id.alarm_popup_commit:
//                if (displayStatus != DISPLAY_STATUS_CONFIRM) {
                dismissInputMethodManager(v);
//                    remarkEditText.clearFocus();
                doAlarmConfirm();
//                } else {
//                    toastShort(mContext.getResources().getString(R.string.tips_choose_status));
//                }
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
            adapter.setImages(selImageList);
//            updateButton();
        } else {
            switch (position) {
                case IMAGE_ITEM_ADD:
                    List<String> names = new ArrayList<>();
                    names.add("拍照");
                    names.add("相册");
                    showDialog(new SelectDialog.SelectDialogListener() {
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
                                    ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                                    Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
                                    /* 如果需要进入选择的时候显示已经选中的图片，
                                     * 详情请查看ImagePickerActivity
                                     * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                                    mActivity.startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                    break;
                                default:
                                    break;
                            }

                        }
                    }, names);


                    break;
                default:
                    //打开预览
                    Intent intentPreview = new Intent(mActivity, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    mActivity.startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                    break;
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
    public void onComplete(List<String> imagesUrl) {
        String s = "";
        for (String temp : imagesUrl) {
            s += temp + "\n";
        }
        dismissProgressDialog();
        toastShort("上传成功---");
        LogUtils.loge(this, "上传成功---" + s);
        //TODO 上传结果
//        mListener.onPopupCallback(displayStatus, remark);
    }

    @Override
    public void onError(String errMsg) {
        dismissProgressDialog();
        toastShort(errMsg);
    }

    @Override
    public void onProgress(int index, double percent) {
        showProgressDialog(index, percent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
        switch (parent.getId()) {
            case R.id.spinner_result:
                tvSpinnerResultInfo.setText(alarmResultInfo.get(position));
                selectResult = position;
                break;
            case R.id.spinner_type:
                selectType = position;
                break;
            case R.id.spinner_place:
                selectPlace = position;
                break;
            default:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnPopupCallbackListener {
        void onPopupCallback(int status, String remark);
    }

    public interface OnPopupCallbackListenerd {
        void onPopupCallback(int status, String remark);
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }
    }

    private void updateButton() {
        if (selImageList.size() > 0) {
            mButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
        } else {
            mButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        }
    }

}
