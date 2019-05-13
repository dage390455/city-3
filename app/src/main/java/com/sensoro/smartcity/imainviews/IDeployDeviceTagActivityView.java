package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IDeployDeviceTagActivityView extends IToast, IActivityIntent {
    void updateTags(List<String> tags);

    void updateSearchHistory(List<String> strHistory);

    void showDialogWithEdit(String text,int position);

    void dismissDialog();

    void updateSaveStatus(boolean isEnable);

    void showHistoryClearDialog();
}
