package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionExceptionDetailActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.InspectionTaskExceptionDeviceModel;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.InspectionTaskExceptionDeviceRsp;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageAlarmPhotoDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionExceptionDetailActivityPresenter extends BasePresenter<IInspectionExceptionDetailActivityView>
        implements Constants {
    private Activity mContext;
    private InspectionTaskDeviceDetail mDeviceDetail;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL);
        requestExceptionDetail();
    }

    private void requestExceptionDetail() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectionDeviceDetail(mDeviceDetail.getId(), null, null, 1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskExceptionDeviceRsp>(this) {
            @Override
            public void onCompleted(InspectionTaskExceptionDeviceRsp response) {
                InspectionTaskExceptionDeviceModel taskDevice = response.getData();
                getView().setTvName(taskDevice.getDevice().getName());
                String inspectionDeviceName = WidgetUtil.getInspectionDeviceName(taskDevice.getDeviceType());
                getView().setTvSn(inspectionDeviceName + " " + taskDevice.getSn());
                getView().updateTagsData(taskDevice.getDevice().getTags());

                switch (taskDevice.getStatus()) {
                    case 1:
                        getView().setTvStatus(R.color.c_29c093, "巡检正常");
                        break;
                    case 2:
                        getView().setTvStatus(R.color.c_ff8d34, "巡检异常");
                        break;
                    default:
                        getView().setTvStatus(R.color.c_a6a6a6, "未巡检");
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
        ImageItem imageItem = new ImageItem();
        imageItem.isRecord = true;
        imageItem.thumbPath = item.thumbUrl;
        imageItem.path = item.url;
        Intent intent = new Intent();
        intent.setClass(mContext, VideoPlayActivity.class);
        intent.putExtra("path_record", (Serializable) imageItem);
        intent.putExtra("video_del", true);
        getView().startAC(intent);
    }
}
