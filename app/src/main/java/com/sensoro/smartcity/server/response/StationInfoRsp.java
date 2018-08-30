package com.sensoro.smartcity.server.response;

import java.io.Serializable;

/**
 * Created by sensoro on 17/7/26.
 */

public class StationInfoRsp extends ResponseBase implements Serializable {
    public StationInfo getData() {
        return data;
    }

    public void setData(StationInfo data) {
        this.data = data;
    }

    private StationInfo data;

    @Override
    public String toString() {
        return "StationInfoRsp{" +
                "data=" + data.toString() +
                ", errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
