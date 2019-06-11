package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.R;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorWeChatRelationActivityView;
import com.sensoro.smartcity.util.RegexUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeployMonitorWeChatRelationActivityPresenter extends BasePresenter<IDeployMonitorWeChatRelationActivityView> {
    private Activity mContext;
    private final List<String> mHistoryKeywords = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

//        String sn = mContext.getIntent().getStringExtra(EXTRA_DEPLOY_TO_SN);
//        if (!TextUtils.isEmpty(sn)) {
//            getView().updateTvTitle(sn);
//        }
        String account = mContext.getIntent().getStringExtra(Constants.EXTRA_SETTING_WE_CHAT_RELATION);
        String history = PreferencesHelper.getInstance().getDeployWeChatRelationHistory();
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        getView().updateSearchHistoryData(mHistoryKeywords);
        if (!TextUtils.isEmpty(account) && !account.equals(mContext.getResources().getString(R.string
                .tips_hint_we_chat_relation_set))) {
            getView().setEditText(account);
        } else {
            getView().setEditText("");
        }
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords.clear();
    }

    private void save(String text) {
        List<String> list = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM, text);
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(list);
//        String oldText = PreferencesHelper.getInstance().getDeployWeChatRelationHistory();
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
//                PreferencesHelper.getInstance().saveDeployWeChatRelationHistory(stringBuilder.toString());
//            } else {
//                if (TextUtils.isEmpty(oldText)) {
//                    PreferencesHelper.getInstance().saveDeployWeChatRelationHistory(text);
//                } else {
//                    PreferencesHelper.getInstance().saveDeployWeChatRelationHistory(text + "," + oldText);
//                }
//                mHistoryKeywords.add(0, text);
//            }
//        }
    }

    public void doChoose(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (!RegexUtils.checkPhone(text)) {
                getView().toastShort(mContext.getString(R.string.please_enter_a_valid_mobile_number));
                return;
            }
            save(text);
        }
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_SETTING_WE_CHAT_RELATION;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void checkCanSave(String phone) {
        boolean isEnable = !TextUtils.isEmpty(phone) && RegexUtils.checkPhone(phone);
        getView().updateSaveStatus(isEnable);

    }

    public void clearHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_MINI_PROGRAM);
        mHistoryKeywords.clear();
        if (isAttachedView()) {
            getView().updateSearchHistoryData(mHistoryKeywords);
        }

    }
}
