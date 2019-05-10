package com.sensoro.smartcity.imainviews;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;

import java.util.List;

public interface ICameraDetailActivityView extends IProgressDialog, IToast, IActivityIntent {

    void updateCameraList(List<DeviceCameraFacePic> data);

    void startPlayLogic(String url1, String title);

    DeviceCameraFacePic getItemData(int position);

    void setDateTime(String time);

    List<DeviceCameraFacePic> getRvListData();

    void onPullRefreshComplete();

    void setLiveState(boolean isLiveStream);

    void setImage(Drawable resource);

    void clearClickPosition();

    boolean isSelectedDateLayoutVisible();

    void setSelectedDateLayoutVisible(boolean isVisible);

    void setSelectedDateSearchText(String s);

    void onPullRefreshCompleteNoMoreData();

    //播放失败
    void playError(int pos);

    void offlineType(String mCameraName);

    void autoRefresh();

    ImageView getImageView();

    void doPlayerResume();

    void doPlayLive(String url, String cameraName, boolean b);

    void setGsyVideoNoVideo();
}
