package com.sensoro.common.server.security.bean;

import java.io.Serializable;

/**
 * @author  预警详情时间线事件
 */

public class SecurityAlarmEventInfo implements Serializable, Comparable<SecurityAlarmEventInfo>{
    public String id;
    public String name;
    public String content;
    public String status;
    public String handlerId;
    public EventHandler handler;
    public long createTime;

    class EventHandler{
         String email;
         String name;

    }

    @Override
    public int compareTo(SecurityAlarmEventInfo o) {
        return 0;
    }
}
