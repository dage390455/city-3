package com.sensoro.smartcity.model;

import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeployAnalyzerModel implements Serializable {
    public String sn;
    public int deployType;
    public String address;
    public String signal;
    public long updatedTime;
    public String nameAndAddress;
    public String weChatAccount;
    public boolean notOwn;
    public int status;
    public String blePassword;
    public String deviceType;
    public int mapSourceType = 1;
    public DeployControlSettingData settingData;
    //
    public final List<Double> latLng = new ArrayList<>();
    //
    public final List<String> tagList = new ArrayList<>();
    //
    public final List<DeployContactModel> deployContactModelList = new ArrayList<>();
    //
    public final ArrayList<ImageItem> images = new ArrayList<>();
    //旧设备
    public InspectionTaskDeviceDetail mDeviceDetail;
    //
    public final List<Integer> channelMask = new ArrayList<>();
}
