package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.ContractAddInfo;

public class ContractAddRsp extends ResponseBase{
    public ContractAddInfo getData() {
        return data;
    }

    public void setData(ContractAddInfo data) {
        this.data = data;
    }

    protected ContractAddInfo data;
}
