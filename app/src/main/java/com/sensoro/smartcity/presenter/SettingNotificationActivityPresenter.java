package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.imainviews.ISettingNotificationActivityView;

public class SettingNotificationActivityPresenter extends BasePresenter<ISettingNotificationActivityView> {
    private Activity mAcitivity;

    @Override
    public void initData(Context context) {
        mAcitivity = (Activity) context;
        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences(SearchHistoryTypeConstants.SP_FILE_SETTINGNOTIFICATION_NAME, Context
                .MODE_PRIVATE);
        String oldText = sp.getString(SearchHistoryTypeConstants.SEARCH_HISTORY_KEY, null);


        if (!TextUtils.isEmpty(oldText)) {
            String[] split = oldText.split(",");

            if (!TextUtils.isEmpty(split[0])) {
                boolean b = Boolean.parseBoolean(split[0]);
                getView().setDeviceInChecked(b);
            }
            if (!TextUtils.isEmpty(split[1])) {
                boolean b = Boolean.parseBoolean(split[1]);
                getView().setDeviceOutChecked(b);
            }
            if (!TextUtils.isEmpty(split[2])) {
                getView().setDeviceInEditContent(split[2]);
            }
            if (!TextUtils.isEmpty(split[3])) {
                getView().setDeviceOutEditContent(split[3]);
            }
        }

    }

    @Override
    public void onDestroy() {

    }

    public void doSave(boolean deviceOut, boolean deviceIn, String deviceOutText, String deviceInText) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(deviceOut).append(",").append(deviceIn).append(",").append(deviceInText).append(",").append(deviceOutText);
        PreferencesSaveAnalyzer.savePreferences(SearchHistoryTypeConstants.TYPE_SETTINGNOTIFICATION, stringBuffer.toString());
        getView().finishAc();
    }
}
