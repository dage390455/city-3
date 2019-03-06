package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeployMonitorNameAddressActivityView extends IToast, IActivityIntent,IProgressDialog {
    void setEditText(String text);

    void updateSearchHistoryData(List<String> searchStr);

    void updateTvTitle(String sn);

    void updateSaveStatus(boolean isEnable);
}
