package com.sensoro.common.server.response;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {
    public static final int CODE_SUCCESS = 0;
    private int errcode;
    private String errmsg;
    private T data;
    private int code;
    private String message;

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
                "code=" + code +
                "message=" + message +
                '}';
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
