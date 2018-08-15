package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeploySettingContactActivityView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeploySettingContactActivityPresenter extends BasePresenter<IDeploySettingContactActivityView>
        implements Constants {
    private SharedPreferences mNamePref;
    private SharedPreferences mPhonePref;
    private SharedPreferences.Editor mNameEditor;
    private SharedPreferences.Editor mPhoneEditor;
    //
    private final List<String> mPhoneHistoryKeywords = new ArrayList<>();

    private final List<String> mNameHistoryKeywords = new ArrayList<>();
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        try {
            mNamePref = mContext.getSharedPreferences
                    (PREFERENCE_DEPLOY_CONTACT_HISTORY, Activity.MODE_PRIVATE);
            mNameEditor = mNamePref.edit();
            String nameHistory = mNamePref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");

            mPhonePref = mContext.getSharedPreferences(PREFERENCE_DEPLOY_CONTENT_HISTORY, Activity.MODE_PRIVATE);
            mPhoneEditor = mPhonePref.edit();
            String phoneHistory = mPhonePref.getString(PREFERENCE_KEY_DEPLOY_PHONE, "");

            if (!TextUtils.isEmpty(nameHistory)) {
                mNameHistoryKeywords.addAll(Arrays.asList(nameHistory.split(",")));
            }
            if (!TextUtils.isEmpty(phoneHistory)) {
                mPhoneHistoryKeywords.addAll(Arrays.asList(phoneHistory.split(",")));
            }
            //默认按名字显示
            getView().setSearchHistoryLayoutVisible(mNameHistoryKeywords.size() > 0 || mPhoneHistoryKeywords.size() >
                    0);
            String contact = mContext.getIntent().getStringExtra(EXTRA_SETTING_CONTACT);
            String content = mContext.getIntent().getStringExtra(EXTRA_SETTING_CONTENT);
            if (!TextUtils.isEmpty(contact)) {
                getView().setName(contact);
            }
            if (!TextUtils.isEmpty(content)) {
                getView().setPhone(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getPhoneHistoryKeywords() {
        return mPhoneHistoryKeywords;
    }

    public List<String> getNameHistoryKeywords() {
        return mNameHistoryKeywords;
    }

    private void saveName(String text) {
        String oldText = mNamePref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");
        if (!TextUtils.isEmpty(text)) {
            if (mNameHistoryKeywords.contains(text)) {
                mNameHistoryKeywords.clear();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        mNameHistoryKeywords.add(o);
                    }
                }
                mNameHistoryKeywords.add(0, text);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < mNameHistoryKeywords.size(); i++) {
                    if (i == (mNameHistoryKeywords.size() - 1)) {
                        stringBuilder.append(mNameHistoryKeywords.get(i));
                    } else {
                        stringBuilder.append(mNameHistoryKeywords.get(i)).append(",");
                    }
                }
                mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, stringBuilder.toString());
                mNameEditor.commit();
            } else {
                if (TextUtils.isEmpty(oldText)) {
                    mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, text);
                    mNameEditor.commit();
                } else {
                    mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, text + "," + oldText);
                    mNameEditor.commit();
                }
                mNameHistoryKeywords.add(0, text);
            }
        }
    }

    private void savePhone(String text) {

        String oldText = mPhonePref.getString(PREFERENCE_KEY_DEPLOY_PHONE, "");
        if (!TextUtils.isEmpty(text)) {
            String[] split = oldText.split(",");
            if (mPhoneHistoryKeywords.contains(text)) {
                mPhoneHistoryKeywords.clear();
                for (String o : split) {
                    if (!o.equalsIgnoreCase(text)) {
                        mPhoneHistoryKeywords.add(o);
                    }
                }
                mPhoneHistoryKeywords.add(0, text);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < mPhoneHistoryKeywords.size(); i++) {
                    if (i == (mPhoneHistoryKeywords.size() - 1)) {
                        stringBuilder.append(mPhoneHistoryKeywords.get(i));
                    } else {
                        stringBuilder.append(mPhoneHistoryKeywords.get(i)).append(",");
                    }
                }
                mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, stringBuilder.toString());
                mPhoneEditor.commit();
            } else {
                if (TextUtils.isEmpty(oldText)) {
                    mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, text);
                    mPhoneEditor.commit();
                } else {
                    mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, text + "," + oldText);
                    mPhoneEditor.commit();
                }
                mPhoneHistoryKeywords.add(0, text);
            }
        }
    }

    public void doFinish(String name, String phone) {
//        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,5-9])|(177)|(171)|(176))\\d{8}$";
        if (TextUtils.isEmpty(name)) {
            getView().toastShort("联系人姓名不能为空！");
            return;
        }
        if (RegexUtils.checkPhone(phone)) {
            saveName(name);
            savePhone(phone);
//            mNameEt.clearFocus();
//            mPhoneEt.clearFocus();
            getView().updateAdapter();
            EventData eventData = new EventData();
            eventData.code = EVENT_DATA_DEPLOY_SETTING_CONTACT;
            DeployContactModel deployContactModel = new DeployContactModel();
            deployContactModel.name = name.trim();
            deployContactModel.phone = phone.trim();
            eventData.data = deployContactModel;
            EventBus.getDefault().post(eventData);
            getView().finishAc();
        } else {
            getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
        }
    }

    @Override
    public void onDestroy() {
        mPhoneHistoryKeywords.clear();
        mNameHistoryKeywords.clear();
    }
}
