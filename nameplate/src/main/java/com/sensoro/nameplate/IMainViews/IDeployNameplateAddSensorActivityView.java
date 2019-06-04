package com.sensoro.nameplate.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.NamePlateInfo;

import java.util.List;

public interface IDeployNameplateAddSensorActivityView extends IToast, IActivityIntent, IProgressDialog {
    void onPullRefreshComplete();

    void updateBindData(List<NamePlateInfo> mBindList);

    void setBindDeviceSize(int size);
}
