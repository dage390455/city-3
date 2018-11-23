package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.ContractListInfo;

import java.util.List;

public class ContractInfoRsp extends ResponseBase {
    public ContractListInfo getData() {
        return data;
    }

    public void setData(ContractListInfo data) {
        this.data = data;
    }

    private ContractListInfo data;
}
