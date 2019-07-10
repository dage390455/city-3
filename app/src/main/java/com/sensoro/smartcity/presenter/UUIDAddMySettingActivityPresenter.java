package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.imainviews.IUUIDAddMySettingActivityView;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class UUIDAddMySettingActivityPresenter extends BasePresenter<IUUIDAddMySettingActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
    }

    @Override
    public void onDestroy() {

    }

    public void doAddNewUUID(String uuid) {
        if (!TextUtils.isEmpty(uuid) && !"添加自定义UUID".equals(uuid)) {
            try {
                UUID uuid1 = UUID.fromString(uuid);
                String s = uuid1.toString();
                EventData eventData = new EventData();
                eventData.code = Constants.EVENT_DATA_ADD_NEW_UUID_CONTENT;
                eventData.data = s.toUpperCase();
                EventBus.getDefault().post(eventData);
                getView().finishAc();
            } catch (Exception e) {
                SensoroToast.getInstance().makeText("清输入正确的UUID", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
