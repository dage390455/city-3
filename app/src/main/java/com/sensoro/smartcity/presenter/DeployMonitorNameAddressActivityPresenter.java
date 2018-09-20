package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorNameAddressActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeployMonitorNameAddressActivityPresenter extends BasePresenter<IDeployMonitorNameAddressActivityView> implements Constants {
    private Activity mContext;
    private final List<String> mHistoryKeywords = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        String name = mContext.getIntent().getStringExtra(EXTRA_SETTING_NAME_ADDRESS);
        String history = PreferencesHelper.getInstance().getDeployNameAddressHistory();
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        getView().updateSearchHistoryData(mHistoryKeywords);
        if (!TextUtils.isEmpty(name) && !name.equals(mContext.getResources().getString(R.string
                .tips_hint_name_address_set))) {
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
        String oldText = PreferencesHelper.getInstance().getDeployNameAddressHistory();
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                List<String> list = new ArrayList<>();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        list.add(o);
                    }
                }
                list.add(0, text);
                mHistoryKeywords.clear();
                mHistoryKeywords.addAll(list);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i == (list.size() - 1)) {
                        stringBuilder.append(list.get(i));
                    } else {
                        stringBuilder.append(list.get(i)).append(",");
                    }
                }
                PreferencesHelper.getInstance().saveDeployNameAddressHistory(stringBuilder.toString());
            } else {
                if (TextUtils.isEmpty(oldText)) {
                    PreferencesHelper.getInstance().saveDeployNameAddressHistory(text);
                } else {
                    PreferencesHelper.getInstance().saveDeployNameAddressHistory(text + "," + oldText);
                }
                mHistoryKeywords.add(0, text);
            }
        }
    }

    public void doChoose(String text) {
        if (!TextUtils.isEmpty(text)) {
            byte[] bytes = new byte[0];
            try {
                bytes = text.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort("名称/地址最长不能超过16个汉字或48个字符");
                return;
            }

        } else {
            getView().toastShort("必须输入名称/地址");
            return;
        }
        save(text);
//        mKeywordEt.clearFocus();
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
