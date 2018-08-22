package com.sensoro.smartcity.adapter;

import android.text.TextUtils;

import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.DateUtil;

import java.util.HashMap;
import java.util.List;

public class IndexListAdapterDiff extends DiffCallBack<DeviceInfo> {
    public IndexListAdapterDiff(List<DeviceInfo> oldList, List<DeviceInfo> newList) {
        super(oldList, newList);
    }

    @Override
    boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getSn().equals(mNewList.get(newItemPosition).getSn());
    }

    @Override
    boolean getContentsTheSame(DeviceInfo oldData, DeviceInfo newData) {
        return oldData.getUpdatedTime() == newData.getUpdatedTime();
    }

    @Override
    Object getChangePayload(DeviceInfo oldData, DeviceInfo newData) {
        HashMap<String, Object> payload = new HashMap<>();
        if (oldData.getStatus() != newData.getStatus()) {
            payload.put("status", newData.getStatus());
        }
        String oldDataName = oldData.getName();
        String newDataName = newData.getName();
        if (!TextUtils.isEmpty(oldDataName) || !TextUtils.isEmpty(newDataName)) {
            if (oldDataName == null) {
                oldDataName = "";
            }
            if (newDataName == null) {
                newDataName = "";
            }
            if (!oldDataName.equals(newDataName)) {
                payload.put("name", TextUtils.isEmpty(newDataName) ? newData.getSn() : newDataName);
            }
        }
        long oldDataUpdatedTime = oldData.getUpdatedTime();
        long newDataUpdatedTime = newData.getUpdatedTime();
        if (oldDataUpdatedTime != newDataUpdatedTime) {
            payload.put("updateTime", DateUtil.getFullParseDate(newDataUpdatedTime));
        }
        if (payload.isEmpty()) {
            return null;
        } else {
            return payload;
        }
    }
}
