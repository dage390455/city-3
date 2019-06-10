package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.BaseStationDetailModel;

public interface INetWorkInfoActivityView extends IToast, IProgressDialog, IActivityIntent {


    void updateNetWork(BaseStationDetailModel.NetWork netWork);
}





