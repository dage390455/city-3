package com.sensoro.smartcity.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.DeployMonitorDetailActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public enum DeployAnalyzerUtils implements Constants {
    INSTANCE;

    public void getDeployAnalyzerResult(final String scanSerialNumber, final Activity activity, final OnDeployAnalyzerListener listener) {
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSerialNumber.toUpperCase(), null, 1).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
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
                    intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                    intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSerialNumber);
                    intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE);
                    intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorMsg);
                }
            }

            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                try {
                    if (deviceInfoListRsp.getData().size() > 0) {
                        Intent intent = new Intent();
                        intent.setClass(activity, DeployMonitorDetailActivity.class);
                        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfoListRsp.getData().get(0));
                        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE);
                        intent.putExtra("uid", activity.getIntent().getStringExtra("uid"));
                        listener.onSuccess(intent);
                    } else {
                        doStation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //获取不到设备信息后拿station信息
            private void doStation() {
                RetrofitServiceHelper.INSTANCE.getStationDetail(scanSerialNumber.toUpperCase()).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<StationInfoRsp>() {
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            listener.onError(errorCode, null, errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                            intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSerialNumber);
                            intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE);
                            listener.onError(errorCode, intent, errorMsg);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployResultActivity.class);
                            intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                            intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSerialNumber);
                            intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_DEVICE);
                            intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorMsg);
                            listener.onError(errorCode, intent, errorMsg);
                        }
                    }

                    @Override
                    public void onCompleted(StationInfoRsp stationInfoRsp) {
                        try {
                            StationInfo stationInfo = stationInfoRsp.getData();
                            double[] lonlat = stationInfo.getLonlat();
//        double[] lonlatLabel = stationInfo.getLonlatLabel();
                            String name = stationInfo.getName();
                            String sn = stationInfo.getSn();
                            String[] tags = stationInfo.getTags();
                            long updatedTime = stationInfo.getUpdatedTime();
                            DeviceInfo deviceInfo = new DeviceInfo();
                            deviceInfo.setSn(sn);
                            deviceInfo.setTags(tags);
                            deviceInfo.setLonlat(lonlat);
                            deviceInfo.setUpdatedTime(updatedTime);
                            if (!TextUtils.isEmpty(name)) {
                                deviceInfo.setName(name);
                            }
                            Intent intent = new Intent();
                            intent.setClass(activity, DeployMonitorDetailActivity.class);
                            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
                            intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_DEPLOY_STATION);
                            intent.putExtra("uid", activity.getIntent().getStringExtra("uid"));
                            listener.onSuccess(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public interface OnDeployAnalyzerListener {
        void onSuccess(Intent intent);

        void onError(int errType, Intent intent, String errMsg);
    }
}
