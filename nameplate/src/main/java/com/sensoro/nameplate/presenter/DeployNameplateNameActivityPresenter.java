package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.IMainViews.IDeployNameplateNameActivityView;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DeployNameplateNameActivityPresenter extends BasePresenter<IDeployNameplateNameActivityView> implements Constants {
    private Activity mContext;
    private final List<String> mHistoryKeywords = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        String sn = mContext.getIntent().getStringExtra(Constants.EXTRA_DEPLOY_TO_SN);
//
        String name = mContext.getIntent().getStringExtra(Constants.EXTRA_SETTING_NAME_ADDRESS);
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAMEPLATE_NAME);
        if (list != null) {
            mHistoryKeywords.addAll(list);
            getView().updateSearchHistoryData(mHistoryKeywords);
        }


        if (!TextUtils.isEmpty(name) && !name.equals(mContext.getResources().getString(R.string
                .required))) {
            getView().setEditText(name);
        } else {
            getView().setEditText("");
        }
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords.clear();
    }

    private void save(String text) {
        List<String> list = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAMEPLATE_NAME, text);
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(list);
    }

    public void doChoose(final String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.contains("[")||text.contains("]")||text.contains("】")||text.contains("【")) {
                getView().toastShort(mContext.getString(R.string.name_address_no_contain_brackets));
                return;
            }
            byte[] bytes = new byte[0];
            try {
                bytes = text.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort(mContext.getString(R.string.name_address_length));
                return;
            }

        } else {
            getView().toastShort(mContext.getString(R.string.must_enter_name_address));
            return;
        }
        doResult(text);
//
    }

    private void doResult(String text) {
        save(text);
//        mKeywordEt.clearFocus();
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_NAMEPLATE_NAME;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void clearHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAMEPLATE_NAME);
        mHistoryKeywords.clear();
        if (isAttachedView()) {
            getView().updateSearchHistoryData(mHistoryKeywords);
        }

    }
}
