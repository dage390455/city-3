package com.sensoro.smartcity.adapter.DiffUtils;

import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.util.LogUtils;

import java.util.HashMap;
import java.util.List;

public class TopListAdapterDiff extends DiffCallBack<HomeTopModel> {
    public TopListAdapterDiff(List<HomeTopModel> oldList, List<HomeTopModel> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        int oldType = mOldList.get(oldItemPosition).type;
        int newType = mNewList.get(newItemPosition).type;
        boolean b = oldType == newType;
        LogUtils.loge("updateData-----getItemsTheSame-->>b = " + b + ",oldType = " + oldType + ",newType = " + newType);
        return b;
    }

    @Override
    public boolean getContentsTheSame(HomeTopModel oldData, HomeTopModel newData) {
        int oldValue = oldData.value;
        int newValue = newData.value;
        boolean b = oldValue == newValue;
        LogUtils.loge("updateData-----getContentsTheSame-->>b = " + b + ",oldValue = " + oldValue + ",newValue = " + newValue);
        return b;
    }

    @Override
    public Object getChangePayload(HomeTopModel oldData, HomeTopModel newData) {
        LogUtils.loge("updateData-----getChangePayload-->>oldData = " + oldData + ", newData = " + newData);
        HashMap<String, Object> payload = new HashMap<>();
        if (oldData.type != newData.type) {
            payload.put("type", newData.type);
        }
        if (oldData.value != newData.value) {
            payload.put("value", newData.value);
        }
        if (payload.isEmpty()) {
            return null;
        } else {
            return payload;
        }

    }
}
