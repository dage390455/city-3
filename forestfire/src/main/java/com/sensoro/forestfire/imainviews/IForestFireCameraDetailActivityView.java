package com.sensoro.forestfire.imainviews;

/**
 * @Author: jack
 * 时  间: 2019-09-19
 * 包  名: com.sensoro.forestfire.imainviews
 * 简  述: <功能简述>
 */
public interface IForestFireCameraDetailActivityView {

    void  updateTitle(String title);

    void updateCameraName(String name);

    void updateCameraType(String type);

    void updateTime(String time);


    void updateDeviceSN(String sn);

    void updateGateway(String gateway);

    void updateLocation(double lon,double lat);


}
