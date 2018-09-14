package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeploySettingNameActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeploySettingNameActivityPresenter extends BasePresenter<IDeploySettingNameActivityView> implements
        Constants {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private final List<String> mHistoryKeywords = new ArrayList<>();
    private CharSequence tempWords = "";
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mPref = mContext.getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        String name = mContext.getIntent().getStringExtra(EXTRA_SETTING_NAME_ADDRESS);
        String history = mPref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        if (mHistoryKeywords.size() > 0) {
            getView().setSearchHistoryLayoutVisible(true);
            getView().updateSearchHistoryData(mHistoryKeywords);
        } else {
            getView().setSearchHistoryLayoutVisible(false);
        }

        if (!TextUtils.isEmpty(name) && !name.equals(mContext.getResources().getString(R.string
                .tips_hint_name_address_set))) {
            getView().setEditText(name);
        } else {
            getView().setEditText("");
        }

    }

    private void save(String text) {
        String oldText = mPref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                List<String> list = new ArrayList<>();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        list.add(o);
                    }
                }
                list.add(0, text);
                mHistoryKeywords.clear();
                mHistoryKeywords.addAll(list);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i == (list.size() - 1)) {
                        stringBuilder.append(list.get(i));
                    } else {
                        stringBuilder.append(list.get(i)).append(",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, stringBuilder.toString());
                mEditor.commit();
            } else {
                if (TextUtils.isEmpty(oldText)) {
                    mEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, text);
                    mEditor.commit();
                } else {
                    mEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, text + "," + oldText);
                    mEditor.commit();
                }
                mHistoryKeywords.add(0, text);
            }
        }
    }

    public void doChoose(String text) {
        if (!TextUtils.isEmpty(text)) {
            byte[] bytes = new byte[0];
            try {
                bytes = text.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort("名称/地址最长不能超过16个汉字或48个字符");
                return;
            }

        } else {
            getView().toastShort("必须输入名称/地址");
            return;
        }
        save(text);
//        mKeywordEt.clearFocus();
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void handleTextChanged(CharSequence s, int start, int before, int count) {
        tempWords = s;
        if (!TextUtils.isEmpty(s)) {
            String text = s.toString();
            getView().setSearchHistoryLayoutVisible(false);
            getView().setSearchRelationLayoutVisible(true);
            filterDeviceInfoByNameAndAddress(text);
        } else {
            getView().setSearchHistoryLayoutVisible(true);
            getView().setSearchRelationLayoutVisible(false);
        }

    }

    private void filterDeviceInfoByNameAndAddress(String filter) {
        List<DeviceInfo> originDeviceInfoList = new ArrayList<>(SensoroCityApplication.getInstance().getData());
        ArrayList<DeviceInfo> deleteDeviceInfoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            if (!TextUtils.isEmpty(deviceInfo.getName())) {
                if (!deviceInfo.getName().contains(filter.toUpperCase())) {
                    deleteDeviceInfoList.add(deviceInfo);
                }
            } else {
                deleteDeviceInfoList.add(deviceInfo);
            }
        }
        for (DeviceInfo deviceInfo : deleteDeviceInfoList) {
            originDeviceInfoList.remove(deviceInfo);
        }
        List<String> tempList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            if (!TextUtils.isEmpty(deviceInfo.getName())) {
                tempList.add(deviceInfo.getName());
            }
        }
        getView().updateRelationData(tempList);
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords.clear();
    }
}
