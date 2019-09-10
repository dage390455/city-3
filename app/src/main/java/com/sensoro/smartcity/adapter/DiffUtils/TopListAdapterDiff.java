package com.sensoro.smartcity.adapter.DiffUtils;

import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.common.utils.LogUtils;

import java.util.HashMap;
import java.util.List;

public class TopListAdapterDiff extends DiffCallBack<HomeTopModel> {
    public TopListAdapterDiff(List<HomeTopModel> oldList, List<HomeTopModel> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        int oldType = mOldList.get(oldItemPosition).status;
        int newType = mNewList.get(newItemPosition).status;
        boolean b = oldType == newType;
        try {
            LogUtils.loge("updateData-----getItemsTheSame-->>b = " + b + ",oldType = " + oldType + ",newType = " + newType);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return b;
    }

    @Override
    public boolean getContentsTheSame(HomeTopModel oldData, HomeTopModel newData) {
        int oldValue = oldData.value;
        int newValue = newData.value;
        boolean b = oldValue == newValue;
        try {
            LogUtils.loge("updateData-----getContentsTheSame-->>b = " + b + ",oldValue = " + oldValue + ",newValue = " + newValue);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return b;
    }

    @Override
    public Object getChangePayload(HomeTopModel oldData, HomeTopModel newData) {
        try {
            LogUtils.loge("updateData-----getChangePayload-->>oldData = " + oldData + ", newData = " + newData);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        HashMap<String, Object> payload = new HashMap<>();
        if (oldData.status != newData.status) {
            payload.put("type", newData.status);
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
