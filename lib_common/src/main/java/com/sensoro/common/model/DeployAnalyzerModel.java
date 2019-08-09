package com.sensoro.common.model;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeployPicInfo;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;

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
    public String cameraStatus;
    public String blePassword;
    public String deviceType;
    public int mapSourceType = 1;
    public String hls;
    public String flv;
    public String installationMode;
    public String orientation;
    public DeployControlSettingData settingData;

    //
    public final List<Double> latLng = new ArrayList<>();
    //
    public final List<String> tagList = new ArrayList<>();
    //
    public final List<DeployContactModel> deployContactModelList = new ArrayList<>();
    //
    public final ArrayList<DeployPicInfo> images = new ArrayList<>();
    //旧设备
    public InspectionTaskDeviceDetail mDeviceDetail;
    //
    public final List<Integer> channelMask = new ArrayList<>();
    //强制部署原因
    public String forceReason;
    public Integer currentStatus;
    public String currentSignalQuality;
    //白名单类型（默认没有白名单限制）
    public int whiteListDeployType = Constants.TYPE_SCAN_DEPLOY_DEVICE;
    //铭牌部署 标识该铭牌是否被部署过，被部署过 跳转铭牌详情页
    public Boolean deployNameplateFlag;
}
