package com.sensoro.smartcity.model;

import java.util.ArrayList;
import java.util.List;

public class TaskOptionModel {
    //    /        "taskOptions": [
    //     * // 是否支持消音
//     * "mute",
//     * // 是否支持复位
//     * "reset",
//     * // 是否支持修改密码
//     * "password",
//     * // 是否支持查看设备
//     * "view",
//     * // 是否支持自检
//     * "check",
//     * // 是否支持配置
//     * "config",
//     * // 是否支持下行断电
//     * "open",
//     * // 是否支持下行上电
//     * "close"
//                * ]
    public static final List<String> taskOptionsList = new ArrayList<String>() {
        {
            add("mute");
            add("reset");
            add("password");
            add("view");
            add("check");
            add("config");
            add("open");
            add("close");
        }
    };
    public int optionType;
    public String id;
    public boolean clickable;
    public int drawableResId;
    public int textColorResId;
    public int contentResId;
}
