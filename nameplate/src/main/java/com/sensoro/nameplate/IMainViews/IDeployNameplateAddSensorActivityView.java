package com.sensoro.nameplate.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

public interface IDeployNameplateAddSensorActivityView extends IToast, IActivityIntent{
    void onPullRefreshComplete();
}
