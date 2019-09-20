package com.sensoro.smartcity.model;

public class SortConditionModel {
    public String order;
    public String sort;
    public String title;
    //请求字段
    public boolean isSelected;

    public SortConditionModel(String order, String sort, String title, boolean isSelected) {
        this.order = order;
        this.sort = sort;
        this.title = title;
        this.isSelected = isSelected;

    }
}
