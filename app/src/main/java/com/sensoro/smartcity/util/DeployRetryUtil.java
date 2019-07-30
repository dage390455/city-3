package com.sensoro.smartcity.util;

import android.content.Context;

import com.google.gson.internal.LinkedTreeMap;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeployStationInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployRetryUtil {

    private static volatile LinkedTreeMap<String, DeployAnalyzerModel> deployTasks = new LinkedTreeMap<>();

    private DeployRetryUtil() {

        LinkedTreeMap<String, DeployAnalyzerModel> alltask = PreferencesHelper.getInstance().getofflineDeployData();
        if (null != alltask) {
            deployTasks = alltask;
        }
    }

    public static DeployRetryUtil getInstance() {
        return DeployRetryHolder.instance;
    }


    private static class DeployRetryHolder {
        private static final DeployRetryUtil instance = new DeployRetryUtil();


    }

    public void addTask(DeployAnalyzerModel task) {

        deployTasks.put(task.sn, task);
        PreferencesHelper.getInstance().setofflineDeployData(deployTasks);


    }

    public void removeTask(DeployAnalyzerModel task) {

        deployTasks.remove(task);
        PreferencesHelper.getInstance().setofflineDeployData(deployTasks);

    }

//    public LinkedHashMap<String, DeployAnalyzerModel> getAllTask() {
//        return deployTasks;
//    }


    /**
     * @param context
     * @param deployAnalyzerModel
     */
    public void retryTry(Context context, DeployAnalyzerModel deployAnalyzerModel, OnRetryListener retryListener) {


        if (null != deployAnalyzerModel.imgUrls && deployAnalyzerModel.imgUrls.size() > 0) {
            //上传完照片
            retryPostResult(deployAnalyzerModel, retryListener);
        } else {
            if (null != deployAnalyzerModel.imageItems && deployAnalyzerModel.imageItems.size() > 0) {
                final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                        .UpLoadPhotoListener() {

                    @Override
                    public void onStart() {
                        retryListener.onStart();
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

                        deployAnalyzerModel.imgUrls = strings;
                        retryPostResult(deployAnalyzerModel, retryListener);


                    }

                    @Override
                    public void onError(String errMsg) {
                        retryListener.onError(errMsg);

                    }

                    @Override
                    public void onProgress(String content, double percent) {
                        retryListener.onProgress(content, percent);
                    }
                };
                UpLoadPhotosUtils upLoadPhotosUtils = new UpLoadPhotosUtils(context, upLoadPhotoListener);
                //本地照片
                upLoadPhotosUtils.doUploadPhoto(deployAnalyzerModel.imageItems);
            } else {
                //没有照片
                retryPostResult(deployAnalyzerModel, retryListener);
            }

        }

    }


    private void retryPostResult(DeployAnalyzerModel deployAnalyzerModel, OnRetryListener retryListener) {


        //TODO 添加设备状态字段
        double lon = deployAnalyzerModel.latLng.get(0);
        double lan = deployAnalyzerModel.latLng.get(1);
        switch (deployAnalyzerModel.deployType) {


            case Constants.TYPE_SCAN_DEPLOY_STATION:
                RetrofitServiceHelper.getInstance().doStationDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<ResponseResult<DeployStationInfo>>(null) {

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                retryListener.onErrorMsg(errorCode, errorMsg);
                            }

                            @Override
                            public void onCompleted(ResponseResult<DeployStationInfo> deployStationInfoRsp) {
                                retryListener.onCompleted(freshStation(deployAnalyzerModel, deployStationInfoRsp));
                                removeTask(deployAnalyzerModel);
                            }
                        });
                break;


            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                boolean isFire = Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                //暂时添加 后续可以删除
                DeployControlSettingData settingData = null;
                if (isFire) {
                    settingData = deployAnalyzerModel.settingData;
                }


                RetrofitServiceHelper.getInstance().doDevicePointDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployAnalyzerModel.deployContactModelList, deployAnalyzerModel.weChatAccount, deployAnalyzerModel.imgUrls, settingData, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<ResponseResult<DeviceInfo>>(null) {
                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {

                                retryListener.onErrorMsg(errorCode, errorMsg);
                            }

                            @Override
                            public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                                retryListener.onCompleted(freshPoint(deployAnalyzerModel, deviceDeployRsp));
                                removeTask(deployAnalyzerModel);


                            }
                        });
                break;
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        deployAnalyzerModel.mDeviceDetail.getTaskId(), 1, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployAnalyzerModel.deployContactModelList, deployAnalyzerModel.imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceInfo>>(null) {
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

                        retryListener.onErrorMsg(errorCode, errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {


                        removeTask(deployAnalyzerModel);


                        retryListener.onCompleted(freshPoint(deployAnalyzerModel, deviceDeployRsp));


                    }
                });
                break;
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        null, 2, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress, deployAnalyzerModel.deployContactModelList, deployAnalyzerModel.imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceInfo>>(null) {
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

                        retryListener.onErrorMsg(errorCode, errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                        retryListener.onCompleted(freshPoint(deployAnalyzerModel, deviceDeployRsp));
                        removeTask(deployAnalyzerModel);


                    }
                });
                break;
            default:
                break;

        }
    }

    private DeployResultModel freshError(DeployAnalyzerModel deployAnalyzerModel, String errorInfo, int resultCode) {
        //

        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.sn = deployAnalyzerModel.sn;
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.resultCode = resultCode;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.errorMsg = errorInfo;
        deployResultModel.wxPhone = deployAnalyzerModel.weChatAccount;
        deployResultModel.settingData = deployAnalyzerModel.settingData;
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            deployResultModel.deployContactModelList.addAll(deployAnalyzerModel.deployContactModelList);
        }
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.updateTime = deployAnalyzerModel.updatedTime;
        deployResultModel.deviceStatus = deployAnalyzerModel.status;
        deployResultModel.signal = deployAnalyzerModel.signal;
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;

        return deployResultModel;
    }

    private DeployResultModel freshPoint(DeployAnalyzerModel deployAnalyzerModel, ResponseResult<DeviceInfo> deviceDeployRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        DeviceInfo deviceInfo = deviceDeployRsp.getData();
        deployResultModel.deviceInfo = deviceInfo;
        //
        deployResultModel.sn = deviceInfo.getSn();
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.resultCode = Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.wxPhone = deployAnalyzerModel.weChatAccount;
        deployResultModel.settingData = deployAnalyzerModel.settingData;
        //TODO 新版联系人
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            deployResultModel.deployContactModelList.addAll(deployAnalyzerModel.deployContactModelList);
        }
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.updateTime = deviceInfo.getUpdatedTime();
        deployResultModel.deployTime = deviceInfo.getDeployTime();
        deployResultModel.deviceStatus = deployAnalyzerModel.status;
        deployResultModel.signal = deviceInfo.getSignal();
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;

        return deployResultModel;
    }

    private DeployResultModel freshStation(DeployAnalyzerModel deployAnalyzerModel, ResponseResult<DeployStationInfo> deployStationInfoRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        //
        DeployStationInfo deployStationInfo = deployStationInfoRsp.getData();
        deployResultModel.name = deployStationInfo.getName();
        deployResultModel.sn = deployStationInfo.getSn();
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.stationStatus = deployStationInfo.getNormalStatus();
        deployResultModel.updateTime = deployStationInfo.getUpdatedTime();
        deployResultModel.resultCode = Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.signal = deployAnalyzerModel.signal;
        return deployResultModel;
    }

    public interface OnRetryListener extends UpLoadPhotosUtils.UpLoadPhotoListener {


        void onCompleted(DeployResultModel deployResultModel);

        void onErrorMsg(int errorCode, String errorMsg);


    }


}
