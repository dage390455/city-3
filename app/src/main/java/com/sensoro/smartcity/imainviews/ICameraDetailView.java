package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.iwidget.IProgressDialog;

import java.util.ArrayList;
import java.util.List;

public interface ICameraDetailView extends IProgressDialog {
    void initVideoOption(String url);

    void updateCameraList(ArrayList<DeviceCameraFacePicListModel> data);

    void startPlayLogic(String url1);

    DeviceCameraFacePicListModel getItemData(int position);

    void setDateTime(String time);

    List<DeviceCameraFacePicListModel> getRvListData();

    void onPullRefreshComplete();
    //播放失败
    void playError(int pos);
}
