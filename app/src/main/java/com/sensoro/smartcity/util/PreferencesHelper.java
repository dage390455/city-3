package com.sensoro.smartcity.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.EventLoginData;

/**
 * Created by sensoro on 17/7/4.
 */

public class PreferencesHelper implements Constants {

    private volatile static PreferencesHelper instance;

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
//                        String loginDataJson = RetrofitServiceHelper.INSTANCE.getGson().toJson(eventLoginData);
//                        EventLoginData eventLoginData1 = RetrofitServiceHelper.INSTANCE.getGson().fromJson(loginDataJson, EventLoginData.class);

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

    public void saveLoginNamePwd(String username, String pwd) {
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_NAME_PWD, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_NAME, username);
        editor.putString(PREFERENCE_KEY_PASSWORD, pwd);
        editor.apply();
    }
}
