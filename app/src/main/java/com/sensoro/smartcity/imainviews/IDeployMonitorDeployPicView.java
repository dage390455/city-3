package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import java.util.List;

public interface IDeployMonitorDeployPicView extends IToast,IActivityIntent,IProgressDialog {

    void showSelectDialog(SelectDialog.SelectDialogListener listener, List<String> names);

    void displayPic(ImageItem[] selImages, int index);

    void setSaveBtnStatus(boolean isEnable);
    void setDeployPicTvInstallationSiteTipVisible(boolean isVisible);
}
