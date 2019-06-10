package com.sensoro.common.server.bean;

/**
 * Created by sensoro on 17/12/25.
 */

public class UpdateInfo {
    private String version;
    private String url;
    private String msg;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
