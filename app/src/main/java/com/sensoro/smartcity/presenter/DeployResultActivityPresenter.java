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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeployResultActivityPresenter extends BasePresenter<IDeployResultActivityView> implements Constants {
    private int resultCode = 0;
    private DeviceInfo deviceInfo = null;
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        resultCode = mContext.getIntent().getIntExtra(EXTRA_SENSOR_RESULT, 0);
        init();
    }

    private void init() {
        try {
            if (resultCode == -1) {
                getView().setResultImageView(R.mipmap.ic_deploy_failed);
                getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_not_exist));
            } else {
                deviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
                String sn = deviceInfo.getSn().toUpperCase();
                String name = deviceInfo.getName();
                String lon = mContext.getIntent().getStringExtra(EXTRA_SENSOR_LON);
                String lan = mContext.getIntent().getStringExtra(EXTRA_SENSOR_LAN);
                String contact = mContext.getIntent().getStringExtra(EXTRA_SETTING_CONTACT);
                String content = mContext.getIntent().getStringExtra(EXTRA_SETTING_CONTENT);
                if (resultCode == 1) {
                    getView().setResultImageView(R.mipmap.ic_deploy_success);
                    getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_success));
                } else {
                    getView().setResultImageView(R.mipmap.ic_deploy_failed);
                    getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_failed));
                }
                getView().setSnTextView(mContext.getString(R.string.sensor_detail_sn) + "：" + sn);
                getView().setNameTextView(mContext.getString(R.string.sensor_detail_name) + "：" + name);
                getView().setLonLanTextView(mContext.getString(R.string.sensor_detail_lon) + "：" + lon, mContext
                        .getString(R.string.sensor_detail_lan) + "：" + lan);
                getView().setContactTextView(mContext.getString(R.string.name) + "：" + (TextUtils.isEmpty(contact) ?
                        "无" : contact));
                getView().setContentTextView(mContext.getString(R.string.phone) + "：" + (TextUtils.isEmpty(contact) ?
                        "无" : content));
                getView().setStatusTextView(mContext.getString(R.string.sensor_detail_status) + "：" + Constants
                        .DEVICE_STATUS_ARRAY[deviceInfo.getStatus()]);
                if (deviceInfo.getLastUpdatedTime() != null) {
                    getView().setUpdateTextViewVisible(true);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
                    Date date = sdf.parse(deviceInfo.getLastUpdatedTime());
                    getView().setUpdateTextView(mContext.getString(R.string.update_time) + "：" + DateUtil
                            .getFullParseDate(date
                                    .getTime()));
                } else {
                    getView().setUpdateTextViewVisible(false);
                }
                getView().refreshSignal(deviceInfo.getUpdatedTime(), deviceInfo.getSignal());
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
        getView().setIntentResult(RESULT_CODE_DEPLOY, intent);
        getView().finishAc();
    }

    public void backHome() {
        Intent intent = new Intent();
        if (resultCode == 1 && deviceInfo != null) {
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        }
        getView().setIntentResult(RESULT_CODE_MAP, intent);
        getView().finishAc();
    }
}
