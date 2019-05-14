package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.activity.TakeRecordActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionUploadExceptionActivityView;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmPopModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageGridActivity;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.popup.UpLoadPhotosUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InspectionUploadExceptionActivityPresenter extends BasePresenter<IInspectionUploadExceptionActivityView>
        implements View.OnClickListener, IOnCreate, Constants, SelectDialog.SelectDialogListener, UpLoadPhotosUtils.UpLoadPhotoListener {
    private Activity mContext;
    private List<String> exceptionTags = new ArrayList<>();
    private long startTime;
    public final ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private static final int maxImgCount = 9;
    private InspectionTaskDeviceDetail mDeviceDetail;
    private ArrayList<ImageItem> tempImages = null;
    private UpLoadPhotosUtils upLoadPhotosUtils;
    private boolean needChangeDevice = false;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, this);
        initExceptionTag();
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL);
        startTime = mContext.getIntent().getLongExtra(EXTRA_INSPECTION_START_TIME, 0);
    }

    private void initExceptionTag() {
        exceptionTags.clear();
        for (int resId : INSPECTION_EXCEPTION_TAGS) {
            exceptionTags.add(mContext.getString(resId));
        }
//        Collections.addAll(exceptionTags, INSPECTION_EXCEPTION_TAGS);
        getView().updateExceptionTagAdapter(exceptionTags);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (tempImages != null) {
            tempImages.clear();
            tempImages = null;
        }
        selImageList.clear();
    }

    public void clickItem(int viewId, int position, List<ImageItem> images) {
        if (viewId == R.id.image_delete) {
            ImageItem imageItem = selImageList.get(position);
            Iterator<ImageItem> iterator = selImageList.iterator();
            while (iterator.hasNext()) {
                ImageItem next = iterator.next();
                if (next.equals(imageItem)) {
                    iterator.remove();
                    break;
                }
            }
            getView().updateImageList(selImageList);
//            updateButton();
        } else if (IMAGE_ITEM_ADD == position) {
            List<String> names = new ArrayList<>();
            names.add(mContext.getString(R.string.take_photo));
//            names.add("相册");
            names.add(mContext.getString(R.string.shooting_video));
            getView().showDialog(this, names);
        } else {
            //打开预览
            ImageItem imageItem = selImageList.get(position);
            if (imageItem.isRecord) {
                Intent intent = new Intent();
                intent.setClass(mContext, VideoPlayActivity.class);
                intent.putExtra("path_record", (Serializable) imageItem);
                intent.putExtra("video_del",true);
                mContext.startActivityForResult(intent, REQUEST_CODE_PLAY_RECORD);
            } else {
                Intent intentPreview = new Intent(mContext, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, selImageList);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                mContext.startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
            }

        }
    }

    /**
     * dialog点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_tv_exception:
                needChangeDevice = false;
                getView().dismissExceptionDialog();
                doException();
                break;
            case R.id.dialog_tv_upload_change_device:
                needChangeDevice = true;
                getView().dismissExceptionDialog();
                doException();
                break;
            case R.id.dialog_tv_waite:
                getView().dismissExceptionDialog();
                break;
        }
    }

    private void doException() {
        getView().initUploadProgressDialog();
        upLoadPhotosUtils.doUploadPhoto(selImageList);
    }


    public List<ImageItem> getSelImageList() {
        return selImageList;
    }

    private void doUploadAndChange() {
        //TODO 更换设备 上传异常
        Intent intent = new Intent(mContext, ScanActivity.class);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE);
        intent.putExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO, mDeviceDetail);
        getView().startAC(intent);
    }

    private void doUploadInspectionException(List<ScenesData> scenesDataList) {
        long finishTime = System.currentTimeMillis();
        List<Integer> selectTags = getView().getSelectTags();
        String remarkMessage = getView().getRemarkMessage();
        if (selectTags.size() == 0) {
            getView().toastShort(mContext.getString(R.string.must_select_a_tag_type));
            return;
        }
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().doUploadInspectionResult(mDeviceDetail.getId(), null, null, 2, 0, startTime, finishTime, remarkMessage,
                scenesDataList, selectTags).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseBase>(this) {
                    @Override
                    public void onCompleted(ResponseBase responseBase) {
                        if (responseBase.getErrcode() == 0) {
                            if (needChangeDevice) {
                                doUploadAndChange();
                            } else {
                                getView().toastShort(mContext.getString(R.string.abnormal_reporting_success));
                                EventData eventData = new EventData();
                                eventData.code = EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE;
                                EventBus.getDefault().post(eventData);
                                getView().finishAc();
                            }

                        } else {
                            getView().toastShort(mContext.getString(R.string.abnormal_reporting_failure));
                        }
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().toastShort(errorMsg);
                        getView().dismissProgressDialog();
                    }
                });
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        AlarmPopUtils.handlePhotoIntent(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        if (code == EVENT_DATA_ALARM_POP_IMAGES) {
            if (data instanceof AlarmPopModel) {
                AlarmPopModel alarmPopModel = (AlarmPopModel) data;
                if (alarmPopModel.resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    //添加图片返回
                    if (alarmPopModel.requestCode == REQUEST_CODE_SELECT) {
                        if (alarmPopModel.imageItems != null) {
//                            adapter.setMaxImgCount(9);
                            if (!alarmPopModel.fromTakePhoto) {
                                selImageList.clear();
                            }
                            selImageList.addAll(alarmPopModel.imageItems);
//                            adapter.updateImages(selImageList);
                            getView().updateImageList(selImageList);
                        }
                    }
                } else if (alarmPopModel.resultCode == ImagePicker.RESULT_CODE_BACK) {
                    //预览图片返回
                    if (alarmPopModel.requestCode == REQUEST_CODE_PREVIEW) {
                        if (alarmPopModel.imageItems != null) {
//                            adapter.setMaxImgCount(9);
                            selImageList.clear();
                            selImageList.addAll(alarmPopModel.imageItems);
//                            adapter.updateImages(selImageList);
                            getView().updateImageList(selImageList);
                        }
                    }
                } else if (alarmPopModel.resultCode == RESULT_CODE_RECORD) {
                    //拍视频
                    if (alarmPopModel.requestCode == REQUEST_CODE_RECORD) {
                        if (alarmPopModel.imageItems != null) {
//                            adapter.setMaxImgCount(9);
                            selImageList.addAll(alarmPopModel.imageItems);
//                            adapter.updateImages(selImageList);
                            getView().updateImageList(selImageList);
                        }
                    } else if (alarmPopModel.requestCode == REQUEST_CODE_PLAY_RECORD) {
//                        adapter.setMaxImgCount(9);
//                        selImageList.clear();
//                        adapter.updateImages(selImageList);
                    }

                }
            }
        } else if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            getView().finishAc();
        } else if (code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            getView().finishAc();
        }
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
                Intent intent = new Intent(mContext, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mContext.startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
//            case 1:
//                //打开选择,本次允许选择的数量
//                //修改选择逻辑
////              ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
//                ImagePicker.getInstance().setSelectLimit(maxImgCount);
//                Intent intent1 = new Intent(mContext, ImageGridActivity.class);
//                /* 如果需要进入选择的时候显示已经选中的图片，
//                 * 详情请查看ImagePickerActivity
//                 * */
//                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES, selImageList);
//                mContext.startActivityForResult(intent1, REQUEST_CODE_SELECT);
//                break;
            case 1:
                Intent intent2 = new Intent(mContext, TakeRecordActivity.class);
//                                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                mContext.startActivityForResult(intent2, REQUEST_CODE_RECORD);
                break;
            default:
                break;
        }

    }

    @Override
    public void onStart() {
        if (isAttachedView()) {
            getView().showUploadProgressDialog(mContext.getString(R.string.please_wait), 0);
        }

    }

    @Override
    public void onComplete(List<ScenesData> scenesDataList) {
        StringBuilder s = new StringBuilder();
        for (ScenesData scenesData : scenesDataList) {
            s.append(scenesData.url).append("\n");
        }
        try {
            LogUtils.loge(this, "上传成功---" + s);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (isAttachedView()) {
            getView().dismissUploadProgressDialog();
//        toastShort("上传成功---");
            // 上传结果
            doUploadInspectionException(scenesDataList);
//        mListener.onPopupCallback(selectResult, selectType, selectPlace, scenesDataList, mRemark);
        }

    }

    @Override
    public void onError(String errMsg) {
//        setUpdateButtonClickable(true);
        if (isAttachedView()) {
            getView().dismissUploadProgressDialog();
            getView().toastShort(errMsg);
        }

    }

    @Override
    public void onProgress(String content, double percent) {
        if (isAttachedView()) {
            getView().showUploadProgressDialog(content, percent);
        }

    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
