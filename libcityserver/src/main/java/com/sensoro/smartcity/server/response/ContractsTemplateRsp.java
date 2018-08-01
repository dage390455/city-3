package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.io.Serializable;
import java.util.List;

public class ContractsTemplateRsp extends ResponseBase implements Serializable {
    public List<ContractsTemplateInfo> getData() {
        return data;
    }

    public void setData(List<ContractsTemplateInfo> data) {
        this.data = data;
    }

    private List<ContractsTemplateInfo> data;
}
