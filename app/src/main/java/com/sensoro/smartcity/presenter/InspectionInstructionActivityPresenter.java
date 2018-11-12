package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.InspectionTaskInstructionModel;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.InspectionTaskInstructionRsp;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageAlarmPhotoDetailActivity;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InspectionInstructionActivityPresenter extends BasePresenter<IInspectionInstructionActivityView>
implements Constants{
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        ArrayList<String> deviceTypes = mActivity.getIntent().getStringArrayListExtra(EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE);
        if (deviceTypes != null && deviceTypes.size()>0) {
            getView().updateRcTag(deviceTypes);
            requestContentData(deviceTypes.get(0));
        }


    }

    private void requestContentData(String type) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getInspectionTemplate(type).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskInstructionRsp>(this) {
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
        Intent intentPreview = new Intent(mActivity, ImageAlarmPhotoDetailActivity.class);
        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imgs);
        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
        intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
        getView().startAC(intentPreview);
    }

    public void doRequestTemplate(String type) {
        requestContentData(type);
    }
}
