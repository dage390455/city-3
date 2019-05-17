package com.sensoro.smartcity.adapter.model;

import com.sensoro.common.server.bean.DeviceCameraFacePic;

import java.util.ArrayList;

public class DeviceCameraFacePicListModel {
    public String time;
    public ArrayList<DeviceCameraFacePic> pics = new ArrayList<>();
    public boolean isSelect = false;
}
