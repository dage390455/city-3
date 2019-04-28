package com.sensoro.smartcity.imainviews;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.ArrayList;
import java.util.List;

public interface ICameraDetailActivityView extends IProgressDialog, IToast , IActivityIntent {
    void initVideoOption(String url);

    void updateCameraList(ArrayList<DeviceCameraFacePicListModel> data);

    void startPlayLogic(String url1);

    DeviceCameraFacePicListModel getItemData(int position);

    void setDateTime(String time);

    List<DeviceCameraFacePicListModel> getRvListData();

    void onPullRefreshComplete();

    void setLiveState(boolean isLiveStream);

    void setImage(Drawable resource);

    void clearAdapterPreModel();

    boolean isSelectedDateLayoutVisible();

    void setSelectedDateLayoutVisible(boolean isVisible);

    void setSelectedDateSearchText(String s);

    void onPullRefreshCompleteNoMoreData();
    //播放失败
    void playError(int pos);
}
