package com.sensoro.smartcity.model;

import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeTopModel implements Serializable {
    public int type;
    public int value;
    public final List<DeviceInfo> mDeviceList = new ArrayList<DeviceInfo>();
    public int scrollOffset;
    public int scrollPosition;
    public MainHomeFragRcContentAdapter innerAdapter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeTopModel that = (HomeTopModel) o;
        return type == that.type &&
                value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public HomeTopModel clone() throws CloneNotSupportedException {
        HomeTopModel bean = null;
        try {
            bean = (HomeTopModel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public void clearData() {
        value = 0;
        mDeviceList.clear();
        scrollOffset = 0;
        scrollPosition = 0;
        if (innerAdapter != null) {
            innerAdapter.getData().clear();
        }
    }
}
