package com.sensoro.smartcity.server.response;

public class AuthRsp extends ResponseBase {
    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    private boolean data;
}
