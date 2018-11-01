package com.sensoro.smartcity.model;

import java.io.Serializable;

public class EventData implements Serializable {
    public int code;
    public Object data;

    @Override
    public String toString() {
        return "EventData{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
