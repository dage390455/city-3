package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.model.DeployAnalyzerModel;

import java.util.ArrayList;

public interface IOfflineDeployActivityView extends IToast, IProgressDialog, IActivityIntent {

    void updateAdapter(ArrayList<DeployAnalyzerModel> deviceInfos);

    void notifyDataSetChanged();

    void onPullRefreshComplete();

    void setCurrentTaskIndex(int index);


    //上传过程不能点击
    void setUploadClickable(boolean canClick);

//    void setFailureemsg(String msg);

//    void showWarnDialog(boolean canForceUpload, String tipText, String instruction);


}
