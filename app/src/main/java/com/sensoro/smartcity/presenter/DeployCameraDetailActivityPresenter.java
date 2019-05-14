package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployDeviceTagActivity;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.activity.DeployMonitorDeployPicActivity;
import com.sensoro.smartcity.activity.DeployMonitorNameAddressActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployCameraDetailActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.DeployResultModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.DeployStationInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeployStationInfoRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.RegexUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.popup.SelectDialog;
import com.sensoro.smartcity.widget.popup.UpLoadPhotosUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployCameraDetailActivityPresenter extends BasePresenter<IDeployCameraDetailActivityView> implements IOnCreate, IOnStart, Constants
        , Runnable {
    private Activity mContext;
    private Handler mHandler;
    private DeployAnalyzerModel deployAnalyzerModel;
    private final Runnable signalTask = new Runnable() {
        @Override
        public void run() {
            //TODO 轮询查看摄像头状态？
            mHandler.postDelayed(signalTask, 2000);
        }
    };
    private String originName;
    private final List<String> deployMethods = new ArrayList<>();
    private final List<String> deployOrientations = new ArrayList<>();

    @Override
    public void initData(Context context) {
        deployMethods.add("支架");
        deployMethods.add("吊顶");
        deployMethods.add("壁装");
        deployMethods.add("立杆(大于8米)");
        deployMethods.add("立杆(6~8米)");
        deployMethods.add("立杆(小于6米)");
        deployMethods.add("悬臂拖装");
        deployMethods.add("悬臂吊装");
        deployOrientations.add("正东朝向");
        deployOrientations.add("正南朝向");
        deployOrientations.add("正西朝向");
        deployOrientations.add("正北朝向");
        deployOrientations.add("东南朝向");
        deployOrientations.add("东北朝向");
        deployOrientations.add("西南朝向");
        deployOrientations.add("西北朝向");
        mContext = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        onCreate();
        Intent intent = mContext.getIntent();
        deployAnalyzerModel = (DeployAnalyzerModel) intent.getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        originName = deployAnalyzerModel.nameAndAddress;
        getView().setNotOwnVisible(deployAnalyzerModel.notOwn);
        init();
        if (PreferencesHelper.getInstance().getUserData().hasSignalConfig && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION || Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
            mHandler.post(this);
        }
        mHandler.post(signalTask);
        //
        getView().setDeployCameraStatus("1");

    }

    private void init() {
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().setDeployPhotoVisible(true);
                echoDeviceInfo();
                break;
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //巡检设备更换
                getView().setDeployPhotoVisible(true);
                getView().updateUploadTvText(mContext.getString(R.string.replacement_equipment));
                echoDeviceInfo();
                break;
            default:
                break;
        }
        String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
        getView().setDeployDeviceType(mContext.getString(R.string.deploy_device_type) + ":" + deviceTypeName);
    }

    //回显设备信息
    private void echoDeviceInfo() {
        getView().setDeviceSn(mContext.getString(R.string.device_number) + deployAnalyzerModel.sn);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
        }
        getView().updateTagsData(deployAnalyzerModel.tagList);
        //TODO 刷线摄像头状态
        getView().setUploadBtnStatus(checkCanUpload());
        getView().setDeployPosition(deployAnalyzerModel.latLng.size() == 2);
        try {
            LogUtils.loge("channelMask--->> " + deployAnalyzerModel.channelMask.size());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    //
    public void requestUpload() {
        final double lon = deployAnalyzerModel.latLng.get(0);
        final double lan = deployAnalyzerModel.latLng.get(1);
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().doStationDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<DeployStationInfoRsp>(this) {

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().updateUploadState(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(deployAnalyzerModel.sn, null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                                } else {
                                    freshError(deployAnalyzerModel.sn, errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                                }
                            }

                            @Override
                            public void onCompleted(DeployStationInfoRsp deployStationInfoRsp) {
                                freshStation(deployStationInfoRsp);
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }
                        });
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //巡检设备更换
                //TODO 暂时对电气火灾设备直接上传
//                if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
//                    doUploadImages(lon, lan);
//                } else {

//                if (PreferencesHelper.getInstance().getUserData().hasSignalConfig || Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType)) {
//                    changeDevice(lon, lan);
//                } else {
//                    doUploadImages(lon, lan);
//                }
//                }
                handleBleSetting(lon, lan);
                break;
            default:
                break;
        }
    }

    private void handleBleSetting(double lon, double lan) {
        doUploadImages(lon, lan);
    }


    private void doUploadImages(final double lon, final double lan) {
        if (getRealImageSize() > 0) {
            //TODO 图片提交
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
                        doDeployResult(lon, lan, strings);
                    }


                }

                @Override
                public void onError(String errMsg) {
                    if (isAttachedView()) {
                        getView().updateUploadState(true);
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
            UpLoadPhotosUtils upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, upLoadPhotoListener);
            ArrayList<ImageItem> list = new ArrayList<>();
            for (ImageItem image : deployAnalyzerModel.images) {
                if (image != null) {
                    list.add(image);
                }
            }
            upLoadPhotosUtils.doUploadPhoto(list);
        } else {
            doDeployResult(lon, lan, null);
        }
    }

    private void doDeployResult(double lon, double lan, List<String> imgUrls) {
        DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().showProgressDialog();
                //TODO 暂时不支持添加wx电话
                //TODO 添加电气火灾配置支持
//                deployAnalyzerModel.weChatAccount = null;
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                //暂时添加 后续可以删除
                DeployControlSettingData settingData = null;
                if (isFire) {
                    settingData = deployAnalyzerModel.settingData;
                }
                RetrofitServiceHelper.getInstance().doDevicePointDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployContactModel.name, deployContactModel.phone, deployAnalyzerModel.weChatAccount, imgUrls, settingData, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<DeviceDeployRsp>(this) {
                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().updateUploadState(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(deployAnalyzerModel.sn, null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                                } else {
                                    freshError(deployAnalyzerModel.sn, errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                                }
                            }

                            @Override
                            public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                                freshPoint(deviceDeployRsp);
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }
                        });
                break;
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        deployAnalyzerModel.mDeviceDetail.getTaskId(), 1, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployContactModel.name, deployContactModel.phone, imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceDeployRsp>(this) {
                    @Override
                    public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                        freshPoint(deviceDeployRsp);
                        getView().dismissProgressDialog();
                        getView().finishAc();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().updateUploadState(true);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(deployAnalyzerModel.sn, null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                        } else {
                            freshError(deployAnalyzerModel.sn, errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                        }
                    }
                });
                break;
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        null, 2, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress, deployContactModel.name,
                        deployContactModel.phone, imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceDeployRsp>(this) {
                    @Override
                    public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                        //
                        freshPoint(deviceDeployRsp);
                        getView().dismissProgressDialog();
                        getView().finishAc();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().updateUploadState(true);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(deployAnalyzerModel.sn, null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                        } else {
                            freshError(deployAnalyzerModel.sn, errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                        }
                    }
                });
                break;
            default:
                break;
        }

    }

    private void freshError(String scanSN, String errorInfo, int resultCode) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivity.class);
        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.sn = scanSN;
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.resultCode = resultCode;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.errorMsg = errorInfo;
        deployResultModel.wxPhone = deployAnalyzerModel.weChatAccount;
        deployResultModel.settingData = deployAnalyzerModel.settingData;
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
            deployResultModel.contact = deployContactModel.name;
            deployResultModel.phone = deployContactModel.phone;
        }
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.updateTime = deployAnalyzerModel.updatedTime;
        deployResultModel.deviceStatus = deployAnalyzerModel.status;
        deployResultModel.signal = deployAnalyzerModel.signal;
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshPoint(DeviceDeployRsp deviceDeployRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        DeviceInfo deviceInfo = deviceDeployRsp.getData();
        deployResultModel.deviceInfo = deviceInfo;
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        //
        deployResultModel.sn = deviceInfo.getSn();
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.wxPhone = deployAnalyzerModel.weChatAccount;
        deployResultModel.settingData = deployAnalyzerModel.settingData;
        //TODO 新版联系人
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
            deployResultModel.contact = deployContactModel.name;
            deployResultModel.phone = deployContactModel.phone;
        }
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.updateTime = deviceInfo.getUpdatedTime();
        deployResultModel.deviceStatus = deviceInfo.getStatus();
        deployResultModel.signal = deviceInfo.getSignal();
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshStation(DeployStationInfoRsp deployStationInfoRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        //
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        DeployStationInfo deployStationInfo = deployStationInfoRsp.getData();
        deployResultModel.name = deployStationInfo.getName();
        deployResultModel.sn = deployStationInfo.getSn();
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.stationStatus = deployStationInfo.getNormalStatus();
        deployResultModel.updateTime = deployStationInfo.getUpdatedTime();
        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.signal = deployAnalyzerModel.signal;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        deployAnalyzerModel.tagList.clear();
        deployAnalyzerModel.images.clear();
        mHandler.removeCallbacksAndMessages(null);

    }

    public void doNameAddress() {
        Intent intent = new Intent(mContext, DeployMonitorNameAddressActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, deployAnalyzerModel.nameAndAddress);
        }
        intent.putExtra(EXTRA_DEPLOY_TO_SN, deployAnalyzerModel.sn);
        intent.putExtra(EXTRA_DEPLOY_TYPE, deployAnalyzerModel.deployType);
        if (!TextUtils.isEmpty(originName)) {
            intent.putExtra(EXTRA_DEPLOY_ORIGIN_NAME_ADDRESS, originName);
        }
        intent.putExtra(EXTRA_DEPLOY_TYPE, deployAnalyzerModel.deployType);
        getView().startAC(intent);
    }

    public void doTag() {
        Intent intent = new Intent(mContext, DeployDeviceTagActivity.class);
        if (deployAnalyzerModel.tagList.size() > 0) {
            intent.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) deployAnalyzerModel.tagList);
        }
        getView().startAC(intent);
    }

    public void doSettingPhoto() {
        Intent intent = new Intent(mContext, DeployMonitorDeployPicActivity.class);
        if (getRealImageSize() > 0) {
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, deployAnalyzerModel.images);
        }
        intent.putExtra(EXTRA_SETTING_DEPLOY_DEVICE_TYPE, deployAnalyzerModel.deviceType);
        getView().startAC(intent);
    }

    public void doDeployMap() {
        Intent intent = new Intent();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mContext, DeployMapActivity.class);
        } else {
            intent.setClass(mContext, DeployMapENActivity.class);
        }
        deployAnalyzerModel.mapSourceType = DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL;
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE:
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
            case EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS:
                if (data instanceof String) {
                    deployAnalyzerModel.nameAndAddress = (String) data;
                    getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
                }
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            case EVENT_DATA_DEPLOY_SETTING_TAG:
                if (data instanceof List) {
                    deployAnalyzerModel.tagList.clear();
                    deployAnalyzerModel.tagList.addAll((List<String>) data);
                    getView().updateTagsData(deployAnalyzerModel.tagList);
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_CONTACT:
                if (data instanceof List) {
                    //TODO 联系人
                    deployAnalyzerModel.deployContactModelList.clear();
                    deployAnalyzerModel.deployContactModelList.addAll((List<DeployContactModel>) data);
                }
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            case EVENT_DATA_DEPLOY_SETTING_PHOTO:
                if (data instanceof List) {
                    deployAnalyzerModel.images.clear();

                    deployAnalyzerModel.images.addAll((ArrayList<ImageItem>) data);

                    if (getRealImageSize() > 0) {
                        getView().setDeployPhotoText(mContext.getString(R.string.added) + getRealImageSize() + mContext.getString(R.string.images));
                    } else {
                        getView().setDeployPhotoText(mContext.getString(R.string.not_added));
                    }
                    getView().setUploadBtnStatus(checkCanUpload());
                }
                break;
            case EVENT_DATA_DEPLOY_MAP:
                if (data instanceof DeployAnalyzerModel) {
                    this.deployAnalyzerModel = (DeployAnalyzerModel) data;
                    //TODO 刷新数据状态
                }
                getView().setDeployPosition(deployAnalyzerModel.latLng.size() == 2);
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            default:
                break;
        }
    }

    private int getRealImageSize() {
        int count = 0;
        for (ImageItem image : deployAnalyzerModel.images) {
            if (image != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doConfirm() {
        //姓名地址校验
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                if (checkHasPhoto()) return;
                //经纬度校验
                if (checkHasNoLatLng()) return;
                requestUpload();
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                if (checkHasPhoto()) return;
                //经纬度校验
                if (checkHasNoLatLng()) return;
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                if (isFire) {
                    if (deployAnalyzerModel.settingData == null) {
                        getView().toastShort(mContext.getString(R.string.deploy_has_no_configuration_tip));
                        return;
                    }
                }
//                if (checkNeedSignal()) {
//                    checkHasForceUploadPermission();
//                } else {
//                    requestUpload();
//                }

                break;
            default:
                break;
        }

    }

    private boolean checkCanUpload() {
        String name_default = mContext.getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress) || deployAnalyzerModel.nameAndAddress.equals(name_default)) {
            return false;
        } else {
            byte[] bytes = new byte[0];
            try {
                bytes = deployAnalyzerModel.nameAndAddress.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                return false;
            }
        }
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                if (getRealImageSize() == 0 && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
                    return false;
                }
                //经纬度校验
                if (deployAnalyzerModel.latLng.size() != 2) {
                    return false;
                }
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //联系人校验
                if (deployAnalyzerModel.deployContactModelList.size() > 0) {
                    DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
                    if (TextUtils.isEmpty(deployContactModel.name) || TextUtils.isEmpty(deployContactModel.phone)) {
                        return false;
                    }
                    if (!RegexUtils.checkPhone(deployContactModel.phone)) {
                        return false;
                    }
                } else {
                    return false;
                }
                //照片校验
                if (getRealImageSize() == 0 && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
                    return false;
                }
                //经纬度校验
                if (deployAnalyzerModel.latLng.size() != 2) {
                    return false;
                }
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                if (isFire) {
                    if (deployAnalyzerModel.settingData == null) {
                        return false;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 检查是否能强制上传
     */
    private void checkHasForceUploadPermission() {
        String mergeType = WidgetUtil.handleMergeType(deployAnalyzerModel.deviceType);
        if (TextUtils.isEmpty(mergeType)) {
            getView().showWarnDialog(true);
        } else {
            if (Constants.DEPLOY_CAN_FOURCE_UPLOAD_PERMISSION_LIST.contains(mergeType)) {
                if (PreferencesHelper.getInstance().getUserData().hasBadSignalUpload) {
                    getView().showWarnDialog(true);
                } else {
                    getView().showWarnDialog(false);
                }
            } else {
                getView().showWarnDialog(true);
            }
        }
    }

    private boolean checkHasDeployMethod() {
        return false;
    }

    private boolean checkHasDeployOrientation() {
        return false;
    }


    /**
     * 检测姓名和地址是否填写
     *
     * @return
     */
    private boolean checkHasNameAddress() {
        //例：大悦城20层走廊2号配电箱
        String name_default = mContext.getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress) || deployAnalyzerModel.nameAndAddress.equals(name_default)) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_input_name));
            getView().updateUploadState(true);
            return true;
        } else {
            byte[] bytes = new byte[0];
            try {
                bytes = deployAnalyzerModel.nameAndAddress.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort(mContext.getString(R.string.name_address_length));
                getView().updateUploadState(true);
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否有经纬度
     *
     * @return
     */
    private boolean checkHasNoLatLng() {
        if (deployAnalyzerModel.latLng.size() != 2) {
            getView().toastShort(mContext.getString(R.string.please_specify_the_deployment_location));
            getView().updateUploadState(true);
            return true;
        }
        return false;
    }

    /**
     * 检测是否有图片
     *
     * @return
     */
    private boolean checkHasPhoto() {
        if (getRealImageSize() == 0 && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
            getView().toastShort(mContext.getString(R.string.please_add_at_least_one_image));
            getView().updateUploadState(true);
            return true;
        }
        return false;
    }


    @Override
    public void run() {
        //TODO
        mHandler.postDelayed(this, 2000);

    }


    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    public void doDeployMethod() {
        AppUtils.showDialog(mContext, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String method = deployMethods.get(position);
                getView().setDeployMethod(method);
            }
        }, deployMethods);

    }

    public void doDeployOrientation() {
        AppUtils.showDialog(mContext, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String orientation = deployOrientations.get(position);
                getView().setDeployOrientation(orientation);
            }
        }, deployOrientations);
    }
}
