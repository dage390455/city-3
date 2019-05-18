package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IBaseStationDetailActivityView extends IToast, IProgressDialog, IActivityIntent {


    public void updateChartData();


}
