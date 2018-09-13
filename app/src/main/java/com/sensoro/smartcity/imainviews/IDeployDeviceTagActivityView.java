package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IDeployDeviceTagActivityView extends IToast, IActivityIntent {
    void updateTags(List<String> tags);

    void updateSearchHistory(List<String> strHistory);
}
