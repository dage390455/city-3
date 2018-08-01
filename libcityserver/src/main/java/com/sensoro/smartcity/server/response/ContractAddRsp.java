package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.ContractAddInfo;

public class ContractAddRsp extends ResponseBase{
    public ContractAddInfo getData() {
        return data;
    }

    public void setData(ContractAddInfo data) {
        this.data = data;
    }

    protected ContractAddInfo data;
}
