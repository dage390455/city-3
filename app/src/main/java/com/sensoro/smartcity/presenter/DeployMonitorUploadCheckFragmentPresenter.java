package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnDestroy;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployContactModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeployStationInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.RegexUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMonitorAlarmContactActivity;
import com.sensoro.smartcity.activity.DeployMonitorCheckActivity;
import com.sensoro.smartcity.activity.DeployMonitorNameAddressActivity;
import com.sensoro.smartcity.activity.DeployMonitorWeChatRelationActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.imainviews.IDeployMonitorUploadCheckFragmentView;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class DeployMonitorUploadCheckFragmentPresenter extends BasePresenter<IDeployMonitorUploadCheckFragmentView> implements IOnCreate, IOnDestroy {
    private DeployMonitorCheckActivity mActivity;
    private CharSequence originName;
    private DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (DeployMonitorCheckActivity) context;
        DeployAnalyzerModel deployAnalyzer = mActivity.getDeployAnalyzerModel();
        if (deployAnalyzer == null) {
            getView().toastLong(mActivity.getString(R.string.unknown));
            return;
        }
        deployAnalyzerModel = deployAnalyzer;
        originName = deployAnalyzerModel.nameAndAddress;
        onCreate();
        init();


    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    private void init() {
        switch (deployAnalyzerModel.deployType) {
            case Constants.TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().setAlarmContactAndPicAndMiniProgramVisible(false);
                getView().setDeployPhotoVisible(false);
                getView().setDeviceSn(mActivity.getString(R.string.device_number) + deployAnalyzerModel.sn);
                if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
                    getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
                    getView().setUploadBtnStatus(true);
                }
                getView().setDeployDeviceType(mActivity.getString(R.string.station));
                getView().updateTagsData(deployAnalyzerModel.tagList);
                break;
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().setAlarmContactAndPicAndMiniProgramVisible(true);
                getView().setDeployPhotoVisible(true);
                echoDeviceInfo();
                break;
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //巡检设备更换
                getView().setAlarmContactAndPicAndMiniProgramVisible(true);
                getView().setDeployPhotoVisible(true);
                getView().updateUploadTvText(mActivity.getString(R.string.replacement_equipment));
                echoDeviceInfo();
                getView().setDeployDetailArrowWeChatVisible(false);
                break;
            case Constants.TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }
    }

    private void echoDeviceInfo() {
        getView().setDeviceSn(mActivity.getString(R.string.device_number) + deployAnalyzerModel.sn);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
        }
        String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
        getView().setDeployDeviceType(deviceTypeName);
        if (!AppUtils.isChineseLanguage()) {
            //TODO 英文版控制不显示小程序账号
            deployAnalyzerModel.weChatAccount = null;
        }
        getView().updateContactData(deployAnalyzerModel.deployContactModelList);
        getView().updateTagsData(deployAnalyzerModel.tagList);
        getView().setUploadBtnStatus(checkCanUpload());
        getView().setDeployWeChatText(deployAnalyzerModel.weChatAccount);
        try {
            LogUtils.loge("channelMask--->> " + deployAnalyzerModel.channelMask.size());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private boolean checkCanUpload() {
        String name_default = mActivity.getString(R.string.tips_hint_name_address);
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
            case Constants.TYPE_SCAN_DEPLOY_STATION:
                break;
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
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
                if (getRealImageSize() == 0 && deployAnalyzerModel.deployType != Constants.TYPE_SCAN_DEPLOY_STATION) {
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_DEPLOY_RESULT_FINISH:
            case Constants.EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE:
            case Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS:
                if (data instanceof String) {
                    deployAnalyzerModel.nameAndAddress = (String) data;
                    getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
                }
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_TAG:
                if (data instanceof List) {
                    deployAnalyzerModel.tagList.clear();
                    deployAnalyzerModel.tagList.addAll((List<String>) data);
                    getView().updateTagsData(deployAnalyzerModel.tagList);
                }
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_CONTACT:
                if (data instanceof List) {
                    //TODO 联系人
                    deployAnalyzerModel.deployContactModelList.clear();
                    deployAnalyzerModel.deployContactModelList.addAll((List<DeployContactModel>) data);
                    getView().updateContactData(deployAnalyzerModel.deployContactModelList);
                }
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_PHOTO:
                if (data instanceof List) {
                    deployAnalyzerModel.images.clear();

                    deployAnalyzerModel.images.addAll((ArrayList<ImageItem>) data);

                    if (getRealImageSize() > 0) {
                        getView().setDeployPhotoText(mActivity.getString(R.string.added) + getRealImageSize() + mActivity.getString(R.string.images));
                    } else {
                        getView().setDeployPhotoText(mActivity.getString(R.string.not_added));
                    }
                    getView().setUploadBtnStatus(checkCanUpload());
                }
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_WE_CHAT_RELATION:
                if (data instanceof String) {
                    String weChatAccount = (String) data;
                    if (TextUtils.isEmpty(weChatAccount)) {
                        deployAnalyzerModel.weChatAccount = null;
                    } else {
                        deployAnalyzerModel.weChatAccount = weChatAccount;
                    }
                    getView().setDeployWeChatText(deployAnalyzerModel.weChatAccount);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doNameAddress() {
        Intent intent = new Intent(mActivity, DeployMonitorNameAddressActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            intent.putExtra(Constants.EXTRA_SETTING_NAME_ADDRESS, deployAnalyzerModel.nameAndAddress);
        }
        getView().startAC(intent);
    }

    public void doTag() {
        Bundle bundle = new Bundle();
        if (deployAnalyzerModel.tagList.size() > 0) {
            bundle.putStringArrayList(Constants.EXTRA_SETTING_TAG_LIST, (ArrayList<String>) deployAnalyzerModel.tagList);
        }
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_DEVICE_TAG,bundle,mActivity);
    }

    public void doSettingPhoto() {
        Bundle bundle = new Bundle();
        if (getRealImageSize() > 0) {
            bundle.putSerializable(Constants.EXTRA_DEPLOY_TO_PHOTO, deployAnalyzerModel.images);
        }
        bundle.putString(Constants.EXTRA_SETTING_DEPLOY_DEVICE_TYPE, deployAnalyzerModel.deviceType);
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_DEVICE_PIC,bundle,mActivity);

//        Intent intent = new Intent(mActivity, DeployMonitorDeployPicActivity.class);
//        if (getRealImageSize() > 0) {
//            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, deployAnalyzerModel.images);
//        }
//        intent.putExtra(EXTRA_SETTING_DEPLOY_DEVICE_TYPE, deployAnalyzerModel.deviceType);
//        getView().startAC(intent);
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

    public void doAlarmContact() {
        Intent intent = new Intent(mActivity, DeployMonitorAlarmContactActivity.class);
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            intent.putExtra(Constants.EXTRA_SETTING_DEPLOY_CONTACT, (ArrayList<DeployContactModel>) deployAnalyzerModel.deployContactModelList);
        }
        getView().startAC(intent);
    }

    public void doWeChatRelation() {
        if (deployAnalyzerModel.deployType == Constants.TYPE_SCAN_DEPLOY_DEVICE) {
            Intent intent = new Intent(mActivity, DeployMonitorWeChatRelationActivity.class);
            if (!TextUtils.isEmpty(deployAnalyzerModel.weChatAccount)) {
                intent.putExtra(Constants.EXTRA_SETTING_WE_CHAT_RELATION, deployAnalyzerModel.weChatAccount);
            }
            intent.putExtra(Constants.EXTRA_DEPLOY_TO_SN, deployAnalyzerModel.sn);
            getView().startAC(intent);
        }
    }

    public void doConfirm() {
        //姓名地址校验
        switch (deployAnalyzerModel.deployType) {
            case Constants.TYPE_SCAN_DEPLOY_STATION:
//                if (checkHasPhoto()) return;
//                //经纬度校验
//                if (checkHasNoLatLng()) return;
                requestUpload();
                break;
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //联系人校验
                if (checkHasContact()) return;
                if (checkHasPhoto()) return;
                requestUpload();
                break;
            default:
                break;
        }
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
                getView().toastShort(mActivity.getString(R.string.please_enter_contact_phone));
                getView().setUploadBtnStatus(true);
                return true;
            }
            if (!RegexUtils.checkPhone(deployContactModel.phone)) {
                getView().toastShort(mActivity.getResources().getString(R.string.tips_phone_empty));
                getView().setUploadBtnStatus(true);
                return true;
            }
        } else {
            getView().toastShort(mActivity.getString(R.string.please_enter_contact_phone));
            getView().setUploadBtnStatus(true);
            return true;
        }
        return false;
    }

    private void requestUpload() {
        final double lon = deployAnalyzerModel.latLng.get(0);
        final double lan = deployAnalyzerModel.latLng.get(1);
        switch (deployAnalyzerModel.deployType) {
            case Constants.TYPE_SCAN_DEPLOY_STATION:
                //基站部署
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().doStationDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<ResponseResult<DeployStationInfo>>(this) {

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().setUploadBtnStatus(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(deployAnalyzerModel.sn, null, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                                } else {
                                    freshError(deployAnalyzerModel.sn, errorMsg, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                                }
                            }

                            @Override
                            public void onCompleted(ResponseResult<DeployStationInfo> deployStationInfoRsp) {
                                freshStation(deployStationInfoRsp);
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }
                        });
                break;
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                doUploadImages(lon, lan);
                break;
            default:
                break;
        }
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
                        getView().setUploadBtnStatus(true);
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
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
                //设备部署
                getView().showProgressDialog();
                //TODO 添加电气火灾配置支持
                boolean isFire = Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
                //暂时添加 后续可以删除
                DeployControlSettingData settingData = null;
                if (isFire) {
                    settingData = deployAnalyzerModel.settingData;
                }
                RetrofitServiceHelper.getInstance().doDevicePointDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployContactModel.name, deployContactModel.phone, deployAnalyzerModel.weChatAccount, imgUrls, settingData, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<ResponseResult<DeviceInfo>>(this) {
                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().setUploadBtnStatus(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(deployAnalyzerModel.sn, null, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                                } else {
                                    freshError(deployAnalyzerModel.sn, errorMsg, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                                }
                            }

                            @Override
                            public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                                freshPoint(deviceDeployRsp);
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }
                        });
                break;
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        deployAnalyzerModel.mDeviceDetail.getTaskId(), 1, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                        deployContactModel.name, deployContactModel.phone, imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceInfo>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                        freshPoint(deviceDeployRsp);
                        getView().dismissProgressDialog();
                        getView().finishAc();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().setUploadBtnStatus(true);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(deployAnalyzerModel.sn, null, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                        } else {
                            freshError(deployAnalyzerModel.sn, errorMsg, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                        }
                    }
                });
                break;
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                        null, 2, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress, deployContactModel.name,
                        deployContactModel.phone, imgUrls, null, deployAnalyzerModel.forceReason, deployAnalyzerModel.status, deployAnalyzerModel.currentSignalQuality).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceInfo>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<DeviceInfo> deviceDeployRsp) {
                        //
                        freshPoint(deviceDeployRsp);
                        getView().dismissProgressDialog();
                        getView().finishAc();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().setUploadBtnStatus(true);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(deployAnalyzerModel.sn, null, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                        } else {
                            freshError(deployAnalyzerModel.sn, errorMsg, Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                        }
                    }
                });
                break;
            default:
                break;
        }

    }

    private void freshPoint(ResponseResult<DeviceInfo> deviceDeployRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        DeviceInfo deviceInfo = deviceDeployRsp.getData();
        deployResultModel.deviceInfo = deviceInfo;
        Intent intent = new Intent(mActivity, DeployResultActivity.class);
        //
        deployResultModel.sn = deviceInfo.getSn();
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.resultCode = Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.wxPhone = deployAnalyzerModel.weChatAccount;
        deployResultModel.settingData = deployAnalyzerModel.settingData;
        //TODO 新版联系人
//        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
//            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
//            deployResultModel.contact = deployContactModel.name;
//            deployResultModel.phone = deployContactModel.phone;
//        }

        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
//            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
//            deployResultModel.contact = deployContactModel.name;
//            deployResultModel.phone = deployContactModel.phone;

            deployResultModel.deployContactModelList.addAll(deployAnalyzerModel.deployContactModelList);
        }
        deployResultModel.address = deployAnalyzerModel.address;
        deployResultModel.updateTime = deviceInfo.getUpdatedTime();
        deployResultModel.deployTime = deviceInfo.getDeployTime();
        deployResultModel.deviceStatus = deviceInfo.getStatus();
        deployResultModel.signal = deviceInfo.getSignal();
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;
        intent.putExtra(Constants.EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshStation(ResponseResult<DeployStationInfo> deployStationInfoRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        //
        Intent intent = new Intent(mActivity, DeployResultActivity.class);
        DeployStationInfo deployStationInfo = deployStationInfoRsp.getData();
        deployResultModel.name = deployStationInfo.getName();
        deployResultModel.sn = deployStationInfo.getSn();
        deployResultModel.deviceType = deployAnalyzerModel.deviceType;
        deployResultModel.stationStatus = deployStationInfo.getNormalStatus();
        deployResultModel.updateTime = deployStationInfo.getUpdatedTime();
        deployResultModel.resultCode = Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.address = deployAnalyzerModel.address;
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            deployResultModel.deployContactModelList.addAll(deployAnalyzerModel.deployContactModelList);
        }
        deployResultModel.signal = deployAnalyzerModel.signal;
        intent.putExtra(Constants.EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshError(String scanSN, String errorInfo, int resultCode) {
        //
        Intent intent = new Intent();
        intent.setClass(mActivity, DeployResultActivity.class);
        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.sn = scanSN;
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
        intent.putExtra(Constants.EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private boolean checkHasPhoto() {
        if (getRealImageSize() == 0 && deployAnalyzerModel.deployType != Constants.TYPE_SCAN_DEPLOY_STATION) {
            getView().toastShort(mActivity.getString(R.string.please_add_at_least_one_image));
            getView().setUploadBtnStatus(true);
            return true;
        }
        return false;
    }
}
