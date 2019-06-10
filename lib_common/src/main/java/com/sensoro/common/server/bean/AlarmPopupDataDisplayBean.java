package com.sensoro.common.server.bean;

import java.util.List;

public class AlarmPopupDataDisplayBean {


    /**
     * title : 安全隐患
     * displayStatus : 4
     * items : [{"refer":"reason","require":true},{"refer":"place","require":true}]
     */

    private String title;
    private int displayStatus;
    private String description;
    private List<AlarmPopupDataDisplayItemsBean> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(int displayStatus) {
        this.displayStatus = displayStatus;
    }

    public List<AlarmPopupDataDisplayItemsBean> getItems() {
        return items;
    }

    public void setItems(List<AlarmPopupDataDisplayItemsBean> items) {
        this.items = items;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
