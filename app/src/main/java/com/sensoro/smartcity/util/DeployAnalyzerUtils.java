package com.sensoro.smartcity.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMonitorDetailActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.InspectionActivity;
import com.sensoro.smartcity.activity.InspectionExceptionDetailActivity;
import com.sensoro.smartcity.activity.ScanLoginResultActivity;
import com.sensoro.smartcity.activity.SignalCheckActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.DeployResultModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeployDeviceInfo;
import com.sensoro.smartcity.server.bean.DeployStationInfo;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetailModel;
import com.sensoro.smartcity.server.response.DeployDeviceDetailRsp;
import com.sensoro.smartcity.server.response.DeployStationInfoRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.InspectionTaskDeviceDetailRsp;
import com.sensoro.smartcity.server.response.ResponseBase;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public enum DeployAnalyzerUtils implements Constants {
    INSTANCE;

    /**
     * 分析来源，返回扫描等结果
     *
     * @param presenter
     * @param scanType
     * @param result
     * @param activity
     * @param inspectionIndexTaskInfo
     * @param oldDeviceDetail
     * @param listener
     */
    public void handlerDeployAnalyzerResult(BasePresenter presenter, final int scanType, final String result, final Activity activity, InspectionIndexTaskInfo inspectionIndexTaskInfo, InspectionTaskDeviceDetail oldDeviceDetail, final OnDeployAnalyzerListener listener) {
        if (TextUtils.isEmpty(result)) {
            listener.onError(0, null, activity.getResources().getString(R.string.please_re_scan_try_again));
            return;
        }
        switch (scanType) {
            //基站部署
            case TYPE_SCAN_DEPLOY_STATION:
                //设备部署
            case TYPE_SCAN_DEPLOY_DEVICE:
                String scanSerialNumber = parseResultMac(result);
                if (TextUtils.isEmpty(scanSerialNumber)) {
                    listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                    return;
                } else {
                    if (scanSerialNumber.length() == 16) {
                        handleDeployDeviceStation(presenter, scanSerialNumber, activity, listener);
                    } else {
                        listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                        return;
                    }
                }
                break;
            case TYPE_SCAN_LOGIN:
                //登录
                if (TextUtils.isEmpty(result)) {
                    listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                    return;
                }
                handleScanLogin(presenter, result, activity, listener);
                break;
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //巡检/故障设备更换
                if (oldDeviceDetail == null) {
                    listener.onError(0, null, null);
                    return;
                }
                String scanNewDeviceSN = parseResultMac(result);
                if (TextUtils.isEmpty(scanNewDeviceSN)) {
                    listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                    return;
                } else {
                    if (scanNewDeviceSN.length() == 16) {
                        if (scanNewDeviceSN.equalsIgnoreCase(oldDeviceDetail.getSn())) {
                            listener.onError(scanType, null, "请使用不同的设备进行更换");
                            return;
                        }
                        handleDeviceDeployChange(scanType, presenter, oldDeviceDetail, scanNewDeviceSN, activity, listener);
                    } else {
                        listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                        return;
                    }
                }
                break;
            case TYPE_SCAN_INSPECTION:
                if (inspectionIndexTaskInfo == null) {
                    listener.onError(0, null, null);
                    return;
                }
                //扫描巡检设备
                String scanInspectionDeviceSn = parseResultMac(result);
                if (TextUtils.isEmpty(scanInspectionDeviceSn)) {
                    listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                    return;
                } else {
                    if (scanInspectionDeviceSn.length() == 16) {
                        handleScanInspectionDevice(presenter, scanInspectionDeviceSn, inspectionIndexTaskInfo.getId(), activity, listener);
                    } else {
                        listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                        return;
                    }
                }
                break;
            case TYPE_SCAN_SIGNAL_CHECK:
                //信号测试
                String signalCheckNum = parseResultMac(result);
                if (TextUtils.isEmpty(signalCheckNum)) {
                    listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                    return;
                } else {
                    if (signalCheckNum.length() == 16) {
                        handleScanSignalCheck(presenter, signalCheckNum, activity, listener);
                    } else {
                        listener.onError(0, null, activity.getResources().getString(R.string.invalid_qr_code));
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleScanSignalCheck(BasePresenter presenter, final String signalCheckNum, final Activity activity, final OnDeployAnalyzerListener listener) {
        RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(signalCheckNum, null, null).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>(presenter) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    listener.onError(errorCode, null, errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    //查找新设备
                    Intent intent = new Intent();
                    intent.setClass(activity, DeployResultActivity.class);
                    DeployResultModel deployResultModel = new DeployResultModel();
                    deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT;
                    deployResultModel.sn = signalCheckNum;
                    deployResultModel.scanType = TYPE_SCAN_SIGNAL_CHECK;
                    intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                    listener.onError(errorCode, intent, errorMsg);
                } else {
                    //TODO 控制逻辑
                    Intent intent = new Intent();
                    intent.setClass(activity, DeployResultActivity.class);
                    DeployResultModel deployResultModel = new DeployResultModel();
                    deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                    deployResultModel.sn = signalCheckNum;
                    deployResultModel.scanType = TYPE_SCAN_SIGNAL_CHECK;
                    deployResultModel.errorMsg = errorMsg;
                    intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                    listener.onError(errorCode, intent, errorMsg);
                }
            }


            @Override
            public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                DeployDeviceInfo data = deployDeviceDetailRsp.getData();
                DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
                deployAnalyzerModel.deployType = TYPE_SCAN_SIGNAL_CHECK;
                deployAnalyzerModel.status = data.getStatus();
                deployAnalyzerModel.updatedTime = data.getUpdatedTime();
                deployAnalyzerModel.nameAndAddress = data.getName();
                deployAnalyzerModel.deviceType = data.getDeviceType();
                deployAnalyzerModel.sn = data.getSn();
                deployAnalyzerModel.blePassword = data.getBlePassword();
                List<String> tags = data.getTags();
                if (tags != null && tags.size() > 0) {
                    deployAnalyzerModel.tagList.clear();
                    deployAnalyzerModel.tagList.addAll(tags);
                }
                Intent intent = new Intent();
                intent.setClass(activity, SignalCheckActivity.class);
                intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
                listener.onSuccess(intent);
            }
        });

    }

    private void handleScanInspectionDevice(BasePresenter presenter, String scanInspectionDevice, String inspectionId, final Activity activity, final OnDeployAnalyzerListener listener) {
        RetrofitServiceHelper.INSTANCE.getInspectionDeviceList(inspectionId, null, scanInspectionDevice.toUpperCase(), null, null, null, null).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<InspectionTaskDeviceDetailRsp>(presenter) {
            @Override
            public void onCompleted(InspectionTaskDeviceDetailRsp inspectionTaskDeviceDetailRsp) {
                InspectionTaskDeviceDetailModel data = inspectionTaskDeviceDetailRsp.getData();
                List<InspectionTaskDeviceDetail> devices = data.getDevices();
                if (devices != null && devices.size() > 0) {
                    InspectionTaskDeviceDetail deviceDetail = devices.get(0);
                    Intent intent = new Intent();
                    int status = deviceDetail.getStatus();
                    switch (status) {
                        case 0:
                            if (PreferencesHelper.getInstance().getUserData().hasInspectionDeviceModify) {
                                intent.setClass(activity, InspectionActivity.class);
                            } else {
                                listener.onError(0, null, activity.getString(R.string.account_no_patrol_device_permissions));
                                return;
                            }
                            break;
                        case 1:
                            listener.onError(0, null, activity.getString(R.string.device_patrolled_status_normal));
                            return;
                        case 2:
                            intent.setClass(activity, InspectionExceptionDetailActivity.class);
                            break;
                    }
                    intent.putExtra(EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL, deviceDetail);
                    listener.onSuccess(intent);
                } else {
                    listener.onError(0, null, activity.getString(R.string.device_not_in_inspection_mission));
                }

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                listener.onError(errorCode, null, errorMsg);
            }
        });
    }

    private void handleDeployDeviceStation(final BasePresenter presenter, final String scanSerialNumber, final Activity activity, final OnDeployAnalyzerListener listener) {
        RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(scanSerialNumber, null, null).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>(presenter) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    listener.onError(errorCode, null, errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    //TODO 控制逻辑
                    doStation();
                } else {
                    //TODO 控制逻辑
                    Intent intent = new Intent();
                    intent.setClass(activity, DeployResultActivity.class);
                    DeployResultModel deployResultModel = new DeployResultModel();
                    deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                    deployResultModel.sn = scanSerialNumber;
                    deployResultModel.scanType = TYPE_SCAN_DEPLOY_DEVICE;
                    deployResultModel.errorMsg = errorMsg;
                    intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                    listener.onError(errorCode, intent, errorMsg);
                }
            }

            private void doStation() {
                RetrofitServiceHelper.INSTANCE.getStationDetail(scanSerialNumber.toUpperCase()).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployStationInfoRsp>(presenter) {
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            listener.onError(errorCode, null, errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            DeployResultModel deployResultModel = new DeployResultModel();
                            deployResultModel.scanType = TYPE_SCAN_DEPLOY_DEVICE;
                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT;
                            deployResultModel.sn = scanSerialNumber;
                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                            listener.onError(errorCode, intent, errorMsg);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            DeployResultModel deployResultModel = new DeployResultModel();
                            deployResultModel.scanType = TYPE_SCAN_DEPLOY_DEVICE;
                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                            deployResultModel.sn = scanSerialNumber;
                            deployResultModel.errorMsg = errorMsg;
                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                            listener.onError(errorCode, intent, errorMsg);
                        }
                    }

                    @Override
                    public void onCompleted(DeployStationInfoRsp deployStationInfoRsp) {
                        DeployStationInfo deployStationInfo = deployStationInfoRsp.getData();
                        try {
                            //todo 包装类
                            DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
                            deployAnalyzerModel.deployType = TYPE_SCAN_DEPLOY_STATION;
                            deployAnalyzerModel.sn = deployStationInfo.getSn();
                            deployAnalyzerModel.nameAndAddress = deployStationInfo.getName();
                            List<Double> lonlat = deployStationInfo.getLonlat();
                            if (lonlat != null && lonlat.size() > 1 && lonlat.get(0) != 0 && lonlat.get(1) != 0) {
                                deployAnalyzerModel.latLng.clear();
                                deployAnalyzerModel.latLng.addAll(lonlat);
                            }
                            List<String> tags = deployStationInfo.getTags();
                            if (tags != null && tags.size() > 0) {
                                deployAnalyzerModel.tagList.clear();
                                deployAnalyzerModel.tagList.addAll(tags);
                            }
                            deployAnalyzerModel.updatedTime = deployStationInfo.getUpdatedTime();
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployMonitorDetailActivity.class);
                            intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
                            listener.onSuccess(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                DeployDeviceInfo data = deployDeviceDetailRsp.getData();
                if (data == null) {
                    doStation();
                } else {
                    String sn = data.getSn();
                    if (TextUtils.isEmpty(sn)) {
                        //拿不到sn认为为空对象
                        doStation();
                    } else {
                        DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
                        List<Double> lonlat = data.getLonlat();
                        if (lonlat != null && lonlat.size() > 1 && lonlat.get(0) != 0 && lonlat.get(1) != 0) {
                            deployAnalyzerModel.latLng.clear();
                            deployAnalyzerModel.latLng.addAll(lonlat);
                            getAllDeviceInfo(deployAnalyzerModel);
                        } else {
                            deployAnalyzerModel.deployType = TYPE_SCAN_DEPLOY_DEVICE;
                            deployAnalyzerModel.sn = sn;
                            deployAnalyzerModel.deviceType = data.getDeviceType();
                            deployAnalyzerModel.nameAndAddress = data.getName();
                            deployAnalyzerModel.notOwn = data.isNotOwn();
                            deployAnalyzerModel.blePassword = data.getBlePassword();
                            deployAnalyzerModel.weChatAccount = data.getWxPhone();

                            deployAnalyzerModel.signal = data.getSignal();
                            List<String> tags = data.getTags();
                            if (tags != null && tags.size() > 0) {
                                deployAnalyzerModel.tagList.clear();
                                deployAnalyzerModel.tagList.addAll(tags);
                            }
                            deployAnalyzerModel.updatedTime = data.getUpdatedTime();
                            AlarmInfo alarmInfo = data.getAlarms();
                            if (alarmInfo != null) {
                                AlarmInfo.NotificationInfo notification = alarmInfo.getNotification();
                                if (notification != null) {
                                    String contact = notification.getContact();
                                    String content = notification.getContent();
                                    if (TextUtils.isEmpty(contact) || TextUtils.isEmpty(content)) {
//                        getView().setContactEditText(mContext.getResources().getString(R.string.tips_hint_contact));
                                    } else {
                                        deployAnalyzerModel.deployContactModelList.clear();
                                        DeployContactModel deployContactModel = new DeployContactModel();
                                        deployContactModel.name = contact;
                                        deployContactModel.phone = content;
                                        deployAnalyzerModel.deployContactModelList.add(deployContactModel);
                                    }

                                }
                            }
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployMonitorDetailActivity.class);
                            intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
                            listener.onSuccess(intent);
                        }
                    }

                }
            }

            private void getAllDeviceInfo(final DeployAnalyzerModel deployAnalyzerModel) {
                RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(scanSerialNumber, deployAnalyzerModel.latLng.get(0), deployAnalyzerModel.latLng.get(1)).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>(presenter) {
                    @Override
                    public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                        DeployDeviceInfo data = deployDeviceDetailRsp.getData();
                        deployAnalyzerModel.deployType = TYPE_SCAN_DEPLOY_DEVICE;
                        deployAnalyzerModel.sn = data.getSn();
                        deployAnalyzerModel.deviceType = data.getDeviceType();
                        deployAnalyzerModel.nameAndAddress = data.getName();
                        deployAnalyzerModel.notOwn = data.isNotOwn();
                        deployAnalyzerModel.blePassword = data.getBlePassword();
                        deployAnalyzerModel.signal = data.getSignal();
                        deployAnalyzerModel.weChatAccount = data.getWxPhone();
                        List<String> tags = data.getTags();
                        if (tags != null && tags.size() > 0) {
                            deployAnalyzerModel.tagList.clear();
                            deployAnalyzerModel.tagList.addAll(tags);
                        }
                        deployAnalyzerModel.updatedTime = data.getUpdatedTime();
                        List<Integer> channelMask = data.getChannelMask();
                        if (channelMask != null && channelMask.size() > 0) {
                            deployAnalyzerModel.channelMask.addAll(channelMask);
                        }
                        AlarmInfo alarmInfo = data.getAlarms();
                        if (alarmInfo != null) {
                            AlarmInfo.NotificationInfo notification = alarmInfo.getNotification();
                            if (notification != null) {
                                String contact = notification.getContact();
                                String content = notification.getContent();
                                if (!TextUtils.isEmpty(contact) && !TextUtils.isEmpty(content)) {
                                    deployAnalyzerModel.deployContactModelList.clear();
                                    DeployContactModel deployContactModel = new DeployContactModel();
                                    deployContactModel.name = contact;
                                    deployContactModel.phone = content;
                                    deployAnalyzerModel.deployContactModelList.add(deployContactModel);
                                }

                            }
                        }
                        Intent intent = new Intent();
                        intent.setClass(activity, DeployMonitorDetailActivity.class);
                        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
                        listener.onSuccess(intent);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            listener.onError(errorCode, null, errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            //TODO 控制逻辑
                            doStation();
                        } else {
                            //TODO 控制逻辑
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            DeployResultModel deployResultModel = new DeployResultModel();
                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                            deployResultModel.sn = scanSerialNumber;
                            deployResultModel.scanType = TYPE_SCAN_DEPLOY_DEVICE;
                            deployResultModel.errorMsg = errorMsg;
                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                            listener.onError(errorCode, intent, errorMsg);
                        }
                    }
                });
            }
        });

    }

    private void handleScanLogin(BasePresenter presenter, final String result, final Activity activity, final OnDeployAnalyzerListener listener) {
        RetrofitServiceHelper.INSTANCE.getLoginScanResult(result).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(presenter) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                listener.onError(errorCode, null, errorMsg);
            }

            @Override
            public void onCompleted(ResponseBase responseBase) {
                if (responseBase.getErrcode() == 0) {
                    try {
                        LogUtils.loge("qrcodeId = " + result);
                        Intent intent = new Intent();
                        intent.setClass(activity, ScanLoginResultActivity.class);
                        intent.putExtra("qrcodeId", result);
                        listener.onSuccess(intent);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onError(0, null, activity.getString(R.string.please_re_scan_try_again));
            }
        });
    }

    private void handleDeviceDeployChange(final int scanType, final BasePresenter presenter, final InspectionTaskDeviceDetail oldDeviceDetail, final String scanSerialNumber, final Activity activity, final OnDeployAnalyzerListener listener) {
        //todo 信息替换
        final DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
        RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(oldDeviceDetail.getSn(), null, null).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>(presenter) {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    listener.onError(errorCode, null, errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    //查找新设备
                    getNesDeviceInfo();
                } else {
                    //TODO 控制逻辑
                    getNesDeviceInfo();
                }
            }

            private void getNesDeviceInfo() {
                Double lon = null;
                Double lat = null;
                if (deployAnalyzerModel.latLng.size() == 2) {
                    lon = deployAnalyzerModel.latLng.get(0);
                    lat = deployAnalyzerModel.latLng.get(1);
                }
                RetrofitServiceHelper.INSTANCE.getDeployDeviceDetail(scanSerialNumber, lon, lat).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployDeviceDetailRsp>(presenter) {
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            listener.onError(errorCode, null, errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            //TODO 控制逻辑
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            DeployResultModel deployResultModel = new DeployResultModel();
                            deployResultModel.scanType = scanType;
                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT;
                            deployResultModel.sn = scanSerialNumber;
                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                            listener.onError(errorCode, intent, errorMsg);
                        } else {
                            //TODO 控制逻辑
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            DeployResultModel deployResultModel = new DeployResultModel();
                            deployResultModel.scanType = scanType;
                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                            deployResultModel.sn = scanSerialNumber;
                            deployResultModel.errorMsg = errorMsg;
                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                            listener.onError(errorCode, intent, errorMsg);
                        }
                    }


                    @Override
                    public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                        DeployDeviceInfo data = deployDeviceDetailRsp.getData();
                        if (data == null) {
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            DeployResultModel deployResultModel = new DeployResultModel();
                            deployResultModel.scanType = scanType;
                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT;
                            deployResultModel.sn = scanSerialNumber;
                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                            listener.onError(0, intent, null);
                        } else {
                            String sn = data.getSn();
                            if (TextUtils.isEmpty(sn)) {
                                //拿不到sn认为为空对象
                                Intent intent = new Intent();
                                intent.setClass(activity, DeployResultActivity.class);
                                DeployResultModel deployResultModel = new DeployResultModel();
                                deployResultModel.scanType = scanType;
                                deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT;
                                deployResultModel.sn = scanSerialNumber;
                                intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                listener.onError(0, intent, null);
                            } else {
                                deployAnalyzerModel.sn = sn;
                                deployAnalyzerModel.notOwn = data.isNotOwn();
                                deployAnalyzerModel.mDeviceDetail = oldDeviceDetail;
                                deployAnalyzerModel.blePassword = data.getBlePassword();

                                String deviceType = data.getDeviceType();
                                if (!TextUtils.isEmpty(deviceType)) {
                                    deployAnalyzerModel.deviceType = deviceType;
                                }
                                List<Integer> channelMask = data.getChannelMask();
                                if (channelMask != null && channelMask.size() > 0) {
                                    deployAnalyzerModel.channelMask.clear();
                                    deployAnalyzerModel.channelMask.addAll(channelMask);
                                }
                                Intent intent = new Intent();
                                intent.setClass(activity, DeployMonitorDetailActivity.class);
                                intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
                                listener.onSuccess(intent);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCompleted(DeployDeviceDetailRsp deployDeviceDetailRsp) {
                DeployDeviceInfo data = deployDeviceDetailRsp.getData();
                deployAnalyzerModel.deployType = scanType;
                deployAnalyzerModel.nameAndAddress = data.getName();
                deployAnalyzerModel.deviceType = data.getDeviceType();
                deployAnalyzerModel.weChatAccount = data.getWxPhone();
                List<Double> lonlat = data.getLonlat();
                if (lonlat != null && lonlat.size() > 1 && lonlat.get(0) != 0 && lonlat.get(1) != 0) {
                    deployAnalyzerModel.latLng.clear();
                    deployAnalyzerModel.latLng.addAll(lonlat);
                }
                deployAnalyzerModel.signal = data.getSignal();
                List<String> tags = data.getTags();
                if (tags != null && tags.size() > 0) {
                    deployAnalyzerModel.tagList.clear();
                    deployAnalyzerModel.tagList.addAll(tags);
                }
                deployAnalyzerModel.updatedTime = data.getUpdatedTime();
                AlarmInfo alarmInfo = data.getAlarms();
                if (alarmInfo != null) {
                    AlarmInfo.NotificationInfo notification = alarmInfo.getNotification();
                    if (notification != null) {
                        String contact = notification.getContact();
                        String content = notification.getContent();
                        if (!TextUtils.isEmpty(contact) && !TextUtils.isEmpty(content)) {
                            deployAnalyzerModel.deployContactModelList.clear();
                            DeployContactModel deployContactModel = new DeployContactModel();
                            deployContactModel.name = contact;
                            deployContactModel.phone = content;
                            deployAnalyzerModel.deployContactModelList.add(deployContactModel);
                        }
                    }
                }

                getNesDeviceInfo();
            }
        });
    }

    public interface OnDeployAnalyzerListener {
        void onSuccess(Intent intent);

        void onError(int errType, Intent intent, String errMsg);

    }

    /**
     * 逻辑处理嵌套太多 暂时不用 以后处理
     *
     * @param presenter
     * @param activity
     * @param deployAnalyzerModel
     * @param imgUrls
     * @param listener
     */
    public void submitDeploymentResult(BasePresenter presenter, final Activity activity, final DeployAnalyzerModel deployAnalyzerModel, List<String> imgUrls, final OnDeployAnalyzerListener listener) {
        if (deployAnalyzerModel.latLng.size() == 2) {
            Double lon = deployAnalyzerModel.latLng.get(0);
            Double lan = deployAnalyzerModel.latLng.get(1);
            if (lon != 0 && lan != 0) {
                switch (deployAnalyzerModel.deployType) {
                    case TYPE_SCAN_DEPLOY_STATION:
                        //基站部署
                        RetrofitServiceHelper.INSTANCE.doStationDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CityObserver<DeployStationInfoRsp>(presenter) {
                                    @Override
                                    public void onErrorMsg(int errorCode, String errorMsg) {
                                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                            listener.onError(errorCode, null, errorMsg);
                                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                                            freshErrorResultNotUnderAccount(errorCode, errorMsg, activity, deployAnalyzerModel, listener);
                                        } else {
                                            Intent intent = new Intent();
                                            intent.setClass(activity, DeployResultActivity.class);
                                            DeployResultModel deployResultModel = new DeployResultModel();
                                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                                            deployResultModel.sn = deployAnalyzerModel.sn;
                                            deployResultModel.scanType = TYPE_SCAN_DEPLOY_STATION;
                                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                            listener.onError(errorCode, intent, errorMsg);
                                        }
                                    }

                                    @Override
                                    public void onCompleted(DeployStationInfoRsp deployStationInfoRsp) {
                                        Intent intent = new Intent(activity, DeployResultActivity.class);
                                        DeployResultModel deployResultModel = new DeployResultModel();
                                        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
                                        DeployStationInfo deployStationInfo = deployStationInfoRsp.getData();
                                        deployResultModel.name = deployStationInfo.getName();
                                        deployResultModel.sn = deployStationInfo.getSn();
                                        deployResultModel.stationStatus = deployStationInfo.getNormalStatus();
                                        deployResultModel.updateTime = deployStationInfo.getUpdatedTime();
                                        deployResultModel.scanType = TYPE_SCAN_DEPLOY_STATION;
                                        deployResultModel.address = deployAnalyzerModel.address;
                                        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                        listener.onSuccess(intent);
                                    }
                                });


                        break;
                    case TYPE_SCAN_DEPLOY_DEVICE:
                        //设备部署
                        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
                            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
                            RetrofitServiceHelper.INSTANCE.doDevicePointDeploy(deployAnalyzerModel.sn, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                                    deployContactModel.name, deployContactModel.phone, deployAnalyzerModel.weChatAccount, imgUrls, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CityObserver<DeviceDeployRsp>(presenter) {
                                        @Override
                                        public void onErrorMsg(int errorCode, String errorMsg) {
                                            if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                                listener.onError(errorCode, null, errorMsg);
                                            } else if (errorCode == 4013101 || errorCode == 4000013) {
                                                freshErrorResultNotUnderAccount(errorCode, errorMsg, activity, deployAnalyzerModel, listener);
                                            } else {
                                                Intent intent = new Intent();
                                                intent.setClass(activity, DeployResultActivity.class);
                                                DeployResultModel deployResultModel = new DeployResultModel();
                                                deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                                                deployResultModel.sn = deployAnalyzerModel.sn;
                                                deployResultModel.scanType = TYPE_SCAN_DEPLOY_DEVICE;
                                                deployResultModel.errorMsg = errorMsg;
                                                intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                                listener.onError(errorCode, intent, errorMsg);
                                            }
                                        }

                                        @Override
                                        public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                                            DeployResultModel deployResultModel = new DeployResultModel();
                                            deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
                                            deployResultModel.deviceInfo = deviceDeployRsp.getData();
                                            Intent intent = new Intent(activity, DeployResultActivity.class);
                                            //TODO 新版联系人
                                            if (deployAnalyzerModel.deployContactModelList.size() > 0) {
                                                DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
                                                deployResultModel.contact = deployContactModel.name;
                                                deployResultModel.phone = deployContactModel.phone;
                                            }
                                            deployResultModel.scanType = TYPE_SCAN_DEPLOY_DEVICE;
                                            deployResultModel.address = deployAnalyzerModel.address;
                                            intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                            listener.onSuccess(intent);
                                        }
                                    });
                        } else {
                            listener.onError(0, null, activity.getString(R.string.please_enter_contact_phone));
                        }

                        break;
                    case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
                            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
                            RetrofitServiceHelper.INSTANCE.doInspectionChangeDeviceDeploy(deployAnalyzerModel.mDeviceDetail.getSn(), deployAnalyzerModel.sn,
                                    deployAnalyzerModel.mDeviceDetail.getTaskId(), 1, lon, lan, deployAnalyzerModel.tagList, deployAnalyzerModel.nameAndAddress,
                                    deployContactModel.name, deployContactModel.phone, imgUrls,null).
                                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceDeployRsp>(presenter) {
                                @Override
                                public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                                    DeployResultModel deployResultModel = new DeployResultModel();
                                    deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
                                    deployResultModel.deviceInfo = deviceDeployRsp.getData();
                                    Intent intent = new Intent(activity, DeployResultActivity.class);
                                    //TODO 新版联系人
                                    if (deployAnalyzerModel.deployContactModelList.size() > 0) {
                                        DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
                                        deployResultModel.contact = deployContactModel.name;
                                        deployResultModel.phone = deployContactModel.phone;
                                    }
                                    deployResultModel.scanType = TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE;
                                    deployResultModel.address = deployAnalyzerModel.address;
                                    intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                    listener.onSuccess(intent);
                                }

                                @Override
                                public void onErrorMsg(int errorCode, String errorMsg) {
                                    if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                        listener.onError(errorCode, null, errorMsg);
                                    } else if (errorCode == 4013101 || errorCode == 4000013) {
                                        freshErrorResultNotUnderAccount(errorCode, errorMsg, activity, deployAnalyzerModel, listener);
                                    } else {
                                        Intent intent = new Intent();
                                        intent.setClass(activity, DeployResultActivity.class);
                                        DeployResultModel deployResultModel = new DeployResultModel();
                                        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED;
                                        deployResultModel.sn = deployAnalyzerModel.sn;
                                        deployResultModel.scanType = TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE;
                                        deployResultModel.errorMsg = errorMsg;
                                        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
                                        listener.onError(errorCode, intent, errorMsg);
                                    }
                                }
                            });
                        } else {
                            listener.onError(0, null, activity.getString(R.string.please_enter_contact_phone));
                        }
                        break;
                    default:
                        break;
                }
                return;
            }

        }
        listener.onError(0, null, activity.getString(R.string.please_specify_the_deployment_location));
    }

    private void freshErrorResultNotUnderAccount(int errorCode, String errorMsg, Activity activity, DeployAnalyzerModel deployAnalyzerModel, OnDeployAnalyzerListener listener) {
        Intent intent = new Intent();
        intent.setClass(activity, DeployResultActivity.class);
        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT;
        deployResultModel.sn = deployAnalyzerModel.sn;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        listener.onError(errorCode, intent, errorMsg);
    }

    private String parseResultMac(String result) {

        String serialNumber = null;
        if (result != null) {
            String[] data;
            String type;
            data = result.split("\\|");
            type = data[0];
            serialNumber = type;
        }
        return serialNumber;
    }

}
