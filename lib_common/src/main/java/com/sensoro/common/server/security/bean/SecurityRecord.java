package com.sensoro.common.server.security.bean;

import java.io.Serializable;

/**
 * @author : bin.tian
 * date   : 2019-06-27
 */
public class SecurityRecord implements Serializable {
    public String eventId;
    public String createTime;
    public String endTime;
    public String beginTime;
    public String sn;
    public String location;
    public String videoSize;
    public String mediaUrl;
    public String coverUrl;
}
