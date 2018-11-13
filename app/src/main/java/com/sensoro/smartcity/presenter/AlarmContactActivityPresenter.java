package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmContactActivityView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AlarmContactActivityPresenter extends BasePresenter<IAlarmContactActivityView> implements Constants {
    private Activity mContext;
    private final List<DeployContactModel> deployContactModelList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        ArrayList<DeployContactModel> deployContactModels = (ArrayList<DeployContactModel>) mContext.getIntent().getSerializableExtra(EXTRA_SETTING_DEPLOY_CONTACT);
        if (deployContactModels != null && deployContactModels.size() > 0) {
            deployContactModelList.clear();
            deployContactModelList.addAll(deployContactModels);
            DeployContactModel deployContactModel = deployContactModelList.get(0);
            getView().setNameAndPhone(deployContactModel.name, deployContactModel.phone);
        }
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
            getView().toastShort(mContext.getString(R.string.Contact_name_cannot_be_empty));
            return;
        }
        if (RegexUtils.checkPhone(phone)) {
//            saveName(name);
//            savePhone(phone);
//            mNameEt.clearFocus();
//            mPhoneEt.clearFocus();
//            getView().updateAdapter(mNameHistoryKeywords, mPhoneHistoryKeywords);
            deployContactModelList.clear();
            DeployContactModel deployContactModel = new DeployContactModel();
            deployContactModel.name = name;
            deployContactModel.phone = phone;
            deployContactModelList.add(deployContactModel);
            //
            EventData eventData = new EventData();
            eventData.code = EVENT_DATA_DEPLOY_SETTING_CONTACT;
            eventData.data = deployContactModelList;
            EventBus.getDefault().post(eventData);
            getView().finishAc();
        } else {
            getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
        }
    }
}
