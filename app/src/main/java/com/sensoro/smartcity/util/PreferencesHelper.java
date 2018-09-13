package com.sensoro.smartcity.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.EventLoginData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sensoro on 17/7/4.
 */

public class PreferencesHelper implements Constants {

    private volatile static PreferencesHelper instance;

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
        //
        editor.apply();
    }

    public EventLoginData getUserData() {
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
        //
        final EventLoginData eventLoginData = new EventLoginData();
        eventLoginData.phoneId = phoneId;
        eventLoginData.userId = userId;
        eventLoginData.userName = userName;
        eventLoginData.phone = phone;
        eventLoginData.roles = roles;
        eventLoginData.isSupperAccount = isSupperAccount;
        eventLoginData.hasStation = hasStation;
        eventLoginData.hasContract = hasContract;
        eventLoginData.hasScanLogin = hasScanLogin;
        return eventLoginData;
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
}
