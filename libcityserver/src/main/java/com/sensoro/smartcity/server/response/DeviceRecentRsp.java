package com.sensoro.smartcity.server.response;

import java.util.Arrays;

/**
 * Created by sensoro on 17/11/21.
 */

public class DeviceRecentRsp extends ResponseBase {

    public Object data;
    public String sensorTypes[];

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String[] getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(String[] sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    @Override
    public String toString() {
        return "DeviceRecentRsp{" +
                "data='" + data + '\'' +
                ", sensorTypes=" + Arrays.toString(sensorTypes) +
                '}';
    }
}
