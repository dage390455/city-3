package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.response.DeviceCameraPersonFaceRsp;

import java.util.List;

public interface ICameraPersonAvatarHistoryActivityView extends IActivityIntent, IProgressDialog, IToast {
    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void updateData(List<DeviceCameraPersonFaceRsp.DataBean> data);

    List<DeviceCameraPersonFaceRsp.DataBean> getAdapterData();

    void loadTitleAvatar(String faceUrl);
}
