package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.List;

public interface IAuthActivityView extends IActivityIntent, IToast, IProgressDialog {
    void updateImvStatus(boolean isSuccess);

    void autoFillCode(List<String> codes);
}
