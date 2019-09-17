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
import com.sensoro.smartcity.imainviews.IDeployMonitorNameAddressActivityView;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeployMonitorNameAddressActivityPresenter extends BasePresenter<IDeployMonitorNameAddressActivityView>  {
    private Activity mContext;
    private final List<String> mHistoryKeywords = new ArrayList<>();
//    private int deployType;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
//        String sn = mContext.getIntent().getStringExtra(EXTRA_DEPLOY_TO_SN);
//        deployType = mContext.getIntent().getIntExtra(EXTRA_DEPLOY_TYPE, -1);
//        if (!TextUtils.isEmpty(sn)) {
////            getView().updateTvTitle(sn);
//        }
        String name = mContext.getIntent().getStringExtra(Constants.EXTRA_SETTING_NAME_ADDRESS);
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
        List<String> list = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS, text);
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(list);
//        String oldText = PreferencesHelper.getInstance().getDeployNameAddressHistory();
//        if (!TextUtils.isEmpty(text)) {
//            if (mHistoryKeywords.contains(text)) {
//                ArrayList<String> list = new ArrayList<>();
//                for (String o : oldText.split(",")) {
//                    if (!o.equalsIgnoreCase(text)) {
//                        list.add(o);
//                    }
//                }
//                list.add(0, text);
//                mHistoryKeywords.clear();
//                mHistoryKeywords.addAll(list);
//                StringBuilder stringBuilder = new StringBuilder();
//                int size;
//                if (list.size() > 20) {
//                    size = 20;
//                } else {
//                    size = list.size();
//                }
//                for (int i = 0; i < size; i++) {
//                    if (i == (size - 1)) {
//                        stringBuilder.append(list.get(i));
//                    } else {
//                        stringBuilder.append(list.get(i)).append(",");
//                    }
//                }
//                PreferencesHelper.getInstance().saveDeployNameAddressHistory(stringBuilder.toString());
//                return list;
//            } else {
//                if (TextUtils.isEmpty(oldText)) {
//                    PreferencesHelper.getInstance().saveDeployNameAddressHistory(text);
//                } else {
//                    String format = String.format(Locale.ROOT, "%s,%s", text, oldText);
//                    String[] split = format.split(",");
//                    StringBuilder sb = new StringBuilder();
//                    int total;
//                    if (split.length > 20) {
//                        total = 20;
//                    } else {
//                        total = split.length;
//                    }
//                    for (int i = 0; i < total; i++) {
//                        if(i == total -1){
//                            sb.append(split[i]);
//                        }else{
//                            sb.append(split[i]).append(",");
//                        }
//
//                    }
//                    PreferencesHelper.getInstance().saveDeployNameAddressHistory(sb.toString());
//                }
//                String[] split = PreferencesHelper.getInstance().getDeployNameAddressHistory().split(",");
//                return Arrays.asList(split);
////                mHistoryKeywords.add(0, text);
////                if (mHistoryKeywords.size() > 20) {
////                    for (int i = 20; i < mHistoryKeywords.size(); i++) {
////                        mHistoryKeywords.remove(i);
////                    }
////                }
//            }
//        }
//        return new ArrayList<String>();
    }

    public void doChoose(final String text) {
        if(!isAttachedView())
            return;

        if (!TextUtils.isEmpty(text)) {
            if (text.contains("[") || text.contains("]") || text.contains("】") || text.contains("【")) {
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
        //TODO 暂时去掉重名检测
//        //跟原先名字一样 保存
//        if (text.equals(originName)) {
//            doResult(text);
//            return;
//        }
//        //基站设备不进行校验
//        if (deployType != -1 && deployType == TYPE_SCAN_DEPLOY_STATION) {
//            doResult(text);
//            return;
//        }
//        getView().showProgressDialog();
//        RetrofitServiceHelper.INSTANCE.getDeviceNameValid(text).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
//            @Override
//            public void onCompleted(ResponseBase responseBase) {
//                if (isAttachedView()) {
//                    getView().dismissProgressDialog();
//                    doResult(text);
//                }
//            }
//
//            @Override
//            public void onErrorMsg(int errorCode, String errorMsg) {
////                if (errorCode==4007108){
//////此code为重名
//////                }
//                if (isAttachedView()) {
//                    getView().dismissProgressDialog();
//                    getView().toastShort(errorMsg);
//                }
//
//
//            }
//        });
//
    }

    private void doResult(String text) {
        save(text);
//        mKeywordEt.clearFocus();
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void clearHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_DEPLOY_NAME_ADDRESS);
        mHistoryKeywords.clear();
        if (isAttachedView()) {
            getView().updateSearchHistoryData(mHistoryKeywords);
        }

    }
}
