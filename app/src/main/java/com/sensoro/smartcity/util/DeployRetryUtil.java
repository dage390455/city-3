package com.sensoro.smartcity.util;

import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.RetryWithDelay;
import com.sensoro.common.server.bean.DeployCameraUploadInfo;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeployStationInfo;
import com.sensoro.common.server.bean.DeviceCameraDetailInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployRetryUtil {
    //TODO treemap默认会按照key做自然排序 顺序会不一致 最好用linkhashmap
    private static volatile LinkedHashMap<String, DeployAnalyzerModel> deployTasks = new LinkedHashMap<>();

    private DeployRetryUtil() {

        LinkedHashMap<String, DeployAnalyzerModel> alltask = PreferencesHelper.getInstance().getOfflineDeployData();
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

        task.lastOperateTime = System.currentTimeMillis();
        deployTasks.put(task.sn, task);
        PreferencesHelper.getInstance().setOfflineDeployData(deployTasks);


    }

    public void removeTask(DeployAnalyzerModel task) {
        //TODO 这个应该是移除sn吧
        deployTasks.remove(task.sn);
        PreferencesHelper.getInstance().setOfflineDeployData(deployTasks);

    }

//    public LinkedHashMap<String, DeployAnalyzerModel> getAllTask() {
//        return deployTasks;
//    }


    /**
     * * 设备上传任务回调，区分摄像机（部署接口及判断是否在线）
     *
     * @param context
     * @param deployAnalyzerModel
     */
    public void retryTry(Context context, DeployAnalyzerModel deployAnalyzerModel, OnRetryListener retryListener) {

        //根据任务类型判断是否调用信号📶接口
//        if (deployAnalyzerModel.isGetDeviceRealStatusFailure) {


        if (deployAnalyzerModel.deployType == Constants.TYPE_SCAN_DEPLOY_CAMERA) {

            getCameraStatus(deployAnalyzerModel, retryListener);
        } else {
            getDeviceRealStatus(deployAnalyzerModel, retryListener);
        }
//        } else {
//            if (null != deployAnalyzerModel.imgUrls && deployAnalyzerModel.imgUrls.size() > 0) {
//                postResult(deployAnalyzerModel, retryListener);
//            } else {
//                doUploadImages(context, deployAnalyzerModel, retryListener);
//            }
//        }

    }


    /**
     * 上传照片
     *
     * @param context
     * @param deployAnalyzerModel
     * @param retryListener
     */
    public void doUploadImages(Context context, DeployAnalyzerModel
            deployAnalyzerModel, OnRetryListener retryListener) {
        //本地照片
        if (null != deployAnalyzerModel.imageItems && deployAnalyzerModel.imageItems.size() > 0) {
            final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                    .UpLoadPhotoListener() {
                @Override
                public void onStart() {
                    retryListener.onStart();
                }

                @Override
                public void onComplete(List<ScenesData> scenesDataList) {
                    ArrayList<String> strings = getStrings(scenesDataList);
                    try {
                        LogUtils.loge(this, "上传成功--- size = " + strings.size());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    deployAnalyzerModel.imgUrls = strings;
                    postResult(deployAnalyzerModel, retryListener);


                }

                private ArrayList<String> getStrings(List<ScenesData> scenesDataList) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (ScenesData scenesData : scenesDataList) {
                        scenesData.type = "image";
                        strings.add(scenesData.url);
                    }
                    return strings;
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
            upLoadPhotosUtils.doUploadPhoto(deployAnalyzerModel.imageItems);
        } else {
            //没有照片
            postResult(deployAnalyzerModel, retryListener);
        }
    }

    /**
     * 获取摄像机在线状态
     *
     * @param deployAnalyzerModel
     * @param retryListener
     */

    private void getCameraStatus(DeployAnalyzerModel deployAnalyzerModel, OnRetryListener retryListener) {

        RetrofitServiceHelper.getInstance().getDeviceCamera(deployAnalyzerModel.sn.toUpperCase()).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceCameraDetailInfo>>(null) {
            @Override
            public void onCompleted(ResponseResult<DeviceCameraDetailInfo> deviceCameraDetailRsp) {
                DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                if (data != null) {
                    DeviceCameraDetailInfo.CameraBean camera = data.getCamera();
                    if (camera != null) {
                        DeviceCameraDetailInfo.CameraBean.InfoBean info = camera.getInfo();
                        if (info != null) {
                            String deviceStatus = info.getDeviceStatus();
                            if (!TextUtils.isEmpty(deviceStatus)) {
                                deployAnalyzerModel.cameraStatus = deviceStatus;
                                retryListener.setDeployCameraStatus(deployAnalyzerModel.cameraStatus);
                            }
                        }
                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                retryListener.onDeployErrorMsg(errorCode, errorMsg);

            }
        });

    }


    /**
     * 获取信号📶质量状态
     *
     * @param deployAnalyzerModel
     * @param retryListener
     */
    private void getDeviceRealStatus(DeployAnalyzerModel deployAnalyzerModel, OnRetryListener
            retryListener) {
        RetrofitServiceHelper.getInstance().getDeviceRealStatus(deployAnalyzerModel.sn).subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(2, 100))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceInfo>>(null) {
            @Override
            public void onCompleted(final ResponseResult<DeviceInfo> data) {
                if (data != null && data.getData() != null) {
                    retryListener.onUpdateDeviceStatus(data);
                }


            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                retryListener.onDeployErrorMsg(errorCode, errorMsg);

            }
        });
    }

    /**
     * 发送服务端
     *
     * @param deployAnalyzerModel
     * @param retryListener
     */
    private void postResult(DeployAnalyzerModel deployAnalyzerModel, OnRetryListener
            retryListener) {


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
                                retryListener.onDeployErrorMsg(errorCode, errorMsg);
                            }

                            @Override
                            public void onCompleted(ResponseResult<DeployStationInfo> deployStationInfoRsp) {
                                retryListener.onDeployCompleted();
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

                                retryListener.onDeployErrorMsg(errorCode, errorMsg);
                            }

                            @Override
                            public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                                retryListener.onDeployCompleted();
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

                        retryListener.onDeployErrorMsg(errorCode, errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {


                        removeTask(deployAnalyzerModel);


                        retryListener.onDeployCompleted();


                    }
                });
                break;
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        null, 2, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress, deployAnalyzerModel.deployContactModelList, deployAnalyzerModel.imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceInfo>>(null) {
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

                        retryListener.onDeployErrorMsg(errorCode, errorMsg);
                    }

                    @Override
                    public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                        retryListener.onDeployCompleted();
                        removeTask(deployAnalyzerModel);


                    }
                });
                break;


            case Constants.TYPE_SCAN_DEPLOY_CAMERA:
                RetrofitServiceHelper.getInstance().doUploadDeployCamera(deployAnalyzerModel.sn, deployAnalyzerModel.nameAndAddress, deployAnalyzerModel.tagList,
                        PreferencesHelper.getInstance().getUserData().phone, String.valueOf(lan), String.valueOf(lon), deployAnalyzerModel.imgUrls, deployAnalyzerModel.address,
                        deployAnalyzerModel.mMethodConfig.code, deployAnalyzerModel.mOrientationConfig.code, deployAnalyzerModel.cameraStatus)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .safeSubscribe(new CityObserver<ResponseResult<DeployCameraUploadInfo>>(null) {
                            @Override
                            public void onCompleted(ResponseResult<DeployCameraUploadInfo> deployCameraUploadRsp) {

                                retryListener.onDeployCompleted();
                                removeTask(deployAnalyzerModel);
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {

                                retryListener.onDeployErrorMsg(errorCode, errorMsg);

                            }
                        });

                break;
            default:
                break;

        }
    }



    public interface OnRetryListener extends UpLoadPhotosUtils.UpLoadPhotoListener {


        void onDeployCompleted();

        void onDeployErrorMsg(int errorCode, String errorMsg);

        void onUpdateDeviceStatus(ResponseResult<DeviceInfo> data);


        void setDeployCameraStatus(String status);



    }


}
