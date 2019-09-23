package com.sensoro.common.analyzer;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;

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

    public static void savePreferences(int status, String content) {
        switch (status) {
            case SearchHistoryTypeConstants.TYPE_SETTINGNOTIFICATION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SETTINGNOTIFICATION_NAME, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_NAMEPLATE_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_NAMEPLATELIST_NAME, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_CAMERA_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SEARCH_CAMERA_LIST, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_BASESTATION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_BASESTATION, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;


            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_WARN, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MALFUNCTION, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_INSPECTION, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_CONTRACT, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MERCHANT, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS:
                PreferencesHelper.getInstance().saveDeployNameAddressHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM:
                PreferencesHelper.getInstance().saveDeployWeChatRelationHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_FOREST_CAMERA_INSTALL_POSITION:
                PreferencesHelper.getInstance().saveDeployForestCameraInstallPositionHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME:
                PreferencesHelper.getInstance().saveDeployAlarmContactNameHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE:
                PreferencesHelper.getInstance().saveDeployAlarmContactPhoneHistory(content);
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAMEPLATE_NAME:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_NAMEPLATE_NAME, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_NAMEPLATE_ADD_FROM_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_NAMEPLATE_ADD_FROM_LIST, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;


            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_SECURITY_WARN:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SECURITY_WARN, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_FOREST_FIRE_CAMERA_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_FOREST_FIRE_CAMERA_LIST, Context
                        .MODE_PRIVATE).edit().putString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, content).apply();
                break;

        }
    }

    private static String obtainOldText(int status) {
        String oldText = null;
        switch (status) {
            case SearchHistoryTypeConstants.TYPE_SETTINGNOTIFICATION:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SETTINGNOTIFICATION_NAME, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_NAMEPLATE_LIST:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_NAMEPLATELIST_NAME, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_CAMERA_LIST:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SEARCH_CAMERA_LIST, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_BASESTATION:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_BASESTATION, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_WARN, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MALFUNCTION, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_INSPECTION, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_CONTRACT, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MERCHANT, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS:
                oldText = PreferencesHelper.getInstance().getDeployNameAddressHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM:
                oldText = PreferencesHelper.getInstance().getDeployWeChatRelationHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_FOREST_CAMERA_INSTALL_POSITION:
                oldText = PreferencesHelper.getInstance().getDeployForestCameraInstallPositionHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME:
                oldText = PreferencesHelper.getInstance().getDeployAlarmContactNameHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE:
                oldText = PreferencesHelper.getInstance().getDeployAlarmContactPhoneHistory();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAMEPLATE_NAME:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_NAMEPLATE_NAME, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_NAMEPLATE_ADD_FROM_LIST:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_NAMEPLATE_ADD_FROM_LIST, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_SECURITY_WARN:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SECURITY_WARN, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_FOREST_FIRE_CAMERA_LIST:
                oldText = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_FOREST_FIRE_CAMERA_LIST, Context
                        .MODE_PRIVATE).getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, "");
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
            case SearchHistoryTypeConstants.TYPE_SEARCH_NAMEPLATE_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_NAMEPLATELIST_NAME, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_BASESTATION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_BASESTATION, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_CAMERA_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SEARCH_CAMERA_LIST, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_WARN, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MALFUNCTION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MALFUNCTION, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_INSPECTION:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_INSPECTION, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_CONTRACT, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_MERCHANT, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_RECORD:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_RECORD, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS:
                ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_NAME_ADDRESS, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME:
                ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_NAME, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM:
                ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_WE_CHAT_RELATION, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_FOREST_CAMERA_INSTALL_POSITION:
                ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_FOREST_CAMERA_INSTALL_POSITION, "").apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_TAG:
                ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_TAG, "").apply();
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE:
                ContextUtils.getContext().getSharedPreferences(Constants.PREFERENCE_DEPLOY_HISTORY, Activity.MODE_PRIVATE)
                        .edit().putString(Constants.PREFERENCE_KEY_DEPLOY_ALARM_CONTACT_PHONE, "").apply();
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAMEPLATE_NAME:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_DEPLOY_NAMEPLATE_NAME, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;
            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_NAMEPLATE_ADD_FROM_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_NAMEPLATE_ADD_FROM_LIST, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_SECURITY_WARN:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SECURITY_WARN, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;

            case SearchHistoryTypeConstants.TYPE_SEARCH_FOREST_FIRE_CAMERA_LIST:
                ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_FOREST_FIRE_CAMERA_LIST, Activity.MODE_PRIVATE)
                        .edit().clear().apply();
                break;


        }
    }
}
