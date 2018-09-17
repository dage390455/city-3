package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployDeviceTagActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mabbas007.tagsedittext.utils.ResourceUtils;

public class DeployDeviceTagActivityPresenter extends BasePresenter<IDeployDeviceTagActivityView> implements Constants {
    private final List<String> mHistoryKeywords = new ArrayList<>();
    private final List<String> mTagList = new ArrayList<>();
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

        ArrayList<String> stringArrayListExtra = mContext.getIntent().getStringArrayListExtra(EXTRA_SETTING_TAG_LIST);
        if (stringArrayListExtra != null) {
            mTagList.addAll(stringArrayListExtra);
        }
        String history = PreferencesHelper.getInstance().getDeployTagsHistory();
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }

        if (mTagList.size() > 0) {
            getView().updateTags(mTagList);
        }
        getView().updateSearchHistory(mHistoryKeywords);
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords.clear();
        mTagList.clear();
    }

    public void clickDeleteTag(int position) {
        mTagList.remove(position);
        getView().updateTags(mTagList);
    }

    public void addTags(String tag) {
        if (mTagList.size() >= 5) {
            getView().toastShort("最多只能添加5个标签");
        } else {
            if (!TextUtils.isEmpty(tag)) {
                String trim = tag.trim();
                if (mTagList.contains(trim)) {
                    getView().toastShort("标签不能重复");
                    return;
                } else {
                    mTagList.add(trim);
                }
                getView().updateTags(mTagList);
            }
        }
        getView().dismissDialog();
    }

    public void addTags(int position) {
        String test = mHistoryKeywords.get(position);
        if (mTagList.size() >= 5) {
            getView().toastShort("最多只能添加5个标签");
        } else {
            if (!TextUtils.isEmpty(test)) {
                String trim = test.trim();
                if (mTagList.contains(trim)) {
                    getView().toastShort("标签不能重复");
                    return;
                } else {
                    mTagList.add(trim);
                }
                getView().updateTags(mTagList);
            }
        }
    }

    public void doFinish() {
        if (mTagList.size() > 5) {
            getView().toastShort("最多只能添加5个标签");
        } else {
            for (String temp : mTagList) {
                if (ResourceUtils.getByteFromWords(temp) > 30) {
                    getView().toastShort("标签最长不能超过10个汉字或30个字符");
                    return;
                }
            }
            save();
            EventData eventData = new EventData();
            eventData.code = EVENT_DATA_DEPLOY_SETTING_TAG;
            eventData.data = mTagList;
            EventBus.getDefault().post(eventData);
            getView().finishAc();
        }
    }


    private void save() {
        //原数据
        String oldText = PreferencesHelper.getInstance().getDeployTagsHistory();
        List<String> oldHistoryList = new ArrayList<>();
        if (!TextUtils.isEmpty(oldText)) {
            oldHistoryList.addAll(Arrays.asList(oldText.split(",")));
        }
        if (mTagList.size() > 0) {
            for (String tag : mTagList) {
                if (!oldHistoryList.contains(tag)) {
                    oldHistoryList.add(0, tag);
                }
            }
        }
        ArrayList<String> tempList = new ArrayList<>();
        for (String str : oldHistoryList) {
            if (tempList.size() < 20) {
                tempList.add(str);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tempList.size(); i++) {
            if (i == (tempList.size() - 1)) {
                stringBuilder.append(tempList.get(i));
            } else {
                stringBuilder.append(tempList.get(i)).append(",");
            }
        }
        PreferencesHelper.getInstance().saveDeployTagsHistory(stringBuilder.toString());
    }


    public void doEditTag(int position) {
        String tag = mTagList.get(position);
        getView().showDialogWithEdit(tag, position);
    }

    public void updateEditTag(int position, String text) {
        mTagList.set(position, text);
        getView().updateTags(mTagList);
        getView().dismissDialog();
    }
}
