package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeploySettingTagActivityView extends IActivityIntent,IToast{
    void setSearchHistoryLayoutVisible(boolean isVisible);

    void updateTags(List<String> tags);
}
