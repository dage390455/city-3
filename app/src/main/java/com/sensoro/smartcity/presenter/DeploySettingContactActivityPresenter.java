package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeploySettingContactActivityView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DeploySettingContactActivityPresenter extends BasePresenter<IDeploySettingContactActivityView>
        implements Constants {
    private SharedPreferences mNamePref;
    private SharedPreferences mPhonePref;
    private SharedPreferences.Editor mNameEditor;
    private SharedPreferences.Editor mPhoneEditor;

    public List<String> getNameHistoryKeywords() {
        return mNameHistoryKeywords;
    }

    private List<String> mNameHistoryKeywords = new ArrayList<>();

    public List<String> getPhoneHistoryKeywords() {
        return mPhoneHistoryKeywords;
    }

    private List<String> mPhoneHistoryKeywords = new ArrayList<>();
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
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mNameHistoryKeywords.size(); i++) {
                    if (i == (mNameHistoryKeywords.size() - 1)) {
                        stringBuffer.append(mNameHistoryKeywords.get(i));
                    } else {
                        stringBuffer.append(mNameHistoryKeywords.get(i) + ",");
                    }
                }
                mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, stringBuffer.toString());
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
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mPhoneHistoryKeywords.size(); i++) {
                    if (i == (mPhoneHistoryKeywords.size() - 1)) {
                        stringBuffer.append(mPhoneHistoryKeywords.get(i));
                    } else {
                        stringBuffer.append(mPhoneHistoryKeywords.get(i) + ",");
                    }
                }
                mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, stringBuffer.toString());
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
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,5-9])|(177)|(171)|(176))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        if (TextUtils.isEmpty(name)) {
            getView().toastShort("联系人姓名不能为空！");
            return;
        }
        if (!TextUtils.isEmpty(phone) && p.matcher(phone).matches()) {
            saveName(name);
            savePhone(phone);
//            mNameEt.clearFocus();
//            mPhoneEt.clearFocus();
            getView().updateAdapter();
            Intent data = new Intent();
            data.putExtra(EXTRA_SETTING_CONTACT, name.trim());
            data.putExtra(EXTRA_SETTING_CONTENT, phone.trim());
            getView().setIntentResult(RESULT_CODE_SETTING_CONTACT, data);
            getView().finishAc();
        } else {
            getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
        }
    }
}
