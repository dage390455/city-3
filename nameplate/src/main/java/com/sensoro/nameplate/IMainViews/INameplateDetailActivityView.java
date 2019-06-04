package com.sensoro.nameplate.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.NamePlateInfo;

import java.util.List;

public interface INameplateDetailActivityView extends IToast, IProgressDialog, IActivityIntent {
    void updateBindDeviceAdapter(List<NamePlateInfo> data);

    void onPullRefreshComplete();

    void updateTopDetail(NamePlateInfo namePlateInfo);

    void updateNamePlateStatus(int pos);

}
