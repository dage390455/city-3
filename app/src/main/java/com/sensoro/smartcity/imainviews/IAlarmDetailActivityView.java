package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.server.bean.AlarmInfo;

import java.util.List;

public interface IAlarmDetailActivityView extends IActivityIntent{
    void setNameTextView(String name);
    void setDateTextView(String date);
    void setStatusInfo(String text,int colorId,int resId);
    void setDisplayStatus(int displayStatus);
    void updateTimerShaftAdapter(List<AlarmInfo.RecordInfo> recordInfoList);
    void setSensoroIv(String sensoroType);
}
