package com.sensoro.smartcity.imainviews;

import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import java.util.ArrayList;
import java.util.List;

public interface IDeployMonitorSettingPhotoActivityView extends IActivityIntent,IToast {
    void updateImageList(ArrayList<ImageItem> imageList);

    void showDialog(SelectDialog.SelectDialogListener listener, List<String> names);

    void setJustDisplayPic(boolean isJustDisplay);

    void setSubtitleVisible(boolean isVisible);
}
