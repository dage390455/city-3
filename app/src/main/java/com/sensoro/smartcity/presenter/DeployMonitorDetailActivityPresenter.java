package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.libbleserver.ble.BLEDevice;
import com.sensoro.libbleserver.ble.SensoroConnectionCallback;
import com.sensoro.libbleserver.ble.SensoroDeviceConnectionTest;
import com.sensoro.libbleserver.ble.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.DeployDeviceTagActivity;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMonitorAlarmContactActivity;
import com.sensoro.smartcity.activity.DeployMonitorNameAddressActivity;
import com.sensoro.smartcity.activity.DeployMonitorSettingPhotoActivity;
import com.sensoro.smartcity.activity.DeployMonitorWeChatRelationActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.DeployResultModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeployStationInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeployStationInfoRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.RegexUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.popup.UpLoadPhotosUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployMonitorDetailActivityPresenter extends BasePresenter<IDeployMonitorDetailActivityView> implements IOnCreate, Constants
        , SensoroConnectionCallback, BLEDeviceListener<BLEDevice>, Runnable {
    private Activity mContext;
    private SensoroDeviceConnectionTest sensoroDeviceConnection;
    private Handler mHandler;
    private String bleAddress;
    private boolean bleHasOpen;
    private static final HashSet<String> BLE_DEVICE_SET = new HashSet<>();
    private DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        onCreate();
        Intent intent = mContext.getIntent();
        deployAnalyzerModel = (DeployAnalyzerModel) intent.getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        getView().setNotOwnVisible(deployAnalyzerModel.notOwn);
        init();
        if (PreferencesHelper.getInstance().getUserData().hasSignalConfig && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
            mHandler.post(this);
        }
        BleObserver.getInstance().registerBleObserver(this);

    }

    private void init() {
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().setDeployContactRelativeLayoutVisible(false);
                getView().setDeployDeviceRlSignalVisible(false);
                getView().setDeployPhotoVisible(false);
                getView().setDeviceTitleName(deployAnalyzerModel.sn);
                if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
                    getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
                }
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().setDeployContactRelativeLayoutVisible(true);
                getView().setDeployDeviceRlSignalVisible(true);
                getView().setDeployPhotoVisible(true);
                echoDeviceInfo();
                break;
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //巡检设备更换
                getView().setDeployContactRelativeLayoutVisible(true);
                getView().setDeployDeviceRlSignalVisible(true);
                getView().setDeployPhotoVisible(true);
                getView().updateUploadTvText(mContext.getString(R.string.replacement_equipment));
                echoDeviceInfo();
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }
        getView().updateUploadState(true);
    }

    //回显设备信息
    private void echoDeviceInfo() {
        getView().setDeviceTitleName(deployAnalyzerModel.sn);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
        }
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            getView().updateContactData(deployAnalyzerModel.deployContactModelList);
        }
        if (deployAnalyzerModel.tagList.size() > 0) {
            getView().updateTagsData(deployAnalyzerModel.tagList);
        }
        freshSignalInfo();
    }

    //
    public void requestUpload() {
        final double lon = deployAnalyzerModel.latLng.get(0);
        final double lan = deployAnalyzerModel.latLng.get(1);
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doStationDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress).subscribeOn(Schedulers.io())
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
                if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                    changeDevice(lon, lan);
                } else {
                    doUploadImages(lon, lan);
                }
                break;
            default:
                break;
        }
    }

    private void changeDevice(double lon, double lan) {
        if (!TextUtils.isEmpty(deployAnalyzerModel.blePassword) && deployAnalyzerModel.channelMask.size() > 0) {
            if (BLE_DEVICE_SET.contains(deployAnalyzerModel.sn)) {
                getView().showBleConfigDialog();
                connectDevice();
            } else {
                getView().toastShort(mContext.getString(R.string.device_ble_deploy_failed));
                getView().updateUploadState(true);
            }
        } else {
            //TODO 直接上传
//            doUploadImages(lon, lan);
            //修改为不能强制上传
            getView().toastShort(mContext.getString(R.string.channel_mask_error_tip));
        }
    }

    private void connectDevice() {
        if (sensoroDeviceConnection != null) {
            sensoroDeviceConnection.disconnect();
        }
        sensoroDeviceConnection = new SensoroDeviceConnectionTest(mContext, bleAddress);
        try {
            sensoroDeviceConnection.connect(deployAnalyzerModel.blePassword, DeployMonitorDetailActivityPresenter.this);
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissBleConfigDialog();
            getView().updateUploadState(true);
            getView().toastShort(mContext.getString(R.string.ble_connect_failed));
        }
    }

    private void doUploadImages(final double lon, final double lan) {
        if (deployAnalyzerModel.images.size() > 0) {
            //TODO 图片提交
            final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                    .UpLoadPhotoListener() {

                @Override
                public void onStart() {
                    getView().showStartUploadProgressDialog();
                }

                @Override
                public void onComplete(List<ScenesData> scenesDataList) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (ScenesData scenesData : scenesDataList) {
                        scenesData.type = "image";
                        strings.add(scenesData.url);
                    }
                    getView().dismissUploadProgressDialog();
                    LogUtils.loge(this, "上传成功--- size = " + strings.size());
                    //TODO 上传结果
                    doDeployResult(lon, lan, strings);
                }

                @Override
                public void onError(String errMsg) {
                    getView().updateUploadState(true);
                    getView().dismissUploadProgressDialog();
                    getView().toastShort(errMsg);
                }

                @Override
                public void onProgress(String content, double percent) {
                    getView().showUploadProgressDialog(content, percent);
                }
            };
            UpLoadPhotosUtils upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, upLoadPhotoListener);
            upLoadPhotosUtils.doUploadPhoto(deployAnalyzerModel.images);
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
                RetrofitServiceHelper.INSTANCE.doDevicePointDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployContactModel.name, deployContactModel.phone, imgUrls).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
                //TODO 巡检设备更换
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        deployAnalyzerModel.mDeviceDetail.getTaskId(), 1, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress, deployContactModel.name, deployContactModel.phone, imgUrls).
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
                RetrofitServiceHelper.INSTANCE.doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        null, 2, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress, deployContactModel.name, deployContactModel.phone, imgUrls).
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
        deployResultModel.resultCode = resultCode;
        deployResultModel.sn = scanSN;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.errorMsg = errorInfo;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshPoint(DeviceDeployRsp deviceDeployRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.deviceInfo = deviceDeployRsp.getData();
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        //TODO 新版联系人
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
            deployResultModel.contact = deployContactModel.name;
            deployResultModel.phone = deployContactModel.phone;
        }
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.address = deployAnalyzerModel.address;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshStation(DeployStationInfoRsp deployStationInfoRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        DeployStationInfo deployStationInfo = deployStationInfoRsp.getData();
        deployResultModel.name = deployStationInfo.getName();
        deployResultModel.sn = deployStationInfo.getSn();
        deployResultModel.deviceStatus = deployStationInfo.getNormalStatus();
        deployResultModel.updateTime = deployStationInfo.getUpdatedTime();
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.address = deployAnalyzerModel.address;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        deployAnalyzerModel.tagList.clear();
        deployAnalyzerModel.images.clear();
        mHandler.removeCallbacksAndMessages(null);
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
        BleObserver.getInstance().unregisterBleObserver(this);
        BLE_DEVICE_SET.clear();

    }

    public void doNameAddress() {
        Intent intent = new Intent(mContext, DeployMonitorNameAddressActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, deployAnalyzerModel.nameAndAddress);
        }
        intent.putExtra(EXTRA_DEPLOY_TO_SN, deployAnalyzerModel.sn);
        getView().startAC(intent);
    }

    public void doAlarmContact() {
        Intent intent = new Intent(mContext, DeployMonitorAlarmContactActivity.class);
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            intent.putExtra(EXTRA_SETTING_DEPLOY_CONTACT, (ArrayList<DeployContactModel>) deployAnalyzerModel.deployContactModelList);
        }
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
        Intent intent = new Intent(mContext, DeployMonitorSettingPhotoActivity.class);
        if (deployAnalyzerModel.images.size() > 0) {
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, deployAnalyzerModel.images);
        }
        getView().startAC(intent);
    }

    public void doDeployMap() {
        Intent intent = new Intent();
        intent.setClass(mContext, DeployMapActivity.class);
        deployAnalyzerModel.mapSourceType = DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETIAL;
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
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
                    getView().updateContactData(deployAnalyzerModel.deployContactModelList);
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_PHOTO:
                if (data instanceof List) {
                    deployAnalyzerModel.images.clear();
                    deployAnalyzerModel.images.addAll((ArrayList<ImageItem>) data);
                    if (deployAnalyzerModel.images.size() > 0) {
                        getView().setDeployPhotoText(mContext.getString(R.string.added) + deployAnalyzerModel.images.size() + mContext.getString(R.string.images));
                    } else {
                        getView().setDeployPhotoText(mContext.getString(R.string.not_added));
                    }
                }
                break;
            case EVENT_DATA_DEPLOY_MAP:
                if (data instanceof DeployAnalyzerModel) {
                    this.deployAnalyzerModel = (DeployAnalyzerModel) data;
                    freshSignalInfo();
                }
                break;
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
                    DeviceInfo deviceInfo = (DeviceInfo) data;
                    String sn = deviceInfo.getSn();
                    try {
                        if (deployAnalyzerModel.sn.equalsIgnoreCase(sn)) {
                            deployAnalyzerModel.updatedTime = deviceInfo.getUpdatedTime();
                            deployAnalyzerModel.signal = deviceInfo.getSignal();
                            freshSignalInfo();
                            LogUtils.loge(this, "部署页刷新信号 -->> deployMapModel.updatedTime = " + deployAnalyzerModel.updatedTime + ",deployMapModel.signal = " + deployAnalyzerModel.signal);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_WE_CHAT_RELATION:
                if (data instanceof String) {
                    deployAnalyzerModel.weChatAccount = (String) data;
                    getView().setDeployWeChatText(deployAnalyzerModel.weChatAccount);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doConfirm() {
        //TODO 所有逻辑拦击
        //姓名地址校验
        if (checkHasNameAddress()) return;
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
                //TODO 联系人上传
                //联系人校验
                if (checkHasContact()) return;
                if (checkHasPhoto()) return;
                //经纬度校验
                if (checkHasNoLatLng()) return;
                if (checkNeedSignal()) {
                    checkHasForceUploadPermission();
                } else {
                    requestUpload();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 检查是否能强制上传
     */
    private void checkHasForceUploadPermission() {
        String mergeType = WidgetUtil.handleMergeType(deployAnalyzerModel.deviceType);
        if (TextUtils.isEmpty(mergeType)) {
//            if (PreferencesHelper.getInstance().getUserData().hasBadSignalUpload) {
//                getView().showWarnDialog(true);
//            } else {
//                getView().showWarnDialog(false);
//            }
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
     * 检测是否填写过联系人
     *
     * @return
     */
    private boolean checkHasContact() {
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
            if (TextUtils.isEmpty(deployContactModel.name) || TextUtils.isEmpty(deployContactModel.phone)) {
                getView().toastShort(mContext.getString(R.string.please_enter_contact_phone));
                getView().updateUploadState(true);
                return true;
            }
            if (!RegexUtils.checkPhone(deployContactModel.phone)) {
                getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                getView().updateUploadState(true);
                return true;
            }
        } else {
            getView().toastShort(mContext.getString(R.string.please_enter_contact_phone));
            getView().updateUploadState(true);
            return true;
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
        if (deployAnalyzerModel.images.size() == 0 && deployAnalyzerModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
            getView().toastShort(mContext.getString(R.string.please_add_at_least_one_image));
            getView().updateUploadState(true);
            return true;
        }
        return false;
    }

    private void freshSignalInfo() {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - deployAnalyzerModel.updatedTime;
        int resId = 0;
        if (deployAnalyzerModel.signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (deployAnalyzerModel.signal) {
                case "good":
                    signal_text = mContext.getString(R.string.signal_excellent);
                    resId = R.drawable.shape_signal_good;
                    break;
                case "normal":
                    signal_text = mContext.getString(R.string.signal_good);
                    resId = R.drawable.shape_signal_normal;
                    break;
                case "bad":
                    signal_text = mContext.getString(R.string.signal_weak);
                    resId = R.drawable.shape_signal_bad;
                    break;
            }
        } else {
            signal_text = mContext.getString(R.string.no_signal);
            resId = R.drawable.shape_signal_none;
        }
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                if (deployAnalyzerModel.latLng.size() != 2) {
                    getView().refreshSignal(true, signal_text, resId, mContext.getString(R.string.not_positioned));
                } else {
                    getView().refreshSignal(true, signal_text, resId, mContext.getString(R.string.positioned));
                }
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                if (deployAnalyzerModel.latLng.size() != 2) {
                    getView().refreshSignal(false, signal_text, resId, mContext.getString(R.string.not_positioned));
                } else {
                    getView().refreshSignal(false, signal_text, resId, mContext.getString(R.string.positioned));
                }
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }


    }

    /**
     * 检查信号状态
     *
     * @return
     */
    private boolean checkNeedSignal() {
        long time_diff = System.currentTimeMillis() - deployAnalyzerModel.updatedTime;
        if (deployAnalyzerModel.signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (deployAnalyzerModel.signal) {
                case "good":
                case "normal":
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().updateBleConfigDialogMessage(mContext.getString(R.string.loading_configuration_file));
                sensoroDeviceConnection.writeData05ChannelMask(deployAnalyzerModel.channelMask, new SensoroWriteCallback() {
                    @Override
                    public void onWriteSuccess(Object o, int cmd) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().dismissBleConfigDialog();
                                sensoroDeviceConnection.disconnect();
                                doUploadImages(deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1));
                            }
                        });

                    }

                    @Override
                    public void onWriteFailure(int errorCode, int cmd) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().dismissBleConfigDialog();
                                getView().updateUploadState(true);
                                getView().toastShort(mContext.getString(R.string.ble_connect_failed));
                            }
                        });

                    }
                });
            }
        });


    }

    @Override
    public void onConnectedFailure(int errorCode) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissBleConfigDialog();
                getView().updateUploadState(true);
                getView().toastShort(mContext.getString(R.string.ble_connect_failed));
            }
        });


    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onNewDevice(final BLEDevice bleDevice) {
        BLE_DEVICE_SET.add(bleDevice.getSn());
        if (TextUtils.isEmpty(bleAddress)) {
            if (bleDevice.getSn().equals(deployAnalyzerModel.sn)) {
                bleAddress = bleDevice.getMacAddress();
            }
        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {
        try {
            BLE_DEVICE_SET.remove(bleDevice.getSn());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (BLEDevice device : deviceList) {
            if (device != null) {
                stringBuilder.append(device.getSn()).append(",");
                BLE_DEVICE_SET.add(device.getSn());
                if (TextUtils.isEmpty(bleAddress)) {
                    if (device.getSn().equals(deployAnalyzerModel.sn)) {
                        bleAddress = device.getMacAddress();
                    }
                }
            }
        }
        LogUtils.loge("onUpdateDevices = " + stringBuilder.toString());
    }

    @Override
    public void run() {
        bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled();
        if (bleHasOpen) {
            try {
                bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
            } catch (Exception e) {
                e.printStackTrace();
                getView().showBleTips();
            }
            if (bleHasOpen) {
                getView().hideBleTips();
            } else {
                getView().showBleTips();
            }
        } else {
            getView().showBleTips();
        }
        mHandler.postDelayed(this, 2000);
        getView().setDeployDeviceDetailFixedPointNearVisible(BLE_DEVICE_SET.contains(deployAnalyzerModel.sn));

    }

    public void doWeChatRelation() {
        Intent intent = new Intent(mContext, DeployMonitorWeChatRelationActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.weChatAccount)) {
            intent.putExtra(EXTRA_SETTING_WE_CHAT_RELATION, deployAnalyzerModel.weChatAccount);
        }
        intent.putExtra(EXTRA_DEPLOY_TO_SN, deployAnalyzerModel.sn);
        getView().startAC(intent);
    }
}
