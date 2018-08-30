package com.sensoro.smartcity.server.response;

/**
 * Created by tangrisheng on 2016/5/5.
 * Response Base Class
 */

public class ResponseBase {
    public static final int CODE_SUCCESS = 0;
    int errcode;
    String errmsg;
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

    @Override
    public String toString() {
        return "ResponseBase{" +
                "errcode=" + errcode +
                "errmsg=" + errmsg +
                '}';
    }
}
