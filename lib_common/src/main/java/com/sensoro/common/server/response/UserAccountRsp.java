package com.sensoro.common.server.response;

import com.sensoro.common.server.bean.UserInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sensoro on 17/7/26.
 */

public class UserAccountRsp extends ResponseBase implements Serializable {
    private int total;
    protected List<UserInfo> data;

    public List<UserInfo> getData() {
        return data;
    }

    public void setData(List<UserInfo> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "UserAccountRsp{" +
                "data=" + data +
                '}';
    }
}
