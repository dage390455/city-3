package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IScanActivityView extends IToast,IProgressDialog,IActivityIntent{
    void startScan();

    void stopScan();

    void updateTitleText(String title);

    void setBottomVisible(boolean isVisible);

    void updateQrTipText(String tip);
}
