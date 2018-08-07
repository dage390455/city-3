package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployResultActivityView;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.DateUtil;

public class DeployResultActivityPresenter extends BasePresenter<IDeployResultActivityView> implements Constants {
    private int resultCode = 0;
    private DeviceInfo deviceInfo = null;
    private Activity mContext;
    private boolean is_station;
    private String errorInfo;
    private String sn;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        resultCode = mContext.getIntent().getIntExtra(EXTRA_SENSOR_RESULT, 0);
        is_station = mContext.getIntent().getBooleanExtra(EXTRA_IS_STATION_DEPLOY, false);
        errorInfo = mContext.getIntent().getStringExtra(EXTRA_SENSOR_RESULT_ERROR);
        sn = mContext.getIntent().getStringExtra(EXTRA_SENSOR_SN_RESULT);
        init();
    }

    private void init() {
        try {
            if (resultCode == -1) {
                getView().setResultImageView(R.mipmap.ic_deploy_failed);
                //
                if (!TextUtils.isEmpty(sn)) {
                    getView().setSnTextView(mContext.getString(R.string.sensor_detail_sn) + "：" + sn);
                }
                if (is_station) {
                    if (!TextUtils.isEmpty(errorInfo)) {
                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_station_failed));
                        getView().setDeployResultErrorInfo("错误：" + errorInfo);
                    } else {
                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_station_not_exist));
                    }
                } else {
                    if (!TextUtils.isEmpty(errorInfo)) {
                        getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_failed));
                        getView().setDeployResultErrorInfo("错误：" + errorInfo);
                    } else {
                        getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_not_exist));
                    }
                }


            } else {
                deviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
                String sn = deviceInfo.getSn().toUpperCase();
                String name = deviceInfo.getName();
                String address = deviceInfo.getAddress();
                if (!TextUtils.isEmpty(address)) {
                    getView().setAddressTextView("位置：" + address);
                }
                long updatedTime = deviceInfo.getUpdatedTime();
                if (!is_station) {
                    getView().setContactAndSignalVisible(true);
                    String contact = mContext.getIntent().getStringExtra(EXTRA_SETTING_CONTACT);
                    String content = mContext.getIntent().getStringExtra(EXTRA_SETTING_CONTENT);

                    getView().setContentTextView("联系人：" + (TextUtils.isEmpty(contact) ?
                            "无" : contact) + "(" + (TextUtils.isEmpty
                            (contact) ?
                            "无" : content) + ")");
                    getView().refreshSignal(updatedTime, deviceInfo.getSignal());
                } else {
                    getView().setContactAndSignalVisible(false);
                }

                if (resultCode == 1) {
                    getView().setResultImageView(R.mipmap.ic_deploy_success);
                    if (is_station) {
                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_station_success));
                    } else {
                        getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_success));
                    }
                } else {
                    getView().setResultImageView(R.mipmap.ic_deploy_failed);
                    if (is_station) {
                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_station_failed));
                    } else {
                        getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_failed));
                    }
                }
                getView().setSnTextView(mContext.getString(R.string.sensor_detail_sn) + "：" + sn);
                getView().setNameTextView(mContext.getString(R.string.sensor_detail_name) + "：" + name);
//                getView().setLonLanTextView(mContext.getString(R.string.sensor_detail_lon) + "：" + lon, mContext
//                        .getString(R.string.sensor_detail_lan) + "：" + lan);
                if (is_station) {
                    getView().setStatusTextView(mContext.getString(R.string.sensor_detail_status) + "：" + Constants
                            .STATION_STATUS_ARRAY[deviceInfo.getStatus() + 1]);
                } else {
                    getView().setStatusTextView(mContext.getString(R.string.sensor_detail_status) + "：" + Constants
                            .DEVICE_STATUS_ARRAY[deviceInfo.getStatus()]);
                }
//                // 修改长传时间
//                String lastUpdatedTime = deviceInfo.getLastUpdatedTime();
                if (updatedTime == -1) {
                    getView().setUpdateTextViewVisible(false);
                } else {
                    getView().setUpdateTextView(mContext.getString(R.string.update_time) + "：" + DateUtil
                            .getFullParseDate(updatedTime));
                }
//                if (lastUpdatedTime != null) {
//                    getView().setUpdateTextViewVisible(true);
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
//                    Date date = sdf.parse(lastUpdatedTime);
//                    getView().setUpdateTextView(mContext.getString(R.string.update_time) + "：" + DateUtil
//                            .getFullParseDate(date.getTime()));
//                } else {
//                    getView().setUpdateTextViewVisible(false);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mContext.getResources().getString(R.string.tips_data_error));
        }

    }

    public void gotoContinue() {
        Intent intent = new Intent();
        if (resultCode == 1 && deviceInfo != null) {
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_CONTAINS_DATA, true);
        } else {
            intent.putExtra(EXTRA_CONTAINS_DATA, false);
        }
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, is_station);
        getView().setIntentResult(RESULT_CODE_DEPLOY, intent);
        getView().finishAc();
    }

    public void backHome() {
        Intent intent = new Intent();
        if (resultCode == 1 && deviceInfo != null) {
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        }
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, is_station);
        getView().setIntentResult(RESULT_CODE_MAP, intent);
        getView().finishAc();

    }

    @Override
    public void onDestroy() {

    }
}
