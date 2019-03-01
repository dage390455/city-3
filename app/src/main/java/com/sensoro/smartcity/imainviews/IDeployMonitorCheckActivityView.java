package com.sensoro.smartcity.imainviews;

import android.os.Bundle;

public interface IDeployMonitorCheckActivityView {
    void showDeployMonitorLocalCheckFragment();

    void showDeployMonitorUploadCheckFragment();

    void deployMonitorLocalCheckFragmentSetArguments(Bundle bundle);

    void deployMonitorUploadCheckFragmentSetArguments(Bundle bundle);

    void setDeployMonitorStep(int step);
}
