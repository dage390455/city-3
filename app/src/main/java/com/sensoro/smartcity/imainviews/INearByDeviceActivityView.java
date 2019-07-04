package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeviceInfo;

import java.util.ArrayList;

public interface INearByDeviceActivityView extends IToast, IProgressDialog, IActivityIntent {

    void updateAdapter(ArrayList<DeviceInfo> deviceInfos);
    void onPullRefreshComplete();

}
