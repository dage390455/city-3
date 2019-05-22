package com.sensoro.smartcity.imainviews;

import com.github.mikephil.charting.data.LineData;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IBaseStationDetailActivityView extends IToast, IProgressDialog, IActivityIntent {


    void updateChartData(LineData lineData);

    void updateTopView(String time, String first, String second);


}
