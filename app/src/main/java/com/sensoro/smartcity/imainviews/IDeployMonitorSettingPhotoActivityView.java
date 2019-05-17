package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.ImageItem;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import java.util.ArrayList;
import java.util.List;

public interface IDeployMonitorSettingPhotoActivityView extends IActivityIntent,IToast {
    void updateImageList(ArrayList<ImageItem> imageList);

    void showDialog(SelectDialog.SelectDialogListener listener, List<String> names);

    void setJustDisplayPic(boolean isJustDisplay);

    void setSubtitleVisible(boolean isVisible);
}
