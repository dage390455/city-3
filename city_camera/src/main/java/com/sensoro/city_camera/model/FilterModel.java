package com.sensoro.city_camera.model;

/**
 * 筛选模式
 * @author qinghao.wang
 */
public class FilterModel {
    //对应含义
    public long status;
    //对应筛选标题
    public String statusTitle;
    //是否选择
    //public boolean isChecked;
    //是否默认选择
    public boolean isDefault;
    public boolean isSpecialShow;//选择后特殊处理的选项

    public FilterModel( String statusTitle,Long status, boolean isDefault,boolean isSpecialShow) {
        this.status = status;
        this.statusTitle = statusTitle;
        this.isDefault = isDefault;
        this.isSpecialShow = isSpecialShow;
    }
}
