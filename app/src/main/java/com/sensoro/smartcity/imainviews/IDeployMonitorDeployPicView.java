package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeployPicInfo;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.widgets.SelectDialog;

import java.util.List;

public interface IDeployMonitorDeployPicView extends IToast,IActivityIntent,IProgressDialog {

    void showSelectDialog(SelectDialog.SelectDialogListener listener, List<String> names);

    void displayPic(ImageItem[] selImages, int index);

    void setDeployPicTvInstallationSiteTipVisible(boolean isVisible);

    void updateData(List<DeployPicInfo> data);

    DeployPicInfo getDeployPicItem(int position);

    void showDeployPicExampleDialog(DeployPicInfo item, int position);

    void dismissDeployPicExampleDialog();

    List<DeployPicInfo> getDeployPicData();

    void updateIndexData(ImageItem imageItem, int mAddPicIndex);

    void updateSaveStatus(boolean isEnable);
}
