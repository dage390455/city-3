package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IInspectionActivityView extends IToast,IProgressDialog,IActivityIntent{
     void updateTagsData(List<String> tagList);

    void showNormalDialog();
    void setMonitorTitle(String title);
    void setMonitorSn(String sn);
    void setConfirmState(boolean hasBle);
}
