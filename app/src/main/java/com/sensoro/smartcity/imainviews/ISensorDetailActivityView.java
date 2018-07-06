package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;

import java.util.List;

public interface ISensorDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setStatusImageView(int resId);

    void setSnTextView(String sn, int color);

    void setDateTextView(String date, int color);

    void setNameTextView(String name, int color);

    void setBatteryLayoutVisible(boolean isVisible);

    void setPowerLayoutVisible(boolean isVisible);

    void setNotDeployLayoutVisible(boolean isVisible);

    void setMapLayoutVisible(boolean isVisible);

    void setBatteryMarkerViewVisible(boolean isVisible);

    void setRightStructLayoutVisible(boolean isVisible);

    void initValueColor(int color);

    void setRecentKLayoutVisible(boolean isVisible);

    void refreshStructLayout(List<SensorStruct> sensorStructList);

    void setMapViewVisible(boolean isVisible);

    void updateBatteryData(List<DeviceRecentInfo> batteryDataList);

    void setRecentDaysTitleTextView(String recentDaysTitle);

    void setRecentDaysInfo1TextView(String text);

    void setRecentDaysInfo2TextView(boolean isVisible, String text);

    void setTypeImageView(String type);

    void setChartLayoutVisible(boolean isVisible);

}
