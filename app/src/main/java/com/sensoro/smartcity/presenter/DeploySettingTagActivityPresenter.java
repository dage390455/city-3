package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeploySettingTagActivityView;
import com.sensoro.smartcity.iwidget.IOndestroy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeploySettingTagActivityPresenter extends BasePresenter<IDeploySettingTagActivityView> implements
        Constants, IOndestroy {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public List<String> getHistoryKeywords() {
        return mHistoryKeywords;
    }

    private final List<String> mHistoryKeywords = new ArrayList<>();

    public void setTagList(List<String> tagList) {
        mTagList.clear();
        mTagList.addAll(tagList);
    }

    private final List<String> mTagList = new ArrayList<>();
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mPref = mContext.getSharedPreferences(PREFERENCE_DEPLOY_TAG_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        //
        ArrayList<String> stringArrayListExtra = mContext.getIntent().getStringArrayListExtra(EXTRA_SETTING_TAG_LIST);
        if (stringArrayListExtra != null) {
            mTagList.addAll(stringArrayListExtra);
        }
        String history = mPref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }

        if (mTagList.size() > 0) {
            getView().updateTags(mTagList);
        }
        getView().updateSearchHistory();
        getView().setSearchHistoryLayoutVisible(mHistoryKeywords.size() > 0);
    }

    public void doChoose(Boolean isFinish, List<String> tags) {
        save(tags);
        if (isFinish) {
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) mTagList);
            getView().setIntentResult(RESULT_CODE_SETTING_TAG, data);
            getView().finishAc();
        }
    }

    public void save(List<String> tags) {
        //原数据
        String oldText = mPref.getString(PREFERENCE_KEY_DEPLOY_NAME, "");
        List<String> oldHistoryList = new ArrayList<String>();
        if (!TextUtils.isEmpty(oldText)) {
            oldHistoryList.addAll(Arrays.asList(oldText.split(",")));
        }
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                if (!oldHistoryList.contains(tag)) {
                    oldHistoryList.add(0, tag);
                }
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < oldHistoryList.size(); i++) {
            if (i == (oldHistoryList.size() - 1)) {
                stringBuffer.append(oldHistoryList.get(i));
            } else {
                stringBuffer.append(oldHistoryList.get(i) + ",");
            }
        }
        mEditor.putString(PREFERENCE_KEY_DEPLOY_NAME, stringBuffer.toString());
        mEditor.commit();

    }

    public void clickHistory(int position) {
        String test = mHistoryKeywords.get(position);
        if (!TextUtils.isEmpty(test)) {
            String trim = test.trim();
            if (mTagList.contains(trim)) {
                getView().toastShort("标签不能重复");
                return;
            } else {
                mTagList.add(trim);
            }

        }
        getView().updateTags(mTagList);
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords.clear();
        mTagList.clear();
    }
}
