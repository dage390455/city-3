package com.sensoro.common.model;

import java.io.Serializable;

public class IbeaconSettingData implements Serializable {
    public boolean switchIn;
    public String switchInMessage;
    public boolean switchOut;
    public String switchOutMessage;
    public String currentUUID;
    public Integer currentMajor;
    public Integer currentMirror;
}
