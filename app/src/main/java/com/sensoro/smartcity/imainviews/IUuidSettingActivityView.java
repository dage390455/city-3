package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.smartcity.model.UuidSettingModel;

import java.util.List;

public interface IUuidSettingActivityView extends IActivityIntent {
    void updateNormalAdapter(List<UuidSettingModel> data);
    void updateMyAdapter(List<UuidSettingModel> data);
}
