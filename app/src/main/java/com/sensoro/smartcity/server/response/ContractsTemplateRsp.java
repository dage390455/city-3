package com.sensoro.smartcity.server.response;

import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class ContractsTemplateRsp extends ResponseBase implements Serializable {
    public ArrayList<ContractsTemplateInfo> getData() {
        return data;
    }

    public void setData(ArrayList<ContractsTemplateInfo> data) {
        this.data = data;
    }

    private ArrayList<ContractsTemplateInfo> data;
}
