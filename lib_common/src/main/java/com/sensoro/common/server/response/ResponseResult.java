package com.sensoro.common.server.response;

/**
 * Created by tangrisheng on 2016/5/5.
 * ResponseResult Base Class
 */

public class ResponseResult<T> {
    public static final int CODE_SUCCESS = 0;
    int errcode;
    String errmsg;
    private T data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseBase{" +
                "errcode=" + errcode +
                "errmsg=" + errmsg +
                '}';
    }


}
