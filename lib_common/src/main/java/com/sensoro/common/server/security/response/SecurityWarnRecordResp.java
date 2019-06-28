package com.sensoro.common.server.security.response;

import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.server.security.bean.SecurityWarnRecord;

import okhttp3.Response;

import java.io.Serializable;

/**
 * @author : bin.tian
 * date   : 2019-06-27
 */
public class SecurityWarnRecordResp extends ResponseBase implements Serializable {
    public SecurityWarnRecord data;
}
