package com.sensoro.smartcity.analyzer;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 标签存储封装类
 */
public class PreferencesSaveAnalyzer {

    /**
     * 存储标签
     *
     * @param status 0 部署名称地址 1 部署预警联系人姓名 2 部署小程序账号 4 部署预警联系人电话
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
                savePreferences(status, sb.toString());
                return list;
            }
        }
        return new ArrayList<String>();
    }

    private static void savePreferences(int status, String content) {
        switch (status) {

            case SearchHistoryTypeConstants.TYPE_SEARCH_CAMERALIST:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SEARCH_CAMERALIST, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_WARN, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MALFUNCTION, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_INSPECTION, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_CONTRACT, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MERCHANT, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS:
                PreferencesHelper.getInstance().saveDeployNameAddressHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM:
                PreferencesHelper.getInstance().saveDeployWeChatRelationHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME:
                PreferencesHelper.getInstance().saveDeployAlarmContactNameHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE:
                PreferencesHelper.getInstance().saveDeployAlarmContactPhoneHistory(content);
                break;

        }
    }

    private static String obtainOldText(int status) {
        String oldText = null;
        switch (status) {
            case SearchHistoryTypeConstants.TYPE_SEARCH_CAMERALIST:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SEARCH_CAMERALIST, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_WARN, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MALFUNCTION, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_INSPECTION, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_CONTRACT, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MERCHANT, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                oldText = SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS:
                oldText = PreferencesHelper.getInstance().getDeployNameAddressHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM:
                oldText = PreferencesHelper.getInstance().getDeployWeChatRelationHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME:
                oldText = PreferencesHelper.getInstance().getDeployAlarmContactNameHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE:
                oldText = PreferencesHelper.getInstance().getDeployAlarmContactPhoneHistory();
                break;


        }
        return oldText;
    }

    /**
     * 清空历史记录
     *
     * @param status
     */
    public static void clearAllData(int status) {
        switch (status) {

            case SearchHistoryTypeConstants.TYPE_SEARCH_CAMERALIST:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SEARCH_CAMERALIST, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_WARN, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MALFUNCTION, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_INSPECTION, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_CONTRACT, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MERCHANT, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                SensoroCityApplication.getInstance().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS:
                SensoroCityApplication.getInstance().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME:
                SensoroCityApplication.getInstance().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_NAME, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM:
                SensoroCityApplication.getInstance().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_WE_CHAT_RELATION, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_TAG:
                SensoroCityApplication.getInstance().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_TAG, "").apply();
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE:
                SensoroCityApplication.getInstance().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_PHONE, "").apply();
                break;

        }
    }
}
