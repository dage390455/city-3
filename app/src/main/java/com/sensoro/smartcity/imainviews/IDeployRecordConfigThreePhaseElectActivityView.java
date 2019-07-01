package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IDeployRecordConfigThreePhaseElectActivityView extends IToast, IActivityIntent, IProgressDialog {
    void setConfigAirRatedCurrentValue(String value);

    void setConfigInput(String input);

    void setConfigOutput(String output);

    void setConfigActualOverCurrentThreshold(String value);

    void setConfigRecommendTrans(String value);

    void setConfigActualTrans(String value);

}
