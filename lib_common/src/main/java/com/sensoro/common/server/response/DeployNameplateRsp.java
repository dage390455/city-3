package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeployNameplateInfo;

import java.util.List;

public class DeployNameplateRsp extends ResponseBase{


    /**
     * data : {"name":1,"deployFlag":true,"tags":["1","2"],"deployPics":["www.baidu.com","www.baidu.com"],"lonlat":[15,30],"createdTime":1558946772294,"updatedTime":1558946772294}
     */

    private DeployNameplateInfo data;

    public DeployNameplateInfo getData() {
        return data;
    }

    public void setData(DeployNameplateInfo data) {
        this.data = data;
    }


}
