package com.sensoro.smartcity.temp.entity;

import java.io.Serializable;

public class LmBaseResponseEntity<T> implements Serializable {

    public int code;
    public T data;
    public String message;

    public boolean isSuccess() {
        return code == 200 || code == 200000 || code == 0;
    }

}
