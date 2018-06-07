package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmDetailActivityView;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class AlarmDetailActivityPresenter extends BasePresenter<IAlarmDetailActivityView> implements Constants {
    private final List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private DeviceAlarmLogInfo deviceAlarmLogInfo;


    public DeviceAlarmLogInfo getDeviceAlarmLogInfo() {
        return deviceAlarmLogInfo;
    }

    public void setDeviceAlarmLogInfo(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        this.deviceAlarmLogInfo = deviceAlarmLogInfo;
    }


    public List<AlarmInfo.RecordInfo> getList() {
        return mList;
    }

    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deviceAlarmLogInfo = (DeviceAlarmLogInfo) mContext.getIntent().getSerializableExtra(EXTRA_ALARM_INFO);
        refreshData();
    }

    public void doBack() {
        Intent data = new Intent();
        data.putExtra(EXTRA_ALARM_INFO, deviceAlarmLogInfo);
        getView().setIntentResult(RESULT_CODE_ALARM_DETAIL, data);
        getView().finishAc();
    }

    public void refreshData() {
        //
        String deviceName = deviceAlarmLogInfo.getDeviceName();
        getView().setNameTextView(TextUtils.isEmpty(deviceName) ? deviceAlarmLogInfo.getDeviceSN() : deviceName);
        getView().setDateTextView(DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime()));

        getView().setDisplayStatus(deviceAlarmLogInfo.getDisplayStatus());
        getView().setSensoroIv(deviceAlarmLogInfo.getSensorType());
        AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
        if (recordInfoArray != null) {
            mList.clear();
            for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                mList.add(recordInfoArray[i]);
            }
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if (recordInfo.getType().equals("recovery")) {
                    getView().setStatusInfo("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常", R
                            .color.sensoro_normal, R.drawable.shape_status_normal);
                    break;
                } else {
                    getView().setStatusInfo(mContext.getResources().getString(R.string.alarming), R.color.sensoro_alarm,
                            R.drawable.shape_status_alarm);
                }
            }
        }
        getView().updateTimerShaftAdapter(mList);
    }
}
