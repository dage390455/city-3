package com.sensoro.smartcity.model;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeTopModel {
    public int type;
    public int value;
    public final List<DeviceInfo> mDeviceList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    public int scrollOffset;
    public int scrollPosition;
    public SmartRefreshLayout smartRefreshLayout;
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

    public void recycleViewRefreshComplete() {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.finishRefresh();
            smartRefreshLayout.finishLoadMore();
        }

    }

    public void recycleViewRefreshCompleteNoMoreData() {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }

    }

    public void clearData() {
        type = 0;
        value = 0;
        mDeviceList.clear();
//        scrollOffset = 0;
//        scrollPosition = 0;
        if (innerAdapter != null) {
            innerAdapter.getData().clear();
        }
        if (smartRefreshLayout != null) {
            smartRefreshLayout = null;
        }

    }
}
