package com.sensoro.smartcity.imainviews;

import android.widget.ImageView;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface ICameraPersonDetailActivityView extends IActivityIntent, IToast , IProgressDialog {
    void startPlayLogic(String url1);

    void playError(String errorMsg);

    void setTitle(String time);

    ImageView getImageView();
}
