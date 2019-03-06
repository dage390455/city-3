package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmContactActivityView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.RegexUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AlarmContactActivityPresenter extends BasePresenter<IAlarmContactActivityView> implements Constants {
    private Activity mContext;
    private final List<DeployContactModel> deployContactModelList = new ArrayList<>();
    private ArrayList<String> mHistoryKeywords = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        ArrayList<DeployContactModel> deployContactModels = (ArrayList<DeployContactModel>) mContext.getIntent().getSerializableExtra(EXTRA_SETTING_DEPLOY_CONTACT);
        if (deployContactModels != null && deployContactModels.size() > 0) {
            deployContactModelList.clear();
            deployContactModelList.addAll(deployContactModels);
            DeployContactModel deployContactModel = deployContactModelList.get(0);
            if (isAttachedView()) {
                getView().setNameAndPhone(deployContactModel.name, deployContactModel.phone);
            }

        }
        String history = PreferencesHelper.getInstance().getDeployAlarmContactHistory();
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        getView().updateHistoryData(mHistoryKeywords);
        //TODO 目前只支持一个联系人
    }

    @Override
    public void onDestroy() {

    }

    public void doFinish() {

    }
//    private void saveName(String text) {
//        String oldText = mNamePref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");
//        if (!TextUtils.isEmpty(text)) {
//            if (mNameHistoryKeywords.contains(text)) {
//                mNameHistoryKeywords.clear();
//                for (String o : oldText.split(",")) {
//                    if (!o.equalsIgnoreCase(text)) {
//                        mNameHistoryKeywords.add(o);
//                    }
//                }
//                mNameHistoryKeywords.add(0, text);
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int i = 0; i < mNameHistoryKeywords.size(); i++) {
//                    if (i == (mNameHistoryKeywords.size() - 1)) {
//                        stringBuilder.append(mNameHistoryKeywords.get(i));
//                    } else {
//                        stringBuilder.append(mNameHistoryKeywords.get(i)).append(",");
//                    }
//                }
//                mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, stringBuilder.toString());
//                mNameEditor.commit();
//            } else {
//                if (TextUtils.isEmpty(oldText)) {
//                    mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, text);
//                    mNameEditor.commit();
//                } else {
//                    mNameEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, text + "," + oldText);
//                    mNameEditor.commit();
//                }
//                mNameHistoryKeywords.add(0, text);
//            }
//        }
//    }

//    private void savePhone(String text) {
//
//        String oldText = mPhonePref.getString(PREFERENCE_KEY_DEPLOY_PHONE, "");
//        if (!TextUtils.isEmpty(text)) {
//            String[] split = oldText.split(",");
//            if (mPhoneHistoryKeywords.contains(text)) {
//                mPhoneHistoryKeywords.clear();
//                for (String o : split) {
//                    if (!o.equalsIgnoreCase(text)) {
//                        mPhoneHistoryKeywords.add(o);
//                    }
//                }
//                mPhoneHistoryKeywords.add(0, text);
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int i = 0; i < mPhoneHistoryKeywords.size(); i++) {
//                    if (i == (mPhoneHistoryKeywords.size() - 1)) {
//                        stringBuilder.append(mPhoneHistoryKeywords.get(i));
//                    } else {
//                        stringBuilder.append(mPhoneHistoryKeywords.get(i)).append(",");
//                    }
//                }
//                mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, stringBuilder.toString());
//                mPhoneEditor.commit();
//            } else {
//                if (TextUtils.isEmpty(oldText)) {
//                    mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, text);
//                    mPhoneEditor.commit();
//                } else {
//                    mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY_PHONE, text + "," + oldText);
//                    mPhoneEditor.commit();
//                }
//                mPhoneHistoryKeywords.add(0, text);
//            }
//        }
//    }

    public void doFinish(String name, String phone) {
        if (TextUtils.isEmpty(name)) {
            if (isAttachedView()) {
                getView().toastShort(mContext.getString(R.string.Contact_name_cannot_be_empty));
            }
            return;
        }else{
            if (name.getBytes().length > 36) {
                if (isAttachedView()) {
                    getView().toastShort(mContext.getString(R.string.contact_name_length_to_long));
                }
                return;
            }
        }
        if (RegexUtils.checkPhone(phone)) {
//            saveName(name);
//            savePhone(phone);
//            mNameEt.clearFocus();
//            mPhoneEt.clearFocus();
//            getView().updateAdapter(mNameHistoryKeywords, mPhoneHistoryKeywords);
            deployContactModelList.clear();
            //保存标签
            save(String.format(Locale.ROOT, "%s#%s", name, phone));
            DeployContactModel deployContactModel = new DeployContactModel();
            deployContactModel.name = name;
            deployContactModel.phone = phone;
            deployContactModelList.add(deployContactModel);
            //
            EventData eventData = new EventData();
            eventData.code = EVENT_DATA_DEPLOY_SETTING_CONTACT;
            eventData.data = deployContactModelList;
            EventBus.getDefault().post(eventData);
            if (isAttachedView()) {
                getView().finishAc();
            }
        } else {
            if (isAttachedView()) {
                getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
            }
        }
    }


    private void save(String text) {
        List<String> list = PreferencesSaveAnalyzer.handleDeployRecord(1, text);
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(list);
//        String oldText = PreferencesHelper.getInstance().getDeployAlarmContactHistory();
//        if (!TextUtils.isEmpty(text)) {
//            if (mHistoryKeywords.contains(text)) {
//                List<String> list = new ArrayList<>();
//                for (String o : oldText.split(",")) {
//                    if (!o.equalsIgnoreCase(text)) {
//                        list.add(o);
//                    }
//                }
//                list.add(0, text);
//                mHistoryKeywords.clear();
//                mHistoryKeywords.addAll(list);
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int i = 0; i < list.size(); i++) {
//                    if (i == (list.size() - 1)) {
//                        stringBuilder.append(list.get(i));
//                    } else {
//                        stringBuilder.append(list.get(i)).append(",");
//                    }
//                }
//                PreferencesHelper.getInstance().saveDeployAlarmContactHistory(stringBuilder.toString());
//            } else {
//                if (TextUtils.isEmpty(oldText)) {
//                    PreferencesHelper.getInstance().saveDeployAlarmContactHistory(text);
//                } else {
//                    PreferencesHelper.getInstance().saveDeployAlarmContactHistory(text + "," + oldText);
//                }
//                mHistoryKeywords.add(0, text);
//            }
//        }
    }

    public void checkCanSave(String name, String phone) {
        boolean isEnable = !TextUtils.isEmpty(name) && name.getBytes().length < 36 && RegexUtils.checkPhone(phone);
        if (isAttachedView()) {
            getView().updateSaveStatus(isEnable);
        }

    }

    public void clearTag() {
        PreferencesSaveAnalyzer.clearAllData(1);
        mHistoryKeywords.clear();
        if (isAttachedView()) {
            getView().updateHistoryData(mHistoryKeywords);
        }
    }
}
