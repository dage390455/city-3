package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.DeployNameplateRsp;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;
import com.sensoro.nameplate.IMainViews.IDeployNameplateActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.activity.DeployNameplateNameActivity;
import com.sensoro.nameplate.model.DeployNameplateModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.common.constant.Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
import static com.sensoro.common.constant.Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY;
import static com.sensoro.common.constant.Constants.EXTRA_DEPLOY_RESULT_MODEL;

public class DeployNameplateActivityPresenter extends BasePresenter<IDeployNameplateActivityView> {
    private Activity mActivity;
    private DeployNameplateModel deployNameplateModel = new DeployNameplateModel();
    private String mNameplateId;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        Bundle bundle = getBundle(mActivity);
        if (bundle != null) {
            mNameplateId = bundle.getString("nameplateId");
            if (!TextUtils.isEmpty(mNameplateId)) {
                getView().setNameplateId(mNameplateId);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        Object data = eventData.data;
        switch (eventData.code) {
            case Constants.EVENT_DATA_DEPLOY_NAMEPLATE_NAME:
                if (data instanceof String) {
                    String text = (String) data;
                    if (TextUtils.isEmpty(text)) {
                        getView().setUploadStatus(false);
                        getView().setName(mActivity.getString(R.string.required), R.color.c_a6a6a6);
                    } else {
                        getView().setUploadStatus(true);
                        getView().setName(text, R.color.c_252525);
                    }
                } else {
                    getView().setUploadStatus(false);
                    getView().setName(mActivity.getString(R.string.required), R.color.c_a6a6a6);
                }


                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_TAG:

                if (data instanceof List) {
                    deployNameplateModel.tags.clear();
                    deployNameplateModel.tags.addAll((List<String>) data);
                    getView().updateTagsData((List<String>) data);
                }
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_PHOTO:
                if (data instanceof List) {
                    deployNameplateModel.deployPics.clear();

                    deployNameplateModel.deployPics.addAll((ArrayList<ImageItem>) data);

                    getView().setDeployPhotoTextSize(deployNameplateModel.deployPics.size());
                }
                break;
            case Constants.EVENT_DATA_DEPLOY_BIND_LIST:
                if (eventData.data instanceof ArrayList) {
                    deployNameplateModel.bindList = (ArrayList<NamePlateInfo>) eventData.data;
                    getView().setAssociateSensorSize(deployNameplateModel.bindList.size());
                }
            case Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
            case Constants.EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE:
                getView().finishAc();

                break;
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doName(String name) {
        Intent intent = new Intent(mActivity, DeployNameplateNameActivity.class);
        intent.putExtra(Constants.EXTRA_DEPLOY_NAMEPLATE_NAME, name);
        getView().startAC(intent);
    }

    public void doTag() {
        Bundle bundle = new Bundle();
//        if (deployAnalyzerModel.tagList.size() > 0) {
//            bundle.putStringArrayList(Constants.EXTRA_SETTING_TAG_LIST, (ArrayList<String>) deployAnalyzerModel.tagList);
//        }
        bundle.putSerializable(Constants.EXTRA_SETTING_TAG_LIST, deployNameplateModel.tags);
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_DEVICE_TAG, bundle, mActivity);
    }

    public void doPic() {
//        Intent intent = new Intent(mActivity, DeployMonitorDeployPicActivity.class);
//////        if (getRealImageSize() > 0) {
//////            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, deployAnalyzerModel.images);
//////        }
//////        intent.putExtra(Constants.EXTRA_SETTING_DEPLOY_DEVICE_TYPE, deployAnalyzerModel.deviceType);
////        getView().startAC(intent);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_SETTING_DEPLOY_DEVICE_TYPE, "deploy_nameplate");
        bundle.putSerializable(Constants.EXTRA_DEPLOY_TO_PHOTO, deployNameplateModel.deployPics);
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_DEVICE_PIC, bundle, mActivity);
    }

    public void doAssociationSensor() {
        if (TextUtils.isEmpty(mNameplateId)) {
            getView().toastShort(mActivity.getString(R.string.nameplate_name_empty));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_ASSOCIATION_SENSOR_ADD_BIND_LIST, deployNameplateModel.bindList);
        bundle.putString(Constants.EXTRA_ASSOCIATION_SENSOR_NAMEPLATE_ID, mNameplateId);
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_ASSOCIATE_SENSOR, bundle, mActivity);
    }

    //必传字段判空
    public void doUpload() {
        if (deployNameplateModel.deployPics.size() > 0) {
            doUploadPic();
        } else {
            doDeployNameplate(null);
        }
    }

    private void doUploadPic() {
        final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                .UpLoadPhotoListener() {

            @Override
            public void onStart() {
                if (isAttachedView()) {
                    getView().showStartUploadProgressDialog();
                }

            }

            @Override
            public void onComplete(List<ScenesData> scenesDataList) {
                ArrayList<String> strings = new ArrayList<>();
                for (ScenesData scenesData : scenesDataList) {
                    scenesData.type = "image";
                    strings.add(scenesData.url);
                }
                try {
                    LogUtils.loge(this, "上传成功--- size = " + strings.size());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if (isAttachedView()) {
                    getView().dismissUploadProgressDialog();
                    // 上传结果
                    doDeployNameplate(strings);
                }


            }

            @Override
            public void onError(String errMsg) {
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
        };
        UpLoadPhotosUtils upLoadPhotosUtils = new UpLoadPhotosUtils(mActivity, upLoadPhotoListener);
        ArrayList<ImageItem> list = new ArrayList<>();
        for (ImageItem image : deployNameplateModel.deployPics) {
            if (image != null) {
                list.add(image);
            }
        }
        upLoadPhotosUtils.doUploadPhoto(list);
    }

    private void doDeployNameplate(ArrayList<String> strings) {
        RetrofitServiceHelper.getInstance().doUploadDeployNameplate(mNameplateId, deployNameplateModel.name, deployNameplateModel.tags, strings, deployNameplateModel.bindList)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployNameplateRsp>(this) {

            @Override
            public void onCompleted(DeployNameplateRsp deployNameplateRsp) {


                DeployResultModel deployResultModel = new DeployResultModel();
                Bundle bundle = new Bundle();
                //
                deployResultModel.sn = mNameplateId;
                deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
                deployResultModel.scanType = EVENT_DATA_ADD_SENSOR_FROM_DEPLOY;
                deployResultModel.updateTime = System.currentTimeMillis();
                deployResultModel.name = deployNameplateModel.name;
                bundle.putSerializable(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);

                startActivity(ARouterConstants.ACTIVITY_DEPLOYRESULT, bundle, mActivity);
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);


                DeployResultModel deployResultModel = new DeployResultModel();
                Bundle bundle = new Bundle();
                deployResultModel.sn = mNameplateId;
                deployResultModel.errorMsg = errorMsg;
                deployResultModel.resultCode = Constants.DEPLOY_RESULT_MODEL_CODE_SCAN_FAILED;
                deployResultModel.scanType = EVENT_DATA_ADD_SENSOR_FROM_DEPLOY;
                deployResultModel.updateTime = System.currentTimeMillis();
                deployResultModel.name = deployNameplateModel.name;
                bundle.putSerializable(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);

                startActivity(ARouterConstants.ACTIVITY_DEPLOYRESULT, bundle, mActivity);
            }
        });
    }
}
