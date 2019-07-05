package com.sensoro.common.server.security.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author  预警详情时间线事件
 */

public class SecurityAlarmEventInfo implements Serializable, Comparable<SecurityAlarmEventInfo>{
    public String id;
    public String type;//1 创建 2:处理结果信息 3 系统电话  4.系统短信
    public String name;
    public String content;
    public String source;//预警事件来源，Web/App
    public String handlerId;
    public EventHandler handler;
    public int status;
    public long createTime;
    public List<EventRecord> records;

    public static class EventHandler{
         public String email;
         public String name;
    }
    public static class EventRecord{
        public String content;
        public int status;
        public String createTime;
    }

    @Override
    public int compareTo(SecurityAlarmEventInfo o) {
        return 0;
    }
}
