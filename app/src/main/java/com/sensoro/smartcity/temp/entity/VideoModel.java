package com.sensoro.smartcity.temp.entity;


import com.sensoro.common.server.bean.DeviceCameraInfo;

import java.util.ArrayList;

/**
 * Created by shuyu on 2016/11/11.
 */

public class VideoModel {
    public DeviceCameraInfo deviceCameraInfo;
    public int state = -10;


    public ArrayList<String> cityURl;

    public VideoModel() {
    }
}
