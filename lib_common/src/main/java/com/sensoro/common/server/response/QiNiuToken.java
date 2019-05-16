package com.sensoro.common.server.response;

import java.io.Serializable;

public class QiNiuToken extends ResponseBase implements Serializable {

    /**
     * errcode : 0
     * uptoken : ds-uUH_gLTmmdWvXIcIIUxCIgM6LZ6MKJlBbxoJs:Nrl6ITn09C4GhfDbe5A5XC5AF3E
     * =:eyJzY29wZSI6InNlbnNvcm8tY2l0eSIsImRlYWRsaW5lIjoxNTMxMzYzODYzfQ==
     */

    private String uptoken;
    private String domain;

    public String getUptoken() {
        return uptoken;
    }

    public void setUptoken(String uptoken) {
        this.uptoken = uptoken;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
