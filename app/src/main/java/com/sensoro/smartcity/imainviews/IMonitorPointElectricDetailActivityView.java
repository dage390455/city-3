package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ScenesData;

import java.util.List;

public interface IMonitorPointElectricDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setTitleNameTextView(String name);

    void setUpdateTime(String time);

    void setAlarmStateColor(int color);

    void setContractName(String contractName);

    void setContractPhone(String contractPhone);

    void setDeviceLocation(String location, boolean isArrowsRight);

    void updateDeviceInfoAdapter(List<MonitoringPointRcContentAdapterModel> data);
    void updateDeviceMalfunctionInfoAdapter(List<MonitoringPointRcContentAdapterModel> data);

    void setSNText(String sn);

    void updateTags(List<String> list);

    void updateMonitorPhotos(List<ScenesData> data);

    void setBatteryInfo(String battery);

    void setInterval(String interval);

    void setStatusInfo(String statusInfo, int textColor);

    void setContactPhoneIconVisible(boolean isVisible);

    void setNoContact();

    void setDeviceLocationTextColor(int color);

    void setDeviceTypeName(String typeName);

    void setDeviceOperationVisible(boolean isVisible);

    void setErasureStatus(boolean isClickable);

    void setResetStatus(boolean isClickable);

    void setSelfCheckStatus(boolean isClickable);

    void setAirSwitchConfigStatus(boolean isClickable);

    void setQueryStatus(boolean isClickable);

    void setPsdStatus(boolean isClickable);

    void showOperationSuccessToast();

    void showErrorTipDialog(String errorMsg);

    void showOperationTipLoadingDialog();

    void dismissTipDialog();

    void dismissOperatingLoadingDialog();

    void setBlePwd(String pwd);

    void setElectDetailVisible(boolean isVisible);

    void setIvAlarmStatusVisible(boolean isVisible);

    void setElect3DetailVisible(boolean isVisible);

    void set3ElectADetail(int index, String text);

    void set3ElectVDetail(int index, String text);

    void set3ElectTDetail(int index, String text);
}
