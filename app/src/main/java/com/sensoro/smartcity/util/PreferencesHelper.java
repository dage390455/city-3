package com.sensoro.smartcity.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sensoro on 17/7/4.
 */

public final class PreferencesHelper implements Constants {

    private volatile static PreferencesHelper instance;
    private volatile EventLoginData mEventLoginData;
    private volatile DeviceMergeTypesInfo mDeviceMergeTypesInfo;

    //    private SharedPreferences splashLoginData;
    private PreferencesHelper() {
    }

    public static PreferencesHelper getInstance() {
        if (instance == null) {
            synchronized (PreferencesHelper.class) {
                if (instance == null) {
                    instance = new PreferencesHelper();
                }
            }
        }
        return instance;
    }


    public void saveUserData(EventLoginData eventLoginData) {
        mEventLoginData = eventLoginData;
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SPLASH_LOGIN_DATA, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //
        //String loginDataJson = RetrofitServiceHelper.INSTANCE.getGson().toJson(eventLoginData);
        //EventLoginData eventLoginData1 = RetrofitServiceHelper.INSTANCE.getGson().fromJson(loginDataJson, EventLoginData.class);

        editor.putString(EXTRA_USER_ID, eventLoginData.userId);
        editor.putString(EXTRA_USER_NAME, eventLoginData.userName);
        editor.putString(EXTRA_PHONE, eventLoginData.phone);
        editor.putString(EXTRA_PHONE_ID, eventLoginData.phoneId);
        //TODO character
        editor.putString(EXTRA_USER_ROLES, eventLoginData.roles);
        editor.putBoolean(EXTRA_IS_SPECIFIC, eventLoginData.isSupperAccount);
        editor.putBoolean(EXTRA_GRANTS_HAS_STATION, eventLoginData.hasStation);
        editor.putBoolean(EXTRA_GRANTS_HAS_CONTRACT, eventLoginData.hasContract);
        editor.putBoolean(EXTRA_GRANTS_HAS_SCAN_LOGIN, eventLoginData.hasScanLogin);
        editor.putBoolean(EXTRA_GRANTS_HAS_SUB_MERCHANT, eventLoginData.hasSubMerchant);
        editor.putBoolean(EXTRA_GRANTS_HAS_MERCHANT_CHANGE, eventLoginData.hasMerchantChange);
        editor.putBoolean(EXTRA_GRANTS_HAS_INSPECTION_TASK_LIST, eventLoginData.hasInspectionTaskList);
        editor.putBoolean(EXTRA_GRANTS_HAS_INSPECTION_TASK_MODIFY, eventLoginData.hasInspectionTaskModify);
        editor.putBoolean(EXTRA_GRANTS_HAS_INSPECTION_DEVICE_LIST, eventLoginData.hasInspectionDeviceList);
        editor.putBoolean(EXTRA_GRANTS_HAS_INSPECTION_DEVICE_MODIFY, eventLoginData.hasInspectionDeviceModify);
        editor.putBoolean(EXTRA_GRANTS_HAS_ALARM_LOG_INFO, eventLoginData.hasAlarmInfo);
        editor.putBoolean(EXTRA_GRANTS_HAS_MALFUNCTION_INFO, eventLoginData.hasMalfunction);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_BRIEF, eventLoginData.hasDeviceBrief);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_SIGNAL_CHECK, eventLoginData.hasSignalCheck);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_SIGNAL_CONFIG, eventLoginData.hasSignalConfig);
        editor.putBoolean(EXTRA_GRANTS_HAS_BAD_SIGNAL_UPLOAD, eventLoginData.hasBadSignalUpload);
        //
        editor.apply();
    }

    public EventLoginData getUserData() {
        if (mEventLoginData == null) {
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SPLASH_LOGIN_DATA, Context
                    .MODE_PRIVATE);
            String phoneId = sp.getString(EXTRA_PHONE_ID, null);
            String userId = sp.getString(EXTRA_USER_ID, null);
            LogUtils.loge(this, "phoneId = " + phoneId + ",userId = " + userId);
            String userName = sp.getString(EXTRA_USER_NAME, null);
            String phone = sp.getString(EXTRA_PHONE, null);
            String roles = sp.getString(EXTRA_USER_ROLES, null);
            boolean isSupperAccount = sp.getBoolean(EXTRA_IS_SPECIFIC, false);
            boolean hasStation = sp.getBoolean(EXTRA_GRANTS_HAS_STATION, false);
            boolean hasContract = sp.getBoolean(EXTRA_GRANTS_HAS_CONTRACT, false);
            boolean hasScanLogin = sp.getBoolean(EXTRA_GRANTS_HAS_SCAN_LOGIN, false);
            boolean hasSubMerchant = sp.getBoolean(EXTRA_GRANTS_HAS_SUB_MERCHANT, false);
            boolean hasMerchantChange = sp.getBoolean(EXTRA_GRANTS_HAS_MERCHANT_CHANGE, false);
            boolean hasInspectionTaskList = sp.getBoolean(EXTRA_GRANTS_HAS_INSPECTION_TASK_LIST, false);
            boolean hasInspectionTaskModify = sp.getBoolean(EXTRA_GRANTS_HAS_INSPECTION_TASK_MODIFY, false);
            boolean hasInspectionDeviceList = sp.getBoolean(EXTRA_GRANTS_HAS_INSPECTION_DEVICE_LIST, false);
            boolean hasInspectionDeviceModify = sp.getBoolean(EXTRA_GRANTS_HAS_INSPECTION_DEVICE_MODIFY, false);
            boolean hasAlarmInfo = sp.getBoolean(EXTRA_GRANTS_HAS_ALARM_LOG_INFO, false);
            boolean hasMalfunction = sp.getBoolean(EXTRA_GRANTS_HAS_MALFUNCTION_INFO, false);
            boolean hasDeviceBrief = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_BRIEF, false);
            boolean hasDeviceSignalCheck = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_SIGNAL_CHECK, false);
            boolean hasDeviceSignalConfig = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_SIGNAL_CONFIG, false);
            boolean hasBadSignalUpload = sp.getBoolean(EXTRA_GRANTS_HAS_BAD_SIGNAL_UPLOAD, false);
            final EventLoginData eventLoginData = new EventLoginData();
            eventLoginData.phoneId = phoneId;
            eventLoginData.userId = userId;
            eventLoginData.userName = userName;
            eventLoginData.phone = phone;
            eventLoginData.roles = roles;
            eventLoginData.hasSubMerchant = hasSubMerchant;
            eventLoginData.hasMerchantChange = hasMerchantChange;
            eventLoginData.isSupperAccount = isSupperAccount;
            eventLoginData.hasStation = hasStation;
            eventLoginData.hasContract = hasContract;
            eventLoginData.hasScanLogin = hasScanLogin;
            eventLoginData.hasInspectionTaskList = hasInspectionTaskList;
            eventLoginData.hasInspectionTaskModify = hasInspectionTaskModify;
            eventLoginData.hasInspectionDeviceList = hasInspectionDeviceList;
            eventLoginData.hasInspectionDeviceModify = hasInspectionDeviceModify;
            eventLoginData.hasAlarmInfo = hasAlarmInfo;
            eventLoginData.hasMalfunction = hasMalfunction;
            eventLoginData.hasDeviceBrief = hasDeviceBrief;
            eventLoginData.hasSignalCheck = hasDeviceSignalCheck;
            eventLoginData.hasSignalConfig = hasDeviceSignalConfig;
            eventLoginData.hasBadSignalUpload = hasBadSignalUpload;
            mEventLoginData = eventLoginData;
        }
        return mEventLoginData;
    }

    /**
     * 保存账户名称
     *
     * @param username
     * @param pwd
     */
    public void saveLoginNamePwd(String username, String pwd) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_NAME, username);
        String aes_pwd = AESUtil.encode(pwd);
        editor.putString(PREFERENCE_KEY_PASSWORD, aes_pwd);
        editor.apply();
    }

    public Map<String, String> getLoginNamePwd() {
        HashMap<String, String> map = new HashMap<>();
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        String name = sp.getString(PREFERENCE_KEY_NAME, null);
        String pwd = sp.getString(PREFERENCE_KEY_PASSWORD, null);
        if (!TextUtils.isEmpty(name)) {
            map.put(PREFERENCE_KEY_NAME, name);
        }
        if (!TextUtils.isEmpty(pwd)) {
            String aes_pwd = AESUtil.decode(pwd);
            map.put(PREFERENCE_KEY_PASSWORD, aes_pwd);
        }
        return map;
    }

    public int getBaseUrlType() {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SCOPE, Context
                .MODE_PRIVATE);
        return sp.getInt(PREFERENCE_KEY_URL, 0);
    }

    public void saveBaseUrlType(int urlType) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SCOPE, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREFERENCE_KEY_URL, urlType);
        editor.apply();
    }

    public void saveSessionId(String sessionId) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_SESSION_ID, sessionId);
        editor.apply();
    }

    public String getSessionId() {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                .MODE_PRIVATE);
        return sp.getString(PREFERENCE_KEY_SESSION_ID, null);

    }

    public void clearLoginDataSessionId() {
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                .MODE_PRIVATE).edit().clear().apply();
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SPLASH_LOGIN_DATA, Context
                .MODE_PRIVATE).edit().clear().apply();
    }

    public void saveDeployNameAddressHistory(String history) {
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, history).apply();
    }

    public String getDeployNameAddressHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, null);
    }

    public void saveDeployTagsHistory(String hisory) {
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_TAG, hisory).apply();
    }

    public String getDeployTagsHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_TAG, null);
    }

    public DeviceMergeTypesInfo getLocalDevicesMergeTypes() {
        if (mDeviceMergeTypesInfo == null) {
            String json = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOCAL_DEVICES_MERGETYPES, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_LOCAL_DEVICES_MERGETYPES, null);
            if (!TextUtils.isEmpty(json)) {
                mDeviceMergeTypesInfo = RetrofitServiceHelper.INSTANCE.getGson().fromJson(json, DeviceMergeTypesInfo.class);
            }
        }
//        if (mDeviceMergeTypesInfo != null) {
//            //加入全部的类型数据
//            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = mDeviceMergeTypesInfo.getConfig();
//            Map<String, DeviceTypeStyles> deviceType = config.getDeviceType();
//            if (!deviceType.containsKey("all")) {
//                DeviceTypeStyles deviceTypeStyles = new DeviceTypeStyles();
//                deviceTypeStyles.setMergeType("all");
//                deviceType.put("all", deviceTypeStyles);
//            }
//            Map<String, MergeTypeStyles> mergeType = config.getMergeType();
//            if (!mergeType.containsKey("all")) {
//                MergeTypeStyles mergeTypeStyles = new MergeTypeStyles();
//                mergeTypeStyles.setName("全部");
//                mergeTypeStyles.setResId(R.drawable.type_all);
//                mergeType.put("all", mergeTypeStyles);
//            }
//            Map<String, SensorTypeStyles> sensorType = config.getSensorType();
//            if (!sensorType.containsKey("all")) {
//                sensorType.put("all", new SensorTypeStyles());
//            }
//        }
        return mDeviceMergeTypesInfo;
    }

    public void saveLocalDevicesMergeTypes(DeviceMergeTypesInfo deviceMergeTypesInfo) {
        if (deviceMergeTypesInfo != null) {
            mDeviceMergeTypesInfo = deviceMergeTypesInfo;
            String json = RetrofitServiceHelper.INSTANCE.getGson().toJson(mDeviceMergeTypesInfo);
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOCAL_DEVICES_MERGETYPES, Context
                    .MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREFERENCE_KEY_LOCAL_DEVICES_MERGETYPES, json);
            editor.apply();
        }

    }

    public boolean saveSearchHistoryText(String text, int type) {
        String spFileName = getSearchHistoryFileName(type);
        if (TextUtils.isEmpty(spFileName)||TextUtils.isEmpty(text)) {
            return false;
        }
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(spFileName, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String oldText = sp.getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
        //
        List<String> oldHistoryList = new ArrayList<String>();
        if (!TextUtils.isEmpty(oldText)) {
            oldHistoryList.addAll(Arrays.asList(oldText.split(",")));
        }
        oldHistoryList.remove(text);
        oldHistoryList.add(0, text);
        ArrayList<String> tempList = new ArrayList<>();
        for (String str : oldHistoryList) {
            if (tempList.size() < 20) {
                tempList.add(str);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tempList.size(); i++) {
            if (i == (tempList.size() - 1)) {
                stringBuilder.append(tempList.get(i));
            } else {
                stringBuilder.append(tempList.get(i)).append(",");
            }
        }
        edit.putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, stringBuilder.toString());
        edit.apply();
        return true;

    }

    private String getSearchHistoryFileName(int type) {
        String spFileName = null;
        switch (type) {
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                spFileName = SearchHistoryTypeConstants.SP_FILE_WARN;
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                spFileName = SearchHistoryTypeConstants.SP_FILE_MALFUNCTION;
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                spFileName = SearchHistoryTypeConstants.SP_FILE_INSPECTION;
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                spFileName = SearchHistoryTypeConstants.SP_FILE_CONTRACT;
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                spFileName = SearchHistoryTypeConstants.SP_FILE_MERCHANT;
                break;
        }
        return spFileName;
    }

    public boolean clearSearchHistory(int type){
        String spFileName = getSearchHistoryFileName(type);
        if (TextUtils.isEmpty(spFileName)) {
            return false;
        }

        SharedPreferences.Editor editor =  SensoroCityApplication.getInstance().getSharedPreferences(spFileName, Context
                .MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        return true;

    }

    public List<String> getSearchHistoryData(int type) {
        String spFileName = getSearchHistoryFileName(type);
        if (TextUtils.isEmpty(spFileName)) {
            return null;
        }

        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(spFileName, Context
                .MODE_PRIVATE);
        String oldText = sp.getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
        //
        List<String> oldHistoryList = new ArrayList<String>();
        if (!TextUtils.isEmpty(oldText)) {
            oldHistoryList.addAll(Arrays.asList(oldText.split(",")));
        }
        return oldHistoryList;
    }
}
