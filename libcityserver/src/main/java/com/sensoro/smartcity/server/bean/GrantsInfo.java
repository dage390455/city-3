package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class GrantsInfo implements Serializable {
    public List<String> getStation() {
        return station;
    }

    public void setStation(List<String> station) {
        this.station = station;
    }

    //    List<String> deploy;
//    List<String> groupNotice;
//    List<String> deviceGroup;
//    List<String> indoorMap;
//    List<String> account;
//    List<String> user;
//    List<String> task;
    List<String> station;
}
