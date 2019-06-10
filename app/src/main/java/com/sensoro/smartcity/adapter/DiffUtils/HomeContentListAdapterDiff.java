package com.sensoro.smartcity.adapter.DiffUtils;

import android.text.TextUtils;

import com.sensoro.common.server.bean.DeviceInfo;

import java.util.HashMap;
import java.util.List;

public class HomeContentListAdapterDiff extends DiffCallBack<DeviceInfo> {
    public HomeContentListAdapterDiff(List<DeviceInfo> oldList, List<DeviceInfo> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getSn().equalsIgnoreCase(mNewList.get(newItemPosition).getSn());
    }

    @Override
    public boolean getContentsTheSame(DeviceInfo oldData, DeviceInfo newData) {
        boolean bStatus = oldData.getStatus() == newData.getStatus();
        boolean bTime = oldData.getUpdatedTime() == newData.getUpdatedTime();
        String oldDataName = oldData.getName();
        String newDataName = newData.getName();
        if (!TextUtils.isEmpty(newDataName)) {
            return bStatus && bTime && newDataName.equalsIgnoreCase(oldDataName);
        }
        return bStatus && bTime;
    }

    @Override
    protected Object getChangePayload(DeviceInfo oldData, DeviceInfo newData) {
        HashMap<String, Object> payload = new HashMap<>();
        if (oldData.getStatus() != newData.getStatus()) {
            payload.put("status", newData.getStatus());
        }
        long oldDataUpdatedTime = oldData.getUpdatedTime();
        long newDataUpdatedTime = newData.getUpdatedTime();

        if (oldDataUpdatedTime != newDataUpdatedTime) {
            payload.put("updateTime", newDataUpdatedTime);
        }
        String oldDataName = oldData.getName();
        String newDataName = newData.getName();
        if (!TextUtils.isEmpty(newDataName)) {
            if (!newDataName.equalsIgnoreCase(oldDataName)) {
                payload.put("name", newDataName);
            }
        }
        if (payload.isEmpty()) {
            return null;
        } else {
            return payload;
        }

    }
}
