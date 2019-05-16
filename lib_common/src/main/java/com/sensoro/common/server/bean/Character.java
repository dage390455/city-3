package com.sensoro.common.server.bean;

import java.io.Serializable;

/**
 * Created by sensoro on 17/11/13.
 */

public class Character implements Serializable{

    private String shortName;
    private boolean isApply;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isApply() {
        return isApply;
    }

    public void setApply(boolean apply) {
        isApply = apply;
    }
}
