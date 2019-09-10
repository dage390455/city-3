package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.DeviceCameraPersonFaceBean;

import java.util.List;

public interface ICameraPersonAvatarHistoryActivityView extends IActivityIntent, IProgressDialog, IToast {
    void onPullRefreshComplete();

    void updateData(List<DeviceCameraPersonFaceBean> data);

    List<DeviceCameraPersonFaceBean> getAdapterData();

    void loadTitleAvatar(String faceUrl);
}
