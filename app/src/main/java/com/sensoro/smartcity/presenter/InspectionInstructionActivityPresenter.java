package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.InspectionTaskInstructionModel;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.smartcity.imainviews.IInspectionInstructionActivityView;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageAlarmPhotoDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InspectionInstructionActivityPresenter extends BasePresenter<IInspectionInstructionActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        ArrayList<String> deviceTypes = mActivity.getIntent().getStringArrayListExtra(Constants.EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE);
        if (deviceTypes != null && deviceTypes.size()>0) {
            getView().updateRcTag(deviceTypes);
            requestContentData(deviceTypes.get(0));
        }


    }

    private void requestContentData(String type) {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getInspectionTemplate(type).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<InspectionTaskInstructionModel>>(this) {
            @Override
            public void onCompleted(ResponseResult<InspectionTaskInstructionModel> instructionRsp) {
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
