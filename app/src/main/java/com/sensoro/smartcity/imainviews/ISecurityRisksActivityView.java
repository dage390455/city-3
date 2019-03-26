package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface ISecurityRisksActivityView extends IToast, IActivityIntent {
    void updateSecurityRisksContent(List<SecurityRisksAdapterModel> data);

    void setConstraintTagVisible(boolean isVisible);
}
