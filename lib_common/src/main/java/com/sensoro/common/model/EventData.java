package com.sensoro.common.model;

import java.io.Serializable;

public class EventData implements Serializable {
    public int code;
    public Object data;

    public EventData() {
    }

    public EventData(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
