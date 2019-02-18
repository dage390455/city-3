package com.sensoro.smartcity.constant;

public interface MonitorPointOperationCode {
    //短消音
    int ERASURE = 0x01;
    String ERASURE_STR = "mute";
    //复位
    int RESET = 0x02;
    String RESET_STR = "reset";
    //密码
    int PSD = 0x03;
    String PSD_STR = "password";
    //查询
    int QUERY = 0x04;
    String QUERY_STR = "view";
    //自检
    int SELF_CHECK = 0x05;
    String SELF_CHECK_STR = "check";
    //配置
    int AIR_SWITCH_CONFIG = 0x06;
    String AIR_SWITCH_CONFIG_STR = "config";
    //断电
    int AIR_SWITCH_POWER_OFF = 0x07;
    String AIR_SWITCH_POWER_OFF_STR = "open";
    //上电
    int AIR_SWITCH_POWER_ON = 0x08;
    String AIR_SWITCH_POWER_ON_STR = "close";
    //长消音
    int ERASURE_LONG = 0x09;
    String ERASURE_LONG_STR = "mute2";

}
