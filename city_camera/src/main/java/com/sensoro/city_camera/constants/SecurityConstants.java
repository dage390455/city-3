package com.sensoro.city_camera.constants;

/**
 * @author : bin.tian
 * date   : 2019-06-26
 */
public interface SecurityConstants {
    /**
     * 预警无效
     */
    int SECURITY_INVALID = 0;
    /**
     * 预警有效
     */
    int SECURITY_VALID = 1;
    /**
     * 重点
     */
    int SECURITY_TYPE_FOCUS = 1;
    /**
     * 外来
     */
    int SECURITY_TYPE_FOREIGN = 2;
    /**
     * 入侵
     */
    int SECURITY_TYPE_INVADE = 3;
    /**
     * 预警未处理
     */
    int SECURITY_IS_NOT_HANDLE = 0;
    /**
     * 相机在线
     */
    int SECURITY_DEVICE_ONLINE = 1;

}
