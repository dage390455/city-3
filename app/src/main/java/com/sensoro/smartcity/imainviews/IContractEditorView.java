package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IContractEditorView extends IToast,IProgressDialog,IActivityIntent {
    void showPersonalFragment();

    void showBusinessFragment();
}
