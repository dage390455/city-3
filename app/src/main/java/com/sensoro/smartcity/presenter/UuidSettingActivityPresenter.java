package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.activity.UUIDAddMySettingActivity;
import com.sensoro.smartcity.imainviews.IUuidSettingActivityView;
import com.sensoro.smartcity.model.UuidSettingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UuidSettingActivityPresenter extends BasePresenter<IUuidSettingActivityView> implements IOnCreate {
    private final List<UuidSettingModel> mData = new ArrayList<>();
    private final List<UuidSettingModel> mMyData = new ArrayList<>();
    private Activity mContext;
    private volatile UuidSettingModel currentUUID;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mData.add(new UuidSettingModel("Sensoro Alpha 系统默认", "70DC44C3-E2A8-4B22-A2C6-129B41A4BDBC"));
        mData.add(new UuidSettingModel("Sensoro iBeacon 默认", "23A01AF0-232A-4518-9C0E-323FB773F5EF"));
        mData.add(new UuidSettingModel("AirLocate", "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"));
        mData.add(new UuidSettingModel("Estimote", "B9407F30-F5F8-466E-AFF9-25556B57FE6D"));
        mData.add(new UuidSettingModel("微信", "FDA50693-A4E2-4FB1-AFCF-C6EB07647825"));
        mData.add(new UuidSettingModel("微信2", "AB8190D5-D11E-4941-ACC4-42F30510B408"));
        mData.add(new UuidSettingModel("Kontakt", "F7826DA6-4FA2-4E98-8024-BC5B71E0893E"));
        //TODO 判断当前UUID
        Serializable current_uuid = mContext.getIntent().getSerializableExtra("current_uuid");
        if (current_uuid instanceof UuidSettingModel) {
            currentUUID = (UuidSettingModel) current_uuid;
            currentUUID.isCheck = true;
            getView().setCurrentUUID(currentUUID.uuid);
        }
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).equals(currentUUID)) {
                mData.set(i, currentUUID);
            }
        }
        getView().updateNormalAdapter(mData);
        List<String> saveMyUUID = PreferencesHelper.getInstance().getSaveMyUUID();
        handleMyUUIDData(saveMyUUID);
        for (int i = 0; i < mMyData.size(); i++) {
            if (mMyData.get(i).equals(currentUUID)) {
                mMyData.set(i, currentUUID);
            }
        }
        getView().updateMyAdapter(mMyData);
    }

    private void handleMyUUIDData(List<String> saveMyUUID) {
        if (saveMyUUID != null && !saveMyUUID.isEmpty()) {
            for (int i = 0; i < saveMyUUID.size(); i++) {
                UuidSettingModel uuidSettingModel = new UuidSettingModel("自定义UUID " + (i + 1), saveMyUUID.get(i));
                mMyData.add(uuidSettingModel);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doSave() {
        if (currentUUID == null) {
            SensoroToast.getInstance().makeText("请选择一个uuid", Toast.LENGTH_SHORT).show();
        } else {
            EventBus.getDefault().post(currentUUID);
            getView().finishAc();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_ADD_NEW_UUID_CONTENT:
                //
                if (data instanceof String) {
                    String uuid = (String) data;
                    UuidSettingModel uuidSettingModel = new UuidSettingModel();
                    uuidSettingModel.uuid = uuid;
                    mMyData.add(uuidSettingModel);
                    getView().updateMyAdapter(mMyData);
                    ArrayList<String> strings = new ArrayList<>();
                    for (UuidSettingModel mMyDatum : mMyData) {
                        strings.add(mMyDatum.uuid);
                    }
                    PreferencesHelper.getInstance().saveMyUUID(strings);
                }
                break;
        }
    }

    public void addNewUUID() {
        Intent intent = new Intent(mContext, UUIDAddMySettingActivity.class);
        getView().startAC(intent);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void clickNormalUUID(UuidSettingModel currentUUID) {
        for (UuidSettingModel mMyDatum : mMyData) {
            mMyDatum.isCheck = false;
        }
        getView().updateMyAdapter(mMyData);
        this.currentUUID = currentUUID;
    }

    public void clickMyUUID(UuidSettingModel currentUUID) {
        for (UuidSettingModel mDatum : mData) {
            mDatum.isCheck = false;
        }
        getView().updateNormalAdapter(mData);
        this.currentUUID = currentUUID;
    }
}
