package com.sensoro.smartcity.imainviews;

import android.widget.ImageView;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;


public interface ICameraPersonDetailActivityView extends IActivityIntent, IToast, IProgressDialog {
    void startPlayLogic(String url1);

    void playError(String errorMsg);

    void setTitle(String time);

    ImageView getImageView();
}
