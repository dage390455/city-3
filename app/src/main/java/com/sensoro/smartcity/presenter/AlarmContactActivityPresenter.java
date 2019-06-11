package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IAlarmContactActivityView;
import com.sensoro.common.model.DeployContactModel;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmContactActivityPresenter extends BasePresenter<IAlarmContactActivityView>  {
    private Activity mContext;
    private final List<DeployContactModel> deployContactModelList = new ArrayList<>();
    private ArrayList<String> mNameKeywords = new ArrayList<>();
    private ArrayList<String> mPhoneKeywords = new ArrayList<>();
    private int mStatus = -1;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        ArrayList<DeployContactModel> deployContactModels = (ArrayList<DeployContactModel>) mContext.getIntent().getSerializableExtra(Constants.EXTRA_SETTING_DEPLOY_CONTACT);
        if (deployContactModels != null && deployContactModels.size() > 0) {
            deployContactModelList.clear();
            deployContactModelList.addAll(deployContactModels);
            getView().updateContactData(deployContactModels);
        } else {
            deployContactModels = new ArrayList<>();
            DeployContactModel deployContactModel = new DeployContactModel();
            deployContactModels.add(deployContactModel);
        }
        getView().updateContactData(deployContactModels);
        getView().updateHistoryData(mNameKeywords);
    }

    @Override
    public void onDestroy() {
        mNameKeywords.clear();
        mPhoneKeywords.clear();
    }

    public void doFinish(List<DeployContactModel> mList) {
        deployContactModelList.clear();
        //检查规则
        for (DeployContactModel model : mList) {
            String name = model.name;
            String phone = model.phone;
            if (TextUtils.isEmpty(name)) {
                if (isAttachedView()) {
                    getView().toastShort(mContext.getString(R.string.Contact_name_cannot_be_empty));
                }
                return;
            } else {
                if (name.getBytes().length > 36) {
                    if (isAttachedView()) {
                        getView().toastShort(mContext.getString(R.string.contact_name_length_to_long));
                    }
                    return;
                }
            }
            if (!RegexUtils.checkPhone(phone)) {
                if (isAttachedView()) {
                    getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
                }
                return;
            }
        }
        deployContactModelList.addAll(mList);
        //保存数据
        for (DeployContactModel deployContactModel : deployContactModelList) {
            save(deployContactModel.name, deployContactModel.phone);
        }
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_SETTING_CONTACT;
        eventData.data = deployContactModelList;
        EventBus.getDefault().post(eventData);
        if (isAttachedView()) {
            getView().finishAc();
        }
    }


    private void save(String name, String phone) {
        List<String> nameList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME, name);
        List<String> phoneList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE, phone);
    }

    public void clearTag() {
        if (mStatus != -1) {
            switch (mStatus) {
                case 0:
                    PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_NAME);
                    break;
                case 1:
                    PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_ALARM_CONTRACT_PHONE);
                    break;
            }
        }

        mNameKeywords.clear();
        mPhoneKeywords.clear();
        if (isAttachedView()) {
            getView().updateHistoryData(mPhoneKeywords);
        }
    }

    /**
     * 判断应该展示姓名还是电话的历史记录
     *
     * @param status 0 姓名 1 电话
     */
    public void updateStatus(int status) {
        //TODO 这里频繁读取sp 可能会造成卡顿 要改
        mStatus = status;
        switch (status) {
            case 0:
                if (mNameKeywords.size() == 0) {
                    String name = PreferencesHelper.getInstance().getDeployAlarmContactNameHistory();
                    if (!TextUtils.isEmpty(name)) {
                        mNameKeywords.addAll(Arrays.asList(name.split(",")));
                    }
                }
                getView().updateHistoryData(mNameKeywords);
                break;
            case 1:
                if (mPhoneKeywords.size() == 0) {

                    String phone = PreferencesHelper.getInstance().getDeployAlarmContactPhoneHistory();
                    if (!TextUtils.isEmpty(phone)) {
                        mPhoneKeywords.addAll(Arrays.asList(phone.split(",")));
                    }
                }
                getView().updateHistoryData(mPhoneKeywords);
                break;
        }
    }
    //    public void checkCanSave(String name, String phone) {
//        boolean isEnable = !TextUtils.isEmpty(name) && name.getBytes().length < 36 && RegexUtils.checkPhone(phone);
//        if (isAttachedView()) {
//            getView().updateSaveStatus(isEnable);
//        }
//
//    }

//    public void doFinish() {
//
//    }
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


//    public void updateText(String content) {
//        if (mStatus != -1) {
//            switch (mStatus) {
//                case 0:
//                    if (isAttachedView()) {
////                        getView().setName(content);
//                    }
//
//                    break;
//                case 1:
//                    if (isAttachedView()) {
////                        getView().setPhone(content);
//                    }
//                    break;
//            }
//        }
//    }
}
