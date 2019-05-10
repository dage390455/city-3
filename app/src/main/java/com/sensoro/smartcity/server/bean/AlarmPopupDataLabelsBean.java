package com.sensoro.smartcity.server.bean;

import java.io.Serializable;

public class AlarmPopupDataLabelsBean implements Serializable {
    /**
     * title : 小区
     * id : 20001
     */

    private String title;
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
