package com.sensoro.common.server.security.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author : bin.tian
 * date   : 2019-06-27
 */
public class SecurityWarnRecord implements Serializable {
    public String id;
    public List<SecurityRecord> recordList;
}
