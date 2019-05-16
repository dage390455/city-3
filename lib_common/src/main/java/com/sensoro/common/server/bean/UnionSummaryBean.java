package com.sensoro.common.server.bean;

import java.io.Serializable;

public class UnionSummaryBean implements Serializable{

    /**
     * unionType : installed|smoke
     * num : 1
     * _id : 5babbf47dd54bfaab6318685
     */

    private String unionType;
    private int num;
    private String _id;

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
