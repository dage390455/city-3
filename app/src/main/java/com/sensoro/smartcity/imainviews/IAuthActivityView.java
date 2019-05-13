package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IAuthActivityView extends IActivityIntent, IToast, IProgressDialog {
    void updateImvStatus(boolean isSuccess);

    void autoFillCode(List<String> codes);
}
