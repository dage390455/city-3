package com.sensoro.smartcity.imainviews;

import com.github.mikephil.charting.data.LineData;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.common.server.bean.ScenesData;

import java.util.List;

public interface IBaseStationDetailActivityView extends IToast, IProgressDialog, IActivityIntent {


    void updateChartData(LineData lineData, float max, float min);

    void updateCharEmpty();

    void updateDetailData(BaseStationDetailModel model);

    void updateNetDelay(String delay, int color);

    void updateTopView(String time, String first, String second);

    void setDeviceLocationTextColor(int color);

    void setDeviceLocation(String location, boolean isArrowsRight);

    void updateMonitorPhotos(List<ScenesData> data);

}
