package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.response.DeviceCameraPersonFaceRsp;

import java.util.List;

public interface ICameraPersonAvatarHistoryActivityView extends IActivityIntent, IProgressDialog , IToast {
    void onPullRefreshComplete();

    void onPullRefreshCompleteNoMoreData();

    void updateData(List<DeviceCameraPersonFaceRsp.DataBean> data);

    List<DeviceCameraPersonFaceRsp.DataBean> getAdapterData();

    void loadTitleAvatar(String faceUrl);
}
