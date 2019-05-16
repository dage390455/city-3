package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.ContractListInfo;

public class ContractInfoRsp extends ResponseBase {
    public ContractListInfo getData() {
        return data;
    }

    public void setData(ContractListInfo data) {
        this.data = data;
    }

    private ContractListInfo data;
}
