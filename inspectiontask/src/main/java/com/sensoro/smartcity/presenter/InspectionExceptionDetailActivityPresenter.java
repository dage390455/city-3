package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.common.server.bean.InspectionTaskExceptionDeviceModel;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.inspectiontask.R;
import com.sensoro.smartcity.imainviews.IInspectionExceptionDetailActivityView;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageAlarmPhotoDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InspectionExceptionDetailActivityPresenter extends BasePresenter<IInspectionExceptionDetailActivityView> {
    private Activity mContext;
    private InspectionTaskDeviceDetail mDeviceDetail;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(Constants.EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL);
        requestExceptionDetail();
    }

    private void requestExceptionDetail() {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getInspectionDeviceDetail(mDeviceDetail.getId(), null, null, 1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<InspectionTaskExceptionDeviceModel>>(this) {
            @Override
            public void onCompleted(ResponseResult<InspectionTaskExceptionDeviceModel> response) {
                InspectionTaskExceptionDeviceModel taskDevice = response.getData();
                getView().setTvName(taskDevice.getDevice().getName());
                String inspectionDeviceName = WidgetUtil.getInspectionDeviceName(taskDevice.getDeviceType());
                getView().setTvSn(inspectionDeviceName + " " + taskDevice.getSn());
                getView().updateTagsData(taskDevice.getDevice().getTags());

                switch (taskDevice.getStatus()) {
                    case 1:
                        getView().setTvStatus(R.color.c_1dbb99, mContext.getString(R.string.normal_inspection));
                        break;
                    case 2:
                        getView().setTvStatus(R.color.c_ff8d34, mContext.getString(R.string.inspection_abnormality));
                        break;
                    default:
                        getView().setTvStatus(R.color.c_a6a6a6, mContext.getString(R.string.not_inspected));
                        break;
                }

                getView().updateExceptionTagsData(taskDevice.getMalfunctions());

                getView().setTvRemark(taskDevice.getRemark());

                List<ScenesData> imgAndVedios = taskDevice.getImgAndVedio();
                ArrayList<ScenesData> images = new ArrayList<>();
                ArrayList<ScenesData> videoThumbs = new ArrayList<>();
                for (ScenesData imgAndVedio : imgAndVedios) {
                    if ("image".equals(imgAndVedio.type)) {
                        //图片资源的话后台没有thumb，所以是直接给url
                        images.add(imgAndVedio);
                    } else if ("video".equals(imgAndVedio.type)) {
                        videoThumbs.add(imgAndVedio);
                    }
                }

                getView().updateRcPhotoAdapter(images);
                getView().updateRcCameraAdapter(videoThumbs);

                getView().dismissProgressDialog();

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    @Override
    public void onDestroy() {

    }

    public void doPreviewPhoto(List<ScenesData> dataList, int position) {
        ArrayList<ImageItem> imgs = new ArrayList<>();
        for (ScenesData scenesData : dataList) {
            ImageItem imageItem = new ImageItem();
            imageItem.fromUrl = true;
            imageItem.path = scenesData.url;
            imgs.add(imageItem);
        }
        Intent intentPreview = new Intent(mContext, ImageAlarmPhotoDetailActivity.class);
        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imgs);
        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
        intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
        getView().startAC(intentPreview);


    }

    public void doPreviewCamera(ScenesData item) {
//        ImageItem imageItem = new ImageItem();
//        imageItem.isRecord = true;
//        imageItem.thumbPath = item.thumbUrl;
//        imageItem.path = item.url;
//        Intent intent = new Intent();
//        intent.setClass(mContext, VideoPlayActivity.class);
//        intent.putExtra("path_record", (Serializable) imageItem);
//        intent.putExtra("video_del", true);
//        getView().startAC(intent);

        ImageItem imageItem = new ImageItem();
        imageItem.isRecord = true;
        imageItem.thumbPath = item.thumbUrl;
        imageItem.path = item.url;


        Bundle bundle=new Bundle();
        bundle.putSerializable(Constants.EXTRA_PATH_RECORD,imageItem);;
        bundle.putBoolean(Constants.EXTRA_VIDEO_DEL,true);
        startActivity(ARouterConstants.ACTIVITY_VIDEP_PLAY,bundle,mContext);
    }
}
