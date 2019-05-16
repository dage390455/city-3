package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.UserInfo;

/**
 * Created by sensoro on 17/7/26.
 */

public class UserAccountControlRsp extends ResponseBase {

    private UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserAccountControlRsp{" +
                "data=" + data +
                '}';
    }
}
