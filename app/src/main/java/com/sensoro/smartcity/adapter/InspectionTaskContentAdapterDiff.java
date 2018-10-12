package com.sensoro.smartcity.adapter;

import android.text.TextUtils;

import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;

import java.util.HashMap;
import java.util.List;

public class InspectionTaskContentAdapterDiff extends DiffCallBack<InspectionTaskDeviceDetail> {
    public InspectionTaskContentAdapterDiff(List<InspectionTaskDeviceDetail> oldList, List<InspectionTaskDeviceDetail> newList) {
        super(oldList, newList);
    }

    @Override
    boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getSn().equalsIgnoreCase(mNewList.get(newItemPosition).getSn());
    }

    @Override
    boolean getContentsTheSame(InspectionTaskDeviceDetail oldData, InspectionTaskDeviceDetail newData) {
        boolean bStatus = oldData.getStatus() == newData.getStatus();
        boolean bNear = oldData.isNearBy_local() == newData.isNearBy_local();
        String oldDataName = oldData.getName();
        String newDataName = newData.getName();
        if (!TextUtils.isEmpty(newDataName)) {
            return bStatus && bNear && newDataName.equalsIgnoreCase(oldDataName);
        }
        return bStatus && bNear;
    }

    @Override
    Object getChangePayload(InspectionTaskDeviceDetail oldData, InspectionTaskDeviceDetail newData) {
        HashMap<String, Object> payload = new HashMap<>();
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
