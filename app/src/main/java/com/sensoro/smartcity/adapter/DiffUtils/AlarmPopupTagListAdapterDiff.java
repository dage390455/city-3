package com.sensoro.smartcity.adapter.DiffUtils;

import android.text.TextUtils;

import com.sensoro.smartcity.model.AlarmPopupModel;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AlarmPopupTagListAdapterDiff extends DiffCallBack<AlarmPopupModel.AlarmPopupTagModel> {
    public AlarmPopupTagListAdapterDiff(List<AlarmPopupModel.AlarmPopupTagModel> oldList, List<AlarmPopupModel.AlarmPopupTagModel> newList) {
        super(oldList, newList);
    }

    @Override
    public boolean getItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).name.equalsIgnoreCase(mNewList.get(newItemPosition).name);
    }

    @Override
    public boolean getContentsTheSame(AlarmPopupModel.AlarmPopupTagModel oldData, AlarmPopupModel.AlarmPopupTagModel newData) {
        return oldData.isChose == newData.isChose;
    }

    @Override
    protected Object getChangePayload(AlarmPopupModel.AlarmPopupTagModel oldData, AlarmPopupModel.AlarmPopupTagModel newData) {
        HashMap<String, Object> payload = new HashMap<>();
        if (!Objects.equals(oldData.id, newData.id)) {
            payload.put("id", newData.id);
        }
        boolean oldIsChose = oldData.isChose;
        boolean newIsChose = newData.isChose;

        if (oldIsChose != newIsChose) {
            payload.put("isChose", newIsChose);
        }
        String oldDataName = oldData.name;
        String newDataName = newData.name;
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
