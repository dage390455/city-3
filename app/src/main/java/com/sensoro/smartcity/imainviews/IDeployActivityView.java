package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeployActivityView extends IToast,IProgressDialog,IActivityIntent{
    void setTitleTextView(String title);
    void setNameAddressEditText(String text);
    void setUploadButtonClickable(boolean isClickable);
    void setContactEditText(String contact);
    void addDefaultTextView();
    void refreshTagLayout(List<String> tagList);
    void refreshSignal(long updateTime, String signal);
}
