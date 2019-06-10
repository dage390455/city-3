package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeployStationInfo;

import java.io.Serializable;

/**
 * Created by sensoro on 17/7/26.
 */

public class DeployStationInfoRsp extends ResponseBase implements Serializable {
    public DeployStationInfo getData() {
        return data;
    }

    public void setData(DeployStationInfo data) {
        this.data = data;
    }

    private DeployStationInfo data;

    @Override
    public String toString() {
        return "DeployStationInfoRsp{" +
                "data=" + data.toString() +
                ", errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
