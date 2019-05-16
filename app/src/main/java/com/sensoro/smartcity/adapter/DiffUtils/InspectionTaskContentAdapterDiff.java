package com.sensoro.smartcity.adapter.DiffUtils;

import android.text.TextUtils;

import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.util.LogUtils;

import java.util.HashMap;
import java.util.List;

public class InspectionTaskContentAdapterDiff extends DiffCallBack<InspectionTaskDeviceDetail> {
    public InspectionTaskContentAdapterDiff(List<InspectionTaskDeviceDetail> oldList, List<InspectionTaskDeviceDetail> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        InspectionTaskDeviceDetail oldIn = mOldList.get(oldItemPosition);
        InspectionTaskDeviceDetail newIn = mNewList.get(newItemPosition);
        try {
            LogUtils.loge(this, "getContentsTheSame bNear = " + true + ",old = " + oldIn.isNearBy_local() + ",new = " + newIn.isNearBy_local());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return oldIn.getId().equalsIgnoreCase(newIn.getId());
    }

    @Override
    public boolean getContentsTheSame(InspectionTaskDeviceDetail oldData, InspectionTaskDeviceDetail newData) {
        boolean bStatus = oldData.getStatus() == newData.getStatus();
        boolean nearBy_local = oldData.isNearBy_local();
        boolean nearBy_local1 = newData.isNearBy_local();
        boolean bNear = nearBy_local == nearBy_local1;
        try {
            LogUtils.loge(this, "getContentsTheSame bNear = " + bNear + ",old = " + nearBy_local + ",new = " + nearBy_local1);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        String oldDataName = oldData.getName();
        String newDataName = newData.getName();
        if (!TextUtils.isEmpty(newDataName)) {
            return bStatus && bNear && newDataName.equalsIgnoreCase(oldDataName);
        }
        return bStatus && bNear;
    }

    @Override
    public Object getChangePayload(InspectionTaskDeviceDetail oldData, InspectionTaskDeviceDetail newData) {
        final HashMap<String, Object> payload = new HashMap<>();
        if (oldData.getStatus() != newData.getStatus()) {
            payload.put("status", newData.getStatus());
        }
        boolean bOldNear = oldData.isNearBy_local();
        boolean bNewNear = newData.isNearBy_local();

        if (bOldNear != bNewNear) {
            payload.put("bNear", bNewNear);
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
