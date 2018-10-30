package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.lzy.imagepicker.bean.ImageItem;
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
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.DeployMapModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeployDeviceDetailRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.RegexUtils;
import com.sensoro.smartcity.widget.popup.UpLoadPhotosUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployMonitorDetailActivityPresenter extends BasePresenter<IDeployMonitorDetailActivityView> implements IOnCreate, Constants
        , SensoroConnectionCallback, BLEDeviceListener<BLEDevice>, Runnable {
    private Activity mContext;
    private DeployMapModel deployMapModel = new DeployMapModel();
    private final List<String> tagList = new ArrayList<>();
    private final List<DeployContactModel> deployContactModelList = new ArrayList<>();
    //新设备
    private DeviceInfo mDeviceInfo;
    private final ArrayList<ImageItem> images = new ArrayList<>();
    private String mNameAndAddress;
    //旧设备
    private InspectionTaskDeviceDetail mDeviceDetail;
    private SensoroDeviceConnectionTest sensoroDeviceConnection;
    private List<Integer> channelMask;
    private String blePassword;
    private boolean isAgainUpLoad = false;
    private Handler mHandler;
    private String bleAddress;
    private boolean isAutoConnect = false;
    private boolean bleHasOpen;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        onCreate();
        Intent intent = mContext.getIntent();
        mDeviceInfo = (DeviceInfo) intent.getSerializableExtra(EXTRA_DEVICE_INFO);
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO);
        deployMapModel.deployType = intent.getIntExtra(EXTRA_SCAN_ORIGIN_TYPE, -1);
        init();
        if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
            mHandler.post(this);
        }
        BleObserver.getInstance().registerBleObserver(this);

    }

    private void getOldDeviceInfo() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(mDeviceDetail.getSn(), null, 1).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    getView().toastShort(errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    getView().toastShort(errorMsg);
                } else {
                    getView().toastShort(errorMsg);
                }
            }

            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                getView().dismissProgressDialog();
                try {
                    if (deviceInfoListRsp.getData().size() > 0) {
                        DeviceInfo deviceInfo = deviceInfoListRsp.getData().get(0);
                        if (deviceInfo != null) {
                            mDeviceInfo = deviceInfo;
                        }
                        freshInspectionDevice();
                    } else {
                        getView().toastShort("未查找到旧设备信息");
//                        freshDevice();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
        if (mDeviceInfo == null) {
            Intent intent = new Intent();
            intent.setClass(mContext, DeployResultActivity.class);
            intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, deployMapModel.deployType);
            intent.putExtra(EXTRA_SENSOR_RESULT, -1);
            getView().startAC(intent);
        } else {
            switch (deployMapModel.deployType) {
                case TYPE_SCAN_DEPLOY_STATION:
                    //基站部署
                    getView().setDeployContactRelativeLayoutVisible(false);
                    getView().setDeployDeviceRlSignalVisible(false);
                    getView().setDeployPhotoVisible(false);

                    deployMapModel.sn = mDeviceInfo.getSn();
                    getView().setDeviceTitleName(deployMapModel.sn);
                    mNameAndAddress = mDeviceInfo.getName();
                    if (!TextUtils.isEmpty(mNameAndAddress)) {
//                不为空设置地址
                        getView().setNameAddressText(mNameAndAddress);
//                name = mContext.getResources().getString(R.string.tips_hint_name_address_set);
                    }
                    break;
                case TYPE_SCAN_DEPLOY_DEVICE:
                    //设备部署
                    getView().setDeployContactRelativeLayoutVisible(true);
                    getView().setDeployDeviceRlSignalVisible(true);
                    getView().setDeployPhotoVisible(true);
                    deployMapModel.sn = mDeviceInfo.getSn();
                    freshDevice();
                    break;
                case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                    //巡检设备更换
                    getView().setDeployContactRelativeLayoutVisible(true);
                    getView().setDeployDeviceRlSignalVisible(true);
                    getView().setDeployPhotoVisible(true);
                    deployMapModel.sn = mDeviceInfo.getSn();
                    getView().updateUploadTvText("更换设备");
//                    if (mDeviceDetail != null) {
//                        freshInspectionDevice();
//                    } else {
                    getOldDeviceInfo();
//                    }
                    break;
                case TYPE_SCAN_INSPECTION:
                    //扫描巡检设备
                    break;
                default:
                    break;
            }
//            String tags[] = mDeviceInfo.getTags();
//            if (tags != null) {
//                for (String tag : tags) {
//                    if (!TextUtils.isEmpty(tag)) {
//                        tagList.add(tag);
//                    }
//                }
//                getView().updateTagsData(tagList);
//            }
            //
            getView().updateUploadState(true);
        }
    }

    private void freshDevice() {
        getView().setDeviceTitleName(deployMapModel.sn);
        mNameAndAddress = mDeviceInfo.getName();
        if (!TextUtils.isEmpty(mNameAndAddress)) {
//                不为空设置地址
            getView().setNameAddressText(mNameAndAddress);
//                name = mContext.getResources().getString(R.string.tips_hint_name_address_set);
        }
        if (mDeviceInfo.getAlarms() != null) {
            AlarmInfo alarmInfo = mDeviceInfo.getAlarms();
            AlarmInfo.NotificationInfo notification = alarmInfo.getNotification();
            if (notification != null) {
                //TODO 设置多个联系人
                String contact = notification.getContact();
                String content = notification.getContent();
                if (TextUtils.isEmpty(contact) || TextUtils.isEmpty(content)) {
//                        getView().setContactEditText(mContext.getResources().getString(R.string.tips_hint_contact));
                } else {
                    deployContactModelList.clear();
                    DeployContactModel deployContactModel = new DeployContactModel();
                    deployContactModel.name = contact;
                    deployContactModel.phone = content;
                    deployContactModelList.add(deployContactModel);
                    getView().updateContactData(deployContactModelList);
                }

            }
        }
        deployMapModel.signal = mDeviceInfo.getSignal();
        deployMapModel.updatedTime = mDeviceInfo.getUpdatedTime();
        freshSignalInfo();
    }

    private void freshInspectionDevice() {
        getView().setDeviceTitleName(deployMapModel.sn);
        mNameAndAddress = mDeviceInfo.getName();
        if (!TextUtils.isEmpty(mNameAndAddress)) {
//                不为空设置地址
            getView().setNameAddressText(mNameAndAddress);
//                name = mContext.getResources().getString(R.string.tips_hint_name_address_set);
        }
        if (mDeviceInfo.getAlarms() != null) {
            AlarmInfo alarmInfo = mDeviceInfo.getAlarms();
            AlarmInfo.NotificationInfo notification = alarmInfo.getNotification();
            if (notification != null) {
                //TODO 设置多个联系人
                String contact = notification.getContact();
                String content = notification.getContent();
                if (TextUtils.isEmpty(contact) || TextUtils.isEmpty(content)) {
//                        getView().setContactEditText(mContext.getResources().getString(R.string.tips_hint_contact));
                } else {
                    deployContactModelList.clear();
                    DeployContactModel deployContactModel = new DeployContactModel();
                    deployContactModel.name = contact;
                    deployContactModel.phone = content;
                    deployContactModelList.add(deployContactModel);
                    getView().updateContactData(deployContactModelList);
                }

            }
        }

//        String[] tags = mDeviceInfo.getTags();
//        if(tags != null && tags.length > 0){
//            tagList.clear();
//            tagList.addAll(Arrays.asList(tags));
//            getView().updateTagsData(tagList);
//        }
        String tags[] = mDeviceInfo.getTags();
        if (tags != null && tags.length > 0) {
            tagList.clear();
            for (String tag : tags) {
                if (!TextUtils.isEmpty(tag)) {
                    tagList.add(tag);
                }
            }
            getView().updateTagsData(tagList);
        }
        double[] lonlat = mDeviceInfo.getLonlat();
        if (lonlat != null && lonlat[0] != 0 && lonlat[1] != 0) {
            deployMapModel.latLng = new LatLng(lonlat[1], lonlat[0]);

        }
        deployMapModel.signal = mDeviceInfo.getSignal();
        deployMapModel.updatedTime = mDeviceInfo.getUpdatedTime();
        freshSignalInfo();
    }

    //
    public void requestUpload() {
        final double lon = deployMapModel.latLng.longitude;
        final double lan = deployMapModel.latLng.latitude;
        switch (deployMapModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doStationDeploy(deployMapModel.sn, lon, lan, tagList, mNameAndAddress).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<StationInfoRsp>(this) {

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().updateUploadState(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(deployMapModel.sn, null);
                                } else {
                                    freshError(deployMapModel.sn, errorMsg);
                                }
                            }

                            @Override
                            public void onCompleted(StationInfoRsp stationInfoRsp) {
                                freshStation(stationInfoRsp);
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }
                        });
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
            case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                //巡检设备更换
                if (PreferencesHelper.getInstance().getUserData().hasSignalConfig) {
                    changeDevice(lon, lan);
                } else {
                    doUploadImages(deployMapModel.latLng.longitude, deployMapModel.latLng.longitude);
                }

                //doUploadImages(lon, lan);
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }
    }

    private void changeDevice(double lon, double lan) {
        getView().showBleConfigDialog();
        if (isAgainUpLoad) {
            if (!TextUtils.isEmpty(blePassword) && channelMask != null && channelMask.size() > 0) {
                if (!TextUtils.isEmpty(bleAddress)) {
                    connectDevice();
                } else {
                    isAutoConnect = true;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getView().updateBleConfigDialogMessage("搜索中，请稍后...");
                        }
                    });

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isAutoConnect = false;
                            getView().dismissBleConfigDialog();
                            getView().toastShort("未搜索到设备，请重新上传");
                            stopScanService();
                            getView().updateUploadState(true);

                        }
                    }, 120 * 1000);


                }
            } else {
                getView().dismissBleConfigDialog();
                isAgainUpLoad = false;
                changeDevice(lon, lan);
            }
        } else {
            RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(deployMapModel.sn, lon, lan)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>() {
                @Override
                public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                    isAgainUpLoad = true;
                    blePassword = deployDeviceDetailRsp.getData().getBlePassword();
                    channelMask = deployDeviceDetailRsp.getData().getChannelMask();
                    //todo delete
//                    blePassword = "hzmBl4;XTD6*[@}I";
                    if (!TextUtils.isEmpty(blePassword) && channelMask != null && channelMask.size() > 0) {
                        if (!TextUtils.isEmpty(bleAddress)) {
                            connectDevice();
                            stopScanService();
                        } else {
                            getView().dismissBleConfigDialog();
                            getView().toastShort("请激活设备后，再进行上传");
                            getView().updateUploadState(true);
                        }

                    } else {
                        getView().dismissBleConfigDialog();
                        doUploadImages(deployMapModel.latLng.longitude, deployMapModel.latLng.latitude);
                    }

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissBleConfigDialog();
//                        getView().updateUploadState(true);
//                        getView().toastShort("获取配置文件失败，请重试 "+errorMsg);
                    doUploadImages(deployMapModel.latLng.longitude, deployMapModel.latLng.latitude);
                }
            });
        }

    }

    private void connectDevice() {
        mHandler.removeCallbacksAndMessages(null);
        sensoroDeviceConnection = new SensoroDeviceConnectionTest(mContext, bleAddress);
        try {
            sensoroDeviceConnection.connect(blePassword, DeployMonitorDetailActivityPresenter.this);
            stopScanService();

        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissBleConfigDialog();
            getView().updateUploadState(true);
            getView().toastShort("蓝牙连接失败,请重试");
            isAgainUpLoad = false;

        }
    }

    private void stopScanService() {
        SensoroCityApplication.getInstance().bleDeviceManager.stopService();
    }

    private void doUploadImages(final double lon, final double lan) {
        if (images.size() > 0) {
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
            upLoadPhotosUtils.doUploadPhoto(images);
        } else {
            doDeployResult(lon, lan, null);
        }
    }

    private void doDeployResult(double lon, double lan, List<String> imgUrls) {
        DeployContactModel deployContactModel = deployContactModelList.get(0);
        switch (deployMapModel.deployType) {
            case TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doDevicePointDeploy(deployMapModel.sn, lon, lan, tagList, mNameAndAddress,
                        deployContactModel.name, deployContactModel.phone, imgUrls).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<DeviceDeployRsp>(this) {
                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().updateUploadState(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(deployMapModel.sn, null);
                                } else {
                                    freshError(deployMapModel.sn, errorMsg);
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
            case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                //TODO 巡检设备更换
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doInspectionChangeDeviceDeploy(mDeviceDetail.getSn(), deployMapModel.sn,
                        mDeviceDetail.getTaskId(), 1, lon, lan, tagList, mNameAndAddress, deployContactModel.name, deployContactModel.phone, imgUrls).
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
                            freshError(deployMapModel.sn, null);
                        } else {
                            freshError(deployMapModel.sn, errorMsg);
                        }
                    }
                });
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }

    }

    private void freshError(String scanSN, String errorInfo) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSN);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, deployMapModel.deployType);
        if (!TextUtils.isEmpty(errorInfo)) {
            intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorInfo);
        }
        getView().startAC(intent);
    }

    private void freshPoint(DeviceDeployRsp deviceDeployRsp) {
        int errCode = deviceDeployRsp.getErrcode();
        int resultCode = 1;
        if (errCode != ResponseBase.CODE_SUCCESS) {
            resultCode = errCode;
        }
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
//        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        DeviceInfo data = deviceDeployRsp.getData();
        intent.putExtra(EXTRA_DEVICE_INFO, data);
        //TODO 新版联系人
        if (deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployContactModelList.get(0);
            intent.putExtra(EXTRA_SETTING_CONTACT, deployContactModel.name);
            intent.putExtra(EXTRA_SETTING_CONTENT, deployContactModel.phone);
        }
        LogUtils.loge("deployMapModel", deployMapModel.address);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, deployMapModel.deployType);
        intent.putExtra(EXTRA_DEPLOY_SUCCESS_ADDRESS, deployMapModel.address);
        getView().startAC(intent);
    }

    private void freshStation(StationInfoRsp stationInfoRsp) {
        String s = stationInfoRsp.toString();
        LogUtils.loge(s);
        int errCode = stationInfoRsp.getErrcode();
        int resultCode = 1;
        if (errCode != ResponseBase.CODE_SUCCESS) {
            resultCode = errCode;
        }
        //
        StationInfo stationInfo = stationInfoRsp.getData();
        double[] lonLat = stationInfo.getLonlat();
        String name = stationInfo.getName();
        String sn = stationInfo.getSn();
        String[] tags = stationInfo.getTags();
        int normalStatus = stationInfo.getNormalStatus();
        long updatedTime = stationInfo.getUpdatedTime();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSn(sn);
        deviceInfo.setTags(tags);
        deviceInfo.setLonlat(lonLat);
        deviceInfo.setStatus(normalStatus);
        deviceInfo.setUpdatedTime(updatedTime);
        if (!TextUtils.isEmpty(name)) {
            deviceInfo.setName(name);
        }
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, deployMapModel.deployType);
        intent.putExtra(EXTRA_DEPLOY_SUCCESS_ADDRESS, deployMapModel.address);
        LogUtils.loge("deployMapModel", deployMapModel.address);
        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        getView().startAC(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        tagList.clear();
        images.clear();
        mHandler.removeCallbacksAndMessages(null);
        stopScanService();
        BleObserver.getInstance().unregisterBleObserver(this);

    }

    public void doNameAddress() {
        Intent intent = new Intent(mContext, DeployMonitorNameAddressActivity.class);
        if (!TextUtils.isEmpty(mNameAndAddress)) {
            intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, mNameAndAddress);
        }
        if (mDeviceInfo != null) {
            intent.putExtra(EXTRA_DEPLOY_TO_SN, mDeviceInfo.getSn());
        }
        getView().startAC(intent);
    }

    public void doAlarmContact() {
        Intent intent = new Intent(mContext, DeployMonitorAlarmContactActivity.class);
        if (deployContactModelList.size() > 0) {
            intent.putExtra(EXTRA_SETTING_DEPLOY_CONTACT, (ArrayList<DeployContactModel>) deployContactModelList);
        }
        getView().startAC(intent);
    }

    public void doTag() {
        Intent intent = new Intent(mContext, DeployDeviceTagActivity.class);
        if (tagList.size() > 0) {
            intent.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) tagList);
        }
        getView().startAC(intent);
    }

    public void doSettingPhoto() {
        Intent intent = new Intent(mContext, DeployMonitorSettingPhotoActivity.class);
        if (images.size() > 0) {
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, images);
        }
        getView().startAC(intent);
    }

    public void doDeployMap() {
        Intent intent = new Intent();
        intent.setClass(mContext, DeployMapActivity.class);
        intent.putExtra(EXTRA_DEPLOY_TO_MAP, deployMapModel);
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
            case EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS:
                if (data instanceof String) {
                    mNameAndAddress = (String) data;
                    getView().setNameAddressText(mNameAndAddress);
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_TAG:
                if (data instanceof List) {
                    tagList.clear();
                    tagList.addAll((List<String>) data);
                    getView().updateTagsData(tagList);
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_CONTACT:
                if (data instanceof List) {
                    //TODO 联系人
                    deployContactModelList.clear();
                    deployContactModelList.addAll((List<DeployContactModel>) data);
                    getView().updateContactData(deployContactModelList);
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_PHOTO:
                if (data instanceof List) {
                    images.clear();
                    images.addAll((ArrayList<ImageItem>) data);
                    if (images.size() > 0) {
                        getView().setDeployPhotoText("已添加" + images.size() + "张图片");
                    } else {
                        getView().setDeployPhotoText("未添加");
                    }
                }
                break;
            case EVENT_DATA_DEPLOY_MAP:
                if (data instanceof DeployMapModel) {
                    deployMapModel = (DeployMapModel) data;
                    freshSignalInfo();
                }
                break;
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
                    DeviceInfo deviceInfo = (DeviceInfo) data;
                    String sn = deviceInfo.getSn();
                    try {
                        if (deployMapModel.sn.equalsIgnoreCase(sn)) {
                            deployMapModel.updatedTime = deviceInfo.getUpdatedTime();
                            deployMapModel.signal = deviceInfo.getSignal();
                            freshSignalInfo();
                            LogUtils.loge(this, "部署页刷新信号 -->> deployMapModel.updatedTime = " + deployMapModel.updatedTime + ",deployMapModel.signal = " + deployMapModel.signal);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doConfirm() {
        //TODO 所有逻辑拦击
        //姓名地址校验
        //        例：大悦城20层走廊2号配电箱
        String name_default = mContext.getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(mNameAndAddress) || mNameAndAddress.equals(name_default)) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_input_name));
            getView().updateUploadState(true);
            return;
        } else {
            byte[] bytes = new byte[0];
            try {
                bytes = mNameAndAddress.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort("名称/地址最长不能超过16个汉字或48个字符");
                getView().updateUploadState(true);
                return;
            }
        }
        if (images.size() == 0 && deployMapModel.deployType != TYPE_SCAN_DEPLOY_STATION) {
            getView().toastShort("请至少添加一张图片");
            getView().updateUploadState(true);
            return;
        }
        //经纬度校验
        if (deployMapModel.latLng == null) {
            getView().toastShort("请指定部署位置");
            getView().updateUploadState(true);
        } else {
            //TODO 背景选择器
            switch (deployMapModel.deployType) {
                case TYPE_SCAN_DEPLOY_STATION:
                    requestUpload();
                    break;
                case TYPE_SCAN_DEPLOY_DEVICE:
                case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                    //TODO 联系人上传
                    //联系人校验
                    if (deployContactModelList.size() > 0) {
                        DeployContactModel deployContactModel = deployContactModelList.get(0);
                        if (TextUtils.isEmpty(deployContactModel.name) || TextUtils.isEmpty(deployContactModel.phone)) {
                            getView().toastShort("请输入联系人名称和电话号码");
                            getView().updateUploadState(true);
                            return;
                        }
                        if (!RegexUtils.checkPhone(deployContactModel.phone)) {
                            getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                            getView().updateUploadState(true);
                            return;
                        }
                    } else {
                        getView().toastShort("请输入联系人名称和电话号码");
                        getView().updateUploadState(true);
                        return;
                    }
                    if (needRefreshSignal()) {
                        getView().showWarnDialog();
                    } else {
                        requestUpload();
                    }
                    break;
                case TYPE_SCAN_INSPECTION:
                    //扫描巡检设备
                    break;
                default:
                    break;
            }
        }

    }

    private void freshSignalInfo() {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - deployMapModel.updatedTime;
        int resId = 0;
        if (deployMapModel.signal != null && (time_diff < 300000)) {
            switch (deployMapModel.signal) {
                case "good":
                    signal_text = "信号：优";
                    resId = R.drawable.shape_signal_good;
                    break;
                case "normal":
                    signal_text = "信号：良";
                    resId = R.drawable.shape_signal_normal;
                    break;
                case "bad":
                    signal_text = "信号：差";
                    resId = R.drawable.shape_signal_bad;
                    break;
            }
        } else {
            signal_text = "无信号";
            resId = R.drawable.shape_signal_none;
        }
        switch (deployMapModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                if (deployMapModel.latLng == null) {
                    getView().refreshSignal(true, signal_text, resId, "未定位");
                } else {
                    getView().refreshSignal(true, signal_text, resId, "已定位");
                }
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                if (deployMapModel.latLng == null) {
                    getView().refreshSignal(false, signal_text, resId, "未定位");
                } else {
                    getView().refreshSignal(false, signal_text, resId, "已定位");
                }
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }


    }

    private boolean needRefreshSignal() {
        long time_diff = System.currentTimeMillis() - deployMapModel.updatedTime;
        if (deployMapModel.signal != null && (time_diff < 300000)) {
            switch (deployMapModel.signal) {
                case "good":
                case "normal":
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onConnectedSuccess(BLEDevice bleDevice, int cmd) {
        isAgainUpLoad = false;
        isAutoConnect = false;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().updateBleConfigDialogMessage("正在加载配置文件...");
                sensoroDeviceConnection.writeData05ChannelMask(channelMask, new SensoroWriteCallback() {
                    @Override
                    public void onWriteSuccess(Object o, int cmd) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().dismissBleConfigDialog();
                                sensoroDeviceConnection.disconnect();
                                doUploadImages(deployMapModel.latLng.longitude, deployMapModel.latLng.latitude);
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
                                getView().toastShort("蓝牙连接失败，请重试");
                            }
                        });

                    }
                });
            }
        });


    }

    @Override
    public void onConnectedFailure(int errorCode) {
        isAgainUpLoad = false;
        isAutoConnect = false;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().dismissBleConfigDialog();
                getView().updateUploadState(true);
                getView().toastShort("蓝牙连接失败，请重试");
            }
        });


    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onNewDevice(final BLEDevice bleDevice) {
        if (bleDevice.getSn().equals(deployMapModel.sn)) {
            bleAddress = bleDevice.getMacAddress();
            if (isAutoConnect) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(blePassword) && channelMask != null && channelMask.size() > 0) {
                            connectDevice();
                        } else {
                            doUploadImages(deployMapModel.latLng.longitude, deployMapModel.latLng.latitude);
                        }

                    }
                });
            }


        }
    }

    @Override
    public void onGoneDevice(BLEDevice bleDevice) {

    }

    @Override
    public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {

    }

    @Override
    public void run() {
        try {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
            getView().showBleTips();
//            getView().toastShort("请检查蓝牙状态");
        }
        if (!bleHasOpen) {
            bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.enEnableBle();
        }
        if (bleHasOpen) {
            getView().hideBleTips();
        } else {
            getView().showBleTips();
        }
        mHandler.postDelayed(this, 3000);

    }
}
