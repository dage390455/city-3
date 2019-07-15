package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IDeployRecordConfigCommonElectActivityView extends IToast, IActivityIntent, IProgressDialog {
    void setConfigAirRatedCurrentValue(String value);

    void setConfigMaterial(String material);

    void setConfigDiameter(String diameter);

    void setConfigActualOverCurrentThreshold(String value);

}
