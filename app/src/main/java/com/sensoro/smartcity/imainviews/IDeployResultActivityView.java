package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

public interface IDeployResultActivityView extends IToast, IActivityIntent {
    void refreshSignal(long updateTime, String signal);

    void setResultImageView(int resId);

    void setTipsTextView(String text);

    void setSnTextView(String sn);

    void setNameTextView(String name);

    void setContactTextView(String content);

    void setWeChatTextView(String content);

    void setStatusTextView(String status);

    void setUpdateTextView(String update);

    void setAddressTextView(String address);

    void setDeployResultErrorInfo(String errorInfo);

    void setUpdateTextViewVisible(boolean isVisible);

    void setContactAndSignalVisible(boolean isVisible);

    void setStateTextView(String msg);

    void setDeployResultContinueText(String text);

    void setDeployResultBackHomeText(String text);

    void setDeployResultContinueVisible(boolean isVisible);

    void setStateTextViewVisible(boolean isVisible);

    void setResultSettingVisible(boolean isVisible);

    void setArrowsLeftVisible(boolean isVisible);

    void setTitleText(String text);

    void setDeployResultHasSetting(String setting);

    void setDeployResultTvStateTextColor(int resColor);
}
