package com.sensoro.smartcity.adapter.DiffUtils;

import com.sensoro.smartcity.model.HomeTopModel;

import java.util.HashMap;
import java.util.List;

public class TopListAdapterDiff extends DiffCallBack<HomeTopModel> {
    public TopListAdapterDiff(List<HomeTopModel> oldList, List<HomeTopModel> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).type == mNewList.get(newItemPosition).type;
    }

    @Override
    public boolean getContentsTheSame(HomeTopModel oldData, HomeTopModel newData) {
        return oldData.value == newData.value;
    }

    @Override
    public Object getChangePayload(HomeTopModel oldData, HomeTopModel newData) {
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
