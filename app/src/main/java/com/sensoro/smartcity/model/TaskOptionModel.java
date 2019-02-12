package com.sensoro.smartcity.model;

import com.sensoro.smartcity.constant.MonitorPointOperationCode;

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
            add(MonitorPointOperationCode.ERASURE_STR);
            add(MonitorPointOperationCode.RESET_STR);
            add(MonitorPointOperationCode.PSD_STR);
            add(MonitorPointOperationCode.QUERY_STR);
            add(MonitorPointOperationCode.SELF_CHECK_STR);
            add(MonitorPointOperationCode.AIR_SWITCH_CONFIG_STR);
            add(MonitorPointOperationCode.AIR_SWITCH_POWER_OFF_STR);
            add(MonitorPointOperationCode.AIR_SWITCH_POWER_ON_STR);
        }
    };
    public int optionType;
    public String id;
    public boolean clickable;
    public int drawableResId;
    public int textColorResId;
    public int contentResId;
}
