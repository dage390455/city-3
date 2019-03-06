package com.sensoro.smartcity.analyzer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 标签存储封装类
 */
public class PreferencesSaveAnalyzer {

    /**
     * 存储标签
     *
     * @param status 0 部署名称地址 1 部署预警联系人 2 部署小程序账号
     * @param text
     * @return
     */
    public static List<String> handleDeployRecord(int status, String text) {
        String oldText = obtainOldText(status);

        if (!TextUtils.isEmpty(text)) {
            if (TextUtils.isEmpty(oldText)) {
                savePreferences(status, text);
                ArrayList<String> list = new ArrayList<>();
                list.add(text);
                return list;
            } else if (oldText.contains(text)) {
                ArrayList<String> list = new ArrayList<>();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        list.add(o);
                    }
                }
                list.add(0, text);
                StringBuilder stringBuilder = new StringBuilder();
                int size;
                if (list.size() > 20) {
                    size = 20;
                } else {
                    size = list.size();
                }
                for (int i = 0; i < size; i++) {
                    if (i == (size - 1)) {
                        stringBuilder.append(list.get(i));
                    } else {
                        stringBuilder.append(list.get(i)).append(",");
                    }
                }
                savePreferences(status, stringBuilder.toString());
                return list;
            } else {
                String format = String.format(Locale.ROOT, "%s,%s", text, oldText);
                String[] split = format.split(",");
                StringBuilder sb = new StringBuilder();
                ArrayList<String> list = new ArrayList<>();
                int total;
                if (split.length > 20) {
                    total = 20;
                } else {
                    total = split.length;
                }
                for (int i = 0; i < total; i++) {
                    if (i == total - 1) {
                        sb.append(split[i]);
                    } else {
                        sb.append(split[i]).append(",");
                    }
                    list.add(split[i]);

                }
                savePreferences(status,sb.toString());
                return list;
            }
        }
        return new ArrayList<String>();
    }

    private static void savePreferences(int status, String content) {
        switch (status) {
            case 0:
                PreferencesHelper.getInstance().saveDeployNameAddressHistory(content);
                break;
            case 1:
                PreferencesHelper.getInstance().saveDeployAlarmContactHistory(content);
                break;
            case 2:
                PreferencesHelper.getInstance().saveDeployWeChatRelationHistory(content);
                break;

        }
    }

    private static String obtainOldText(int status) {
        String oldText = null;
        switch (status) {
            case 0:
                oldText = PreferencesHelper.getInstance().getDeployNameAddressHistory();
                break;
            case 1:
                oldText = PreferencesHelper.getInstance().getDeployAlarmContactHistory();
                break;
            case 2:
                oldText = PreferencesHelper.getInstance().getDeployWeChatRelationHistory();
                break;

        }
        return oldText;
    }

    /**
     * 清空历史记录
     * @param status 0 部署名称地址 1 部署预警联系人 2 部署小程序账号 3 部署标签
     */
    public static void clearAllData(int status) {
        SharedPreferences sharedPreferences = SensoroCityApplication.getInstance().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE);
        switch (status) {
            case 0:
                sharedPreferences.edit().putString(Constants.PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, "").apply();
                break;
            case 1:
                sharedPreferences.edit().putString(Constants.PREFERENCE_KEY_DEPLOY_ALARM_CONTACT, "").apply();
                break;
            case 2:
                sharedPreferences.edit().putString(Constants.PREFERENCE_KEY_DEPLOY_WE_CHAT_RELATION, "").apply();
                break;
            case 3:
                sharedPreferences.edit().putString(Constants.PREFERENCE_KEY_DEPLOY_TAG, "").apply();
                break;
        }
    }
}
