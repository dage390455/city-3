package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.model.SignalData;

import java.util.List;

public interface ISignalCheckActivityView extends IToast,IActivityIntent,IProgressDialog {
    void setSnText(String sn);

    void setStatus(String statusText, int textColor);

    void setUpdateTime(String time);

    void setTypeAndName(String text);

    void updateTag(List<String> tags);

    void setStartBtnIcon(int resId);

    void updateProgressDialogMessage(String content);

    boolean getIsStartSignalCheck();

    void setSubTitleVisible(boolean isVisible);

    void updateStatusText(String text);

    void updateContentAdapter(SignalData signalData);

    void setLlTestVisible(boolean isVisible);

    void setLlDetailVisible(boolean isVisible);

    void updateSignalStatusText(String text);

    void setNearVisible(boolean isVisible);
}
