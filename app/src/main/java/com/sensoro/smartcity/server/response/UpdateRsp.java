package com.sensoro.smartcity.server.response;

/**
 * Created by sensoro on 17/12/25.
 */

public class UpdateRsp extends ResponseBase{
    private int version;
    private String changelog;
    private String install_url;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changeLog) {
        this.changelog = changeLog;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    @Override
    public String toString() {
        return "UpdateRsp{" +
                "version=" + version +
                ", changelog='" + changelog + '\'' +
                ", install_url='" + install_url + '\'' +
                '}';
    }
}
