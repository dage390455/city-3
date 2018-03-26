package com.sensoro.smartcity.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.sensoro.smartcity.constant.Constants;

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


    public void saveLoginData(Context context,  String username, String pwd) {
        String aes_pwd = AESUtil.encode(pwd);
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_NAME, username);
        editor.putString(PREFERENCE_KEY_PASSWORD, aes_pwd);

        editor.commit();
    }


}
