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
import com.sensoro.common.utils.RegexUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployForestCameraInstallPositionActivityView;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeployForestCameraInstallPositionActivityPresenter extends BasePresenter<IDeployForestCameraInstallPositionActivityView> {
    private Activity mContext;
    private final List<String> mHistoryKeywords = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

        String account = mContext.getIntent().getStringExtra(Constants.EXTRA_SETTING_FOREST_CAMERA_INSTALL_POSITION);
        String history = PreferencesHelper.getInstance().getDeployForestCameraInstallPositionHistory();
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
        List<String> list = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_FOREST_CAMERA_INSTALL_POSITION, text);
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(list);
    }

    public void doChoose(String text) {
        //TODO 限制字符
        if (!TextUtils.isEmpty(text)) {

            if (text.contains("[") || text.contains("]") || text.contains("】") || text.contains("【")) {
                getView().toastShort(mContext.getString(R.string.deploy_forest_camera_no_install_position));
                return;
            }
            byte[] bytes = new byte[0];
            try {
                bytes = text.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort(mContext.getString(R.string.deploy_forest_camera_install_position_length));
                return;
            }
            save(text);
        } else {
            getView().toastShort(mContext.getString(R.string.must_enter_deploy_forest_camera_no_install_position));
            return;
        }
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_SETTING_FOREST_DEPLOY_INSTALL_POSITION;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void checkCanSave(String phone) {
        boolean isEnable = !TextUtils.isEmpty(phone) && RegexUtils.checkPhone(phone);
        getView().updateSaveStatus(isEnable);

    }

    public void clearHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_FOREST_CAMERA_INSTALL_POSITION);
        mHistoryKeywords.clear();
        if (isAttachedView()) {
            getView().updateSearchHistoryData(mHistoryKeywords);
        }

    }
}
