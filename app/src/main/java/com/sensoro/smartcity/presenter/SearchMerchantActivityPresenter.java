package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISearchMerchantActivityView;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.UserAccountRsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchMerchantActivityPresenter extends BasePresenter<ISearchMerchantActivityView> implements Constants {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private final List<String> mHistoryKeywords = new ArrayList<>();
    private Activity mContext;


    public List<String> getmHistoryKeywords() {
        return mHistoryKeywords;
    }

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        initSearchHistory();
    }

    private void initSearchHistory() {
        mPref = mContext.getSharedPreferences(PREFERENCE_MERCHANT_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        String history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        getView().setSearchHistoryLayoutVisible(mHistoryKeywords.size() > 0);
    }

    public void save(String text) {
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                mHistoryKeywords.clear();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        mHistoryKeywords.add(o);
                    }
                }
                mHistoryKeywords.add(0, text);
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mHistoryKeywords.size(); i++) {
                    if (i == (mHistoryKeywords.size() - 1)) {
                        stringBuffer.append(mHistoryKeywords.get(i));
                    } else {
                        stringBuffer.append(mHistoryKeywords.get(i) + ",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
                mEditor.commit();
            } else {
                mEditor.putString(PREFERENCE_KEY_DEVICE, text + "," + oldText);
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
            getView().updateSearchHistory();
        }
    }

    public void requestData(String text) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getUserAccountList(text).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>() {


            @Override
            public void onCompleted() {

            }

            @Override
            public void onNext(UserAccountRsp userAccountRsp) {
                getView().dismissProgressDialog();
                List<UserInfo> list = userAccountRsp.getData();
                if (list.size() == 0) {
                    getView().setTipsLinearLayoutVisible(true);
                } else {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_MERCHANT_INFO, userAccountRsp);
                    getView().setIntentResult(RESULT_CODE_SEARCH_MERCHANT, data);
                    getView().finishAc();
                }
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        getView().updateSearchHistory();
        getView().setSearchHistoryLayoutVisible(false);
    }
}
