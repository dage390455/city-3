package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.UserInfo;

/**
 * Created by sensoro on 17/7/25.
 */

public class LoginRsp extends ResponseBase {
    private UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
