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
import com.sensoro.smartcity.server.bean.DeployPicInfo;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MalfunctionTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sensoro on 17/7/4.
 */

public final class PreferencesHelper implements Constants {

    private volatile EventLoginData mEventLoginData;
    private volatile DeviceMergeTypesInfo mDeviceMergeTypesInfo;

    private PreferencesHelper() {
    }

    public static PreferencesHelper getInstance() {
        return PreferencesHelperHolder.instance;
    }

    private static class PreferencesHelperHolder {
        private static final PreferencesHelper instance = new PreferencesHelper();
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
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_POSITION_CALIBRATION, eventLoginData.hasDevicePositionCalibration);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_MUTE_SHORT, eventLoginData.hasDeviceMuteShort);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_MUTE_LONG, eventLoginData.hasDeviceMuteLong);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_FIRMWARE_UPDATE, eventLoginData.hasDeviceFirmwareUpdate);
        editor.putBoolean(EXTRA_GRANTS_HAS_DEVICE_DEMO_MODE, eventLoginData.hasDeviceDemoMode);
        editor.putBoolean(EXTRA_GRANTS_HAS_CONTROLLER_AID, eventLoginData.hasControllerAid);
        //
        editor.apply();
    }

    public EventLoginData getUserData() {
        if (mEventLoginData == null) {
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SPLASH_LOGIN_DATA, Context
                    .MODE_PRIVATE);
            String phoneId = sp.getString(EXTRA_PHONE_ID, null);
            String userId = sp.getString(EXTRA_USER_ID, null);
            try {
                LogUtils.loge(this, "phoneId = " + phoneId + ",userId = " + userId);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
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
            boolean hasDevicePositionCalibration = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_POSITION_CALIBRATION, false);
            boolean hasDeviceMuteShort = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_MUTE_SHORT, false);
            boolean hasDeviceMuteLong = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_MUTE_LONG, false);
            boolean hasDeviceFirmUpdate = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_FIRMWARE_UPDATE, false);
            boolean hasDeviceDemoMode = sp.getBoolean(EXTRA_GRANTS_HAS_DEVICE_DEMO_MODE, false);
            boolean hasControllerAid = sp.getBoolean(EXTRA_GRANTS_HAS_CONTROLLER_AID, false);
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
            eventLoginData.hasDevicePositionCalibration = hasDevicePositionCalibration;
            eventLoginData.hasDeviceMuteShort = hasDeviceMuteShort;
            eventLoginData.hasDeviceMuteLong = hasDeviceMuteLong;
            eventLoginData.hasDeviceFirmwareUpdate = hasDeviceFirmUpdate;
            eventLoginData.hasDeviceDemoMode = hasDeviceDemoMode;
            eventLoginData.hasControllerAid = hasControllerAid;
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
    public boolean saveLoginNamePwd(String username, String pwd) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            return false;
        }
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_NAME, username);
        String aes_pwd = AESUtil.encode(pwd);
        editor.putString(PREFERENCE_KEY_PASSWORD, aes_pwd);
        editor.apply();
        return true;
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

    public boolean saveSessionId(String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            return false;
        }
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_SESSION_ID, sessionId);
        editor.apply();
        return true;
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
        this.mEventLoginData = null;
    }

    public boolean saveDeployNameAddressHistory(String history) {
        if (TextUtils.isEmpty(history)) {
            return false;
        }
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, history).apply();
        return true;
    }

    public boolean saveDeployWeChatRelationHistory(String history) {
        if (TextUtils.isEmpty(history)) {
            return false;
        }
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_WE_CHAT_RELATION, history).apply();
        return true;
    }

    public String getDeployNameAddressHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, null);
    }

    public String getDeployWeChatRelationHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_WE_CHAT_RELATION, null);
    }

    public boolean saveDeployTagsHistory(String history) {
        if (TextUtils.isEmpty(history)) {
            return false;
        }
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_TAG, history).apply();
        return true;
    }

    public String getDeployTagsHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_TAG, null);
    }

    public DeviceMergeTypesInfo getLocalDevicesMergeTypes() {
        try {
            if (mDeviceMergeTypesInfo == null) {
                String json = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOCAL_DEVICES_MERGETYPES, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_LOCAL_DEVICES_MERGETYPES, null);
                LogUtils.loge("DeviceMergeTypesInfo json : " + json);
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
        } catch (Throwable t) {
            return null;
        }
    }

    public boolean saveLocalDevicesMergeTypes(DeviceMergeTypesInfo deviceMergeTypesInfo) {
        if (deviceMergeTypesInfo == null) {
            return false;
        }
        mDeviceMergeTypesInfo = deviceMergeTypesInfo;
        String json = RetrofitServiceHelper.INSTANCE.getGson().toJson(mDeviceMergeTypesInfo);
        if (!TextUtils.isEmpty(json)) {
            try {
                LogUtils.loge("saveLocalDevicesMergeTypes length = " + json.length());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            try {
                LogUtils.loge("saveLocalDevicesMergeTypes :" + json);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOCAL_DEVICES_MERGETYPES, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_LOCAL_DEVICES_MERGETYPES, json);
        editor.apply();
        return true;
    }

    public boolean saveSearchHistoryText(String text, int type) {
        String spFileName = getSearchHistoryFileName(type);
        if (TextUtils.isEmpty(spFileName) || TextUtils.isEmpty(text)) {
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
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                spFileName = SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD;
                break;
        }
        return spFileName;
    }

    public boolean clearSearchHistory(int type) {
        String spFileName = getSearchHistoryFileName(type);
        if (TextUtils.isEmpty(spFileName)) {
            return false;
        }

        SharedPreferences.Editor editor = SensoroCityApplication.getInstance().getSharedPreferences(spFileName, Context
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

    /**
     * 获取配置字段 --故障主字段
     *
     * @param mainFunctionMainType
     * @return
     */
    public MalfunctionTypeStyles getConfigMalfunctionMainTypes(String mainFunctionMainType) {
        DeviceMergeTypesInfo localDevicesMergeTypes = getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                DeviceMergeTypesInfo.DeviceMergeTypeConfig.MalfunctionTypeBean malfunctionType = config.getMalfunctionType();
                if (malfunctionType != null) {
                    Map<String, MalfunctionTypeStyles> mainTypes = malfunctionType.getMainTypes();
                    if (mainTypes != null) {
                        return mainTypes.get(mainFunctionMainType);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取配置字段 --故障子字段
     *
     * @param mainFunctionSubType
     * @return
     */
    public MalfunctionTypeStyles getConfigMalfunctionSubTypes(String mainFunctionSubType) {
        DeviceMergeTypesInfo localDevicesMergeTypes = getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                DeviceMergeTypesInfo.DeviceMergeTypeConfig.MalfunctionTypeBean malfunctionType = config.getMalfunctionType();
                if (malfunctionType != null) {
                    Map<String, MalfunctionTypeStyles> subTypes = malfunctionType.getSubTypes();
                    if (subTypes != null) {
                        return subTypes.get(mainFunctionSubType);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取配置字段 --设备副类型
     *
     * @param deviceType
     * @return
     */
    public DeviceTypeStyles getConfigDeviceType(String deviceType) {
        DeviceMergeTypesInfo localDevicesMergeTypes = getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                Map<String, DeviceTypeStyles> deviceTypeStylesMap = config.getDeviceType();
                if (deviceTypeStylesMap != null) {
                    return deviceTypeStylesMap.get(deviceType);
                }
            }
        }
        return null;
    }

    /**
     * 获取部署照片中的配置字段
     *
     * @param deviceType
     * @return
     */
    public List<DeployPicInfo> getConfigDeviceDeployPic(String deviceType) {
        DeviceTypeStyles configDeviceType = getConfigDeviceType(deviceType);
        if (configDeviceType != null) {
            return configDeviceType.getDeployPicConfig();
        }
        return null;
    }

    /**
     * 获取配置字段 --设备主类型
     *
     * @param mergeType
     * @return
     */
    public MergeTypeStyles getConfigMergeType(String mergeType) {
        DeviceMergeTypesInfo localDevicesMergeTypes = getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                Map<String, MergeTypeStyles> mergeTypeStylesMap = config.getMergeType();
                if (mergeTypeStylesMap != null) {
                    return mergeTypeStylesMap.get(mergeType);
                }
            }
        }
        return null;
    }

    /**
     * 获取配置字段 --传感器类型
     *
     * @param sensorType
     * @return
     */
    public SensorTypeStyles getConfigSensorType(String sensorType) {
        DeviceMergeTypesInfo localDevicesMergeTypes = getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                Map<String, SensorTypeStyles> sensorTypeStylesMap = config.getSensorType();
                if (sensorTypeStylesMap != null) {
                    return sensorTypeStylesMap.get(sensorType);
                }
            }
        }
        return null;
    }

    /**
     * 存储示例照片今日不再提示的时间
     *
     * @param key
     */
    public void saveDeployExamplePicTimestamp(String key) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_EXAMPLE_PIC, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, DateUtil.getStrTime_yymmdd(System.currentTimeMillis()));
        editor.apply();
    }

    public String getDeployExamplePicTimestamp(String key) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_EXAMPLE_PIC, Context
                .MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public int getLocalDemoModeState(String sn) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEMO_MODE_JSON, Context
                .MODE_PRIVATE);
        String json = sp.getString(PREFERENCE_DEMO_MODE_JSON, null);
        if (!TextUtils.isEmpty(json)) {
            HashMap map = RetrofitServiceHelper.INSTANCE.getGson().fromJson(json, HashMap.class);
            Object value = map.get(sn);
            if (value instanceof Integer) {
                return (int) value;
            }
        }
        return 0;
    }

    public void saveLocalDemoModeState(String sn, int mode) {
        final HashMap<String, Integer> localDemoModeMap = new HashMap<>();
        localDemoModeMap.put(sn, mode);
        try {
            String json = RetrofitServiceHelper.INSTANCE.getGson().toJson(localDemoModeMap);
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEMO_MODE_JSON, Context
                    .MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREFERENCE_DEMO_MODE_JSON, json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取存储的版本号
     *
     * @return
     */
    public int getSaveVersionCode() {
        try {
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_KEY_VERSION_CODE, Context
                    .MODE_PRIVATE);
            return sp.getInt(PREFERENCE_KEY_VERSION_CODE, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 保存当前的版本号
     *
     * @param code
     */
    public void saveCurrentVersionCode(int code) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_KEY_VERSION_CODE, Context
                .MODE_PRIVATE);
        sp.edit().putInt(PREFERENCE_KEY_VERSION_CODE, code).apply();
    }

    public String getDeployAlarmContactNameHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_NAME, null);
    }

    public boolean saveDeployAlarmContactNameHistory(String history) {
        if (TextUtils.isEmpty(history)) {
            return false;
        }
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_NAME, history).apply();
        return true;
    }

    public String getDeployAlarmContactPhoneHistory() {
        return SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).getString(PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_PHONE, null);
    }

    public boolean saveDeployAlarmContactPhoneHistory(String history) {
        if (TextUtils.isEmpty(history)) {
            return false;
        }
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE).edit().putString(PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_PHONE, history).apply();
        return true;
    }
}
