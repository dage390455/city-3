package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionExceptionDetailActivityView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionTaskExceptionDeviceModel;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.InspectionTaskExceptionDeviceRsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionExceptionDetailActivityPresenter extends BasePresenter<IInspectionExceptionDetailActivityView>
implements Constants{
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;

        requestExceptionDetail();


    }

    private void requestExceptionDetail() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectionDeviceDetail("5ba9b3b2f11db9772ee33025",null,null,1)
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExceptionDeviceRsp>() {
            @Override
            public void onCompleted(InspectionTaskExceptionDeviceRsp response) {
                InspectionTaskExceptionDeviceModel taskDevice = response.getData();
                getView().setTvName(taskDevice.getDevice().getName());

                for (DeviceTypeModel deviceTypeModel : SensoroCityApplication.getInstance().mDeviceTypeList) {
                    if (taskDevice.getDeviceType().equals(deviceTypeModel.matcherType)) {
                        getView().setTvSn(deviceTypeModel.name+" "+taskDevice.getSn());
                        break;
                    }
                }
                getView().updateTagsData(taskDevice.getDevice().getTags());

                switch (taskDevice.getStatus()){
                    case -1:
                        getView().setTvStatus(R.color.c_a6a6a6,"未巡检");
                        break;
                    case 0:
                        getView().setTvStatus(R.color.c_29c093,"巡检正常");
                        break;
                    case 1:
                        getView().setTvStatus(R.color.c_ff8d34,"巡检异常");
                        break;
                }


                List<String> exceptionTags = new ArrayList<>();
                for (Integer integer : taskDevice.getMalfunctions()) {
                    exceptionTags.add(INSPECTION_EXCEPTION_TASGS[integer]);
                }
                getView().updateExceptionTagsData(exceptionTags);

                getView().setTvReamrk(taskDevice.getRemark());

                List<ScenesData> imgAndVedios = taskDevice.getImgAndVedio();
                ArrayList<ScenesData> images = new ArrayList<>();
                ArrayList<ScenesData> videoThumbs = new ArrayList<>();
                for (ScenesData imgAndVedio : imgAndVedios) {
                    if("image".equals(imgAndVedio.type)){
                        //图片资源的话后台没有thumb，所以是直接给url
                        images.add(imgAndVedio);
                    }else if("video".equals(imgAndVedio.type)){
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
        ImageItem imageItem = new ImageItem();
        imageItem.isRecord = true;
        imageItem.recordPath = item.url;
        imageItem.path = item.thumbUrl;
        Intent intent = new Intent();
        intent.setClass(mContext, VideoPlayActivity.class);
        intent.putExtra("path_record", (Serializable) imageItem);
        intent.putExtra("video_del", true);
        getView().startAC(intent);
    }
}
