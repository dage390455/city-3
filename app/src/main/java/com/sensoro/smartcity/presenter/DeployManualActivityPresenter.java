package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployManualActivityView;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployManualActivityPresenter extends BasePresenter<IDeployManualActivityView> implements Constants {
    private Activity mContext;
    private boolean is_station;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        is_station = mContext.getIntent().getBooleanExtra(EXTRA_IS_STATION_DEPLOY, false);
    }

    public void clickClose() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONTAINS_DATA, false);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, is_station);
        getView().setIntentResult(RESULT_CODE_DEPLOY, intent);
        getView().finishAc();
    }

    public void clickNext(String text) {
        if (!TextUtils.isEmpty(text) && text.length() == 16) {
//            Intent intent = new Intent(this, DeployActivity.class);
//            intent.putExtra(EXTRA_SENSOR_SN, contentEditText.getText().toString().toUpperCase());
//            startActivity(intent);
            requestData(text);
        } else {
            getView().toastShort("请输入正确的SN,SN为16个字符");
        }
    }

    private void requestData(final String scanSerialNumber) {

        if (TextUtils.isEmpty(scanSerialNumber)) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
        } else {
            if (is_station) {
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getStationDetail(scanSerialNumber.toUpperCase()).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<StationInfoRsp>() {
                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onNext(StationInfoRsp stationInfoRsp) {
                        refreshStation(stationInfoRsp);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(scanSerialNumber, null);
                        } else {
                            freshError(scanSerialNumber, errorMsg);
                        }
                    }
                });
            } else {
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSerialNumber.toUpperCase(), null, 1)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                        refreshDevice(scanSerialNumber, deviceInfoListRsp);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
//                        getView().toastShort(errorMsg);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else {
                            freshError(scanSerialNumber, errorMsg);
                        }
                    }
                });
            }


        }
    }

    /**
     * 错误的基站信息
     */
    private void freshError(String scanSN, String errorInfo) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSN);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, is_station);
        if (!TextUtils.isEmpty(errorInfo)) {
            intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorInfo);
        }
        getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
    }

    /**
     * 刷新基站
     *
     * @param stationInfoRsp
     */
    private void refreshStation(StationInfoRsp stationInfoRsp) {
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
            intent.setClass(mContext, DeployActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
            intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
            getView().startACForResult(intent, REQUEST_CODE_STATION_DEPLOY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 刷新设备
     *
     * @param response
     */
    private void refreshDevice(String sn, DeviceInfoListRsp response) {
        try {
            Intent intent = new Intent();
            if (response.getData().size() > 0) {

                intent.setClass(mContext, DeployActivity.class);
                intent.putExtra(EXTRA_DEVICE_INFO, response.getData().get(0));
                intent.putExtra(EXTRA_IS_STATION_DEPLOY, false);
                intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                getView().startACForResult(intent, REQUEST_CODE_POINT_DEPLOY);
            } else {
                freshError(sn, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        //数据回传
        if (resultCode == RESULT_CODE_MAP) {
            getView().setIntentResult(RESULT_CODE_MAP, data);
        }
        getView().finishAc();
    }
}
