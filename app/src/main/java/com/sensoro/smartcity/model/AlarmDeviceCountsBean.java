package com.sensoro.smartcity.model;

import com.google.gson.annotations.SerializedName;

public class AlarmDeviceCountsBean {
    /**
     * 0 : 0
     * 1 : 17
     * 2 : 74
     * 3 : 10
     */

    @SerializedName("0")
    private int _$0;
    @SerializedName("1")
    private int _$1;
    @SerializedName("2")
    private int _$2;
    @SerializedName("3")
    private int _$3;
    @SerializedName("4")
    private int _$4;

    public int get_$4() {
        return _$4;
    }

    public void set_$4(int _$4) {
        this._$4 = _$4;
    }

    public int get_$0() {
        return _$0;
    }

    public void set_$0(int _$0) {
        this._$0 = _$0;
    }

    public int get_$1() {
        return _$1;
    }

    public void set_$1(int _$1) {
        this._$1 = _$1;
    }

    public int get_$2() {
        return _$2;
    }

    public void set_$2(int _$2) {
        this._$2 = _$2;
    }

    public int get_$3() {
        return _$3;
    }

    public void set_$3(int _$3) {
        this._$3 = _$3;
    }

    @Override
    public String toString() {
        return "AlarmDeviceCountsBean{ " +
                "_$0=" + _$0 +
                ", _$1=" + _$1 +
                ", _$2=" + _$2 +
                ", _$3=" + _$3 +
                ", _$4=" + _$4 +
                '}' ;
    }
}
