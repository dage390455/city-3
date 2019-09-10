package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IInspectionActivityView extends IToast,IProgressDialog,IActivityIntent{
     void updateTagsData(List<String> tagList);

    void showNormalDialog();
    void setMonitorTitle(String title);
    void setMonitorSn(String sn);
    void setConfirmState(boolean hasBle);
}
