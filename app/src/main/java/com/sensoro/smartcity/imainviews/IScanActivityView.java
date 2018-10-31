package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IScanActivityView extends IToast,IProgressDialog,IActivityIntent{
    void startScan();

    void stopScan();

    void updateTitleText(String title);

    void setBottomVisible(boolean isVisible);

    void updateQrTipText(String tip);
}
