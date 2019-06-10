package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.DeployCameraUploadInfo;

public class DeployCameraUploadRsp extends ResponseBase {
    public DeployCameraUploadInfo getData() {
        return data;
    }

    public void setData(DeployCameraUploadInfo data) {
        this.data = data;
    }

    protected DeployCameraUploadInfo data;
}
