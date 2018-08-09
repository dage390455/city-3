package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeployActivityView extends IToast, IProgressDialog, IActivityIntent {
    void setTitleTextView(String title);

    void setNameAddressEditText(String text);

    void setUploadButtonClickable(boolean isClickable);

    void setContactEditText(String contact);

    void addDefaultTextView();

    void refreshTagLayout(List<String> tagList);

    void refreshSignal(long updateTime, String signal);

    void setDeployDevicerlSignalVisible(boolean isVisible);

    void setDeployContactRelativeLayoutVisible(boolean isVisible);

    void setDeployPhotoVisible(boolean isVisible);

    void showUploadProgressDialog(int currentNum, int count, double percent);

    void dismissUploadProgressDialog();

    void showStartUploadProgressDialog();

    void setDeployPhotoText(String text);
}
