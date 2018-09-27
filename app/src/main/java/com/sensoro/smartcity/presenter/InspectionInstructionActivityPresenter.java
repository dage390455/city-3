package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskInstructionModel;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.InspectionTaskInstructionRsp;
import com.sensoro.smartcity.server.response.ResponseBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionInstructionActivityPresenter extends BasePresenter<IInspectionInstructionActivityView>
implements Constants{
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;

        InspectionIndexTaskInfo taskInfo = (InspectionIndexTaskInfo) ((Activity) mContext).getIntent().getSerializableExtra(EXTRA_INSPECTION_INDEX_TASK_INFO);
        getView().updateRcTag(taskInfo.getDeviceSummary());
        if (taskInfo.getDeviceSummary().size()>0) {
            requestContentData(taskInfo.getDeviceSummary().get(0).getType());
        }


    }

    private void requestContentData(String type) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectionTemplate(type).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskInstructionRsp>() {
            @Override
            public void onCompleted(InspectionTaskInstructionRsp instructionRsp) {
                List<InspectionTaskInstructionModel.DataBean> data = instructionRsp.getData().getData();
                getView().updateRcContentData(data);
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

    public void doRequestTemplate(String type) {
        requestContentData(type);
    }
}
