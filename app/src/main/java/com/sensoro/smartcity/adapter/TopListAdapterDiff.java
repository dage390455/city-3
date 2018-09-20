package com.sensoro.smartcity.adapter;

import com.sensoro.smartcity.model.HomeTopModel;

import java.util.HashMap;
import java.util.List;

public class TopListAdapterDiff extends DiffCallBack<HomeTopModel> {
    public TopListAdapterDiff(List<HomeTopModel> oldList, List<HomeTopModel> newList) {
        super(oldList, newList);
    }

    @Override
    boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).type == mNewList.get(newItemPosition).type;
    }

    @Override
    boolean getContentsTheSame(HomeTopModel oldData, HomeTopModel newData) {
        return oldData.value == newData.value;
    }

    @Override
    Object getChangePayload(HomeTopModel oldData, HomeTopModel newData) {
        HashMap<String, Object> payload = new HashMap<>();
        if (oldData.type != newData.type) {
            payload.put("type", newData.type);
        }
        int oldDataValue = oldData.value;
        int newDataValue = newData.value;
        if (oldDataValue != newDataValue) {
            payload.put("value", newDataValue);
        }
        if (payload.isEmpty()) {
            return null;
        } else {
            return payload;
//            return null;
        }

    }
}
