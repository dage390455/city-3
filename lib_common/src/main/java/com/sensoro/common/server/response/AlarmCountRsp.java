package com.sensoro.common.server.response;

public class AlarmCountRsp extends ResponseResult {
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;
}
