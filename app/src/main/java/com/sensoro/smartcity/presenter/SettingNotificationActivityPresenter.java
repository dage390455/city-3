package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.IbeaconSettingData;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.activity.UuidSettingActivity;
import com.sensoro.smartcity.imainviews.ISettingNotificationActivityView;
import com.sensoro.smartcity.model.UuidSettingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class SettingNotificationActivityPresenter extends BasePresenter<ISettingNotificationActivityView> implements IOnCreate {
    private Activity mActivity;
    private volatile IbeaconSettingData currentIbeaconSettingData;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        onCreate();
        Serializable serializable = mActivity.getIntent().getSerializableExtra("ibeaconSettingData");
        if (serializable instanceof IbeaconSettingData) {
            currentIbeaconSettingData = (IbeaconSettingData) serializable;
        }
        getView().setDeviceInChecked(currentIbeaconSettingData.switchIn);
        getView().setDeviceOutChecked(currentIbeaconSettingData.switchOut);
        getView().setDeviceInEditContent(currentIbeaconSettingData.switchInMessage);
        getView().setDeviceOutEditContent(currentIbeaconSettingData.switchOutMessage);
        getView().setDeviceUUID(currentIbeaconSettingData.currentUUID);
        if (currentIbeaconSettingData.currentMajor != null) {
            getView().setDeviceMajor(String.valueOf(currentIbeaconSettingData.currentMajor));
        }
        if (currentIbeaconSettingData.currentMirror != null) {
            getView().setDeviceMirror(String.valueOf(currentIbeaconSettingData.currentMirror));
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doSave(boolean switchIn, String switchInMessage, boolean switchOut, String switchOutMessage, String currentUUID, String major, String mirror) {
        currentIbeaconSettingData.switchIn = switchIn;
        if (!TextUtils.isEmpty(switchInMessage)) {
            currentIbeaconSettingData.switchInMessage = switchInMessage;
        }
        currentIbeaconSettingData.switchOut = switchOut;
        if (!TextUtils.isEmpty(switchOutMessage)) {
            currentIbeaconSettingData.switchOutMessage = switchOutMessage;
        }
        if (!TextUtils.isEmpty(currentUUID)) {
            currentIbeaconSettingData.currentUUID = currentUUID;
        }
        if (!TextUtils.isEmpty(major)) {
            try {
                currentIbeaconSettingData.currentMajor = Integer.parseInt(major);
            } catch (Exception e) {
                SensoroToast.getInstance().makeText(mActivity.getString(com.sensoro.common.R.string.enter_the_correct_number_format), Toast.LENGTH_SHORT).show();
                return;
            }

        }
        if (!TextUtils.isEmpty(mirror)) {
            try {
                currentIbeaconSettingData.currentMirror = Integer.parseInt(mirror);
            } catch (Exception e) {
                SensoroToast.getInstance().makeText(mActivity.getString(com.sensoro.common.R.string.enter_the_correct_number_format), Toast.LENGTH_SHORT).show();
                return;
            }

        }
        PreferencesHelper.getInstance().setIbeaconSettingData(currentIbeaconSettingData);
        EventBus.getDefault().post(currentIbeaconSettingData);
        getView().finishAc();
    }

    public void goSettingUUID(String uuid) {
        Intent intent = new Intent(mActivity, UuidSettingActivity.class);
        if (!TextUtils.isEmpty(uuid)) {
            UuidSettingModel uuidSettingModel = new UuidSettingModel();
            uuidSettingModel.uuid = uuid;
            intent.putExtra("current_uuid", uuidSettingModel);
        }
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UuidSettingModel uuidSettingModel) {
        if (uuidSettingModel != null) {
            getView().setDeviceUUID(uuidSettingModel.uuid);
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
