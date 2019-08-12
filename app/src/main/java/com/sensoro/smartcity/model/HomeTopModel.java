package com.sensoro.smartcity.model;

import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.common.server.bean.DeviceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeTopModel implements Serializable {
    public int status;
    public int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeTopModel that = (HomeTopModel) o;
        return status == that.status &&
                value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, value);
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
    }

}
