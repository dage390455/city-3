package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeploySettingNameActivityView extends IToast,IActivityIntent{
    void setEditText(String text);
    void setSearchHistoryLayoutVisible(boolean isVisible);
    void setSearchRelationLayoutVisible(boolean isVisible);
    void updateSearchHistoryData(List<String> searchStr);
    void updateRelationData(List<String> strList);
}
