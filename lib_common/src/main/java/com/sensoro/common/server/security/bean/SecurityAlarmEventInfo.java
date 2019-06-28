package com.sensoro.common.server.security.bean;

import java.io.Serializable;

/**
 * @author  预警详情时间线事件
 */

public class SecurityAlarmEventInfo implements Serializable, Comparable<SecurityAlarmEventInfo>{
    public String id;
    public String name;
    public String content;
    public int status;
    public String handlerId;
    public EventHandler handler;
    public long createTime;
    public String type;//2:处理结果信息
    public String source;//预警事件来源，Web/App

    public static class EventHandler{
         public String email;
         public String name;
    }

    @Override
    public int compareTo(SecurityAlarmEventInfo o) {
        return 0;
    }
}
