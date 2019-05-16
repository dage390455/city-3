package com.sensoro.smartcity.imainviews;

import android.os.Bundle;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

public interface IContractEditorView extends IToast, IProgressDialog, IActivityIntent {
    void showPersonalFragment();

    void showBusinessFragment();

    void personalFragmentSetArguments(Bundle bundle);

    void businessFragmentSetArguments(Bundle bundle);

    void setTopTabVisible(boolean isVisible);

    void setTitleText(String text);

    void setOriginFormList(boolean isFormList);
}
