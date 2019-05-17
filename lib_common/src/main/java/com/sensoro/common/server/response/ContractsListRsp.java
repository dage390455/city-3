package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.ContractListInfo;

import java.io.Serializable;
import java.util.List;

public class ContractsListRsp extends ResponseBase implements Serializable {
    public List<ContractListInfo> getData() {
        return data;
    }

    public void setData(List<ContractListInfo> data) {
        this.data = data;
    }

    private List<ContractListInfo> data;
}
