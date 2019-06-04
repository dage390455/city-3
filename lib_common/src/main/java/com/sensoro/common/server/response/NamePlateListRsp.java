package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.NamePlateInfo;

import java.io.Serializable;
import java.util.List;

public class NamePlateListRsp extends ResponseBase implements Serializable {
    public List<NamePlateInfo> getData() {
        return data;
    }

    public void setData(List<NamePlateInfo> data) {
        this.data = data;
    }

    private List<NamePlateInfo> data;
}
