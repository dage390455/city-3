package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorNameAddressActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployMonitorNameAddressActivityPresenter extends BasePresenter<IDeployMonitorNameAddressActivityView> implements Constants {
    private Activity mContext;
    private final List<String> mHistoryKeywords = new ArrayList<>();
    private int deployType;
    private String originName;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        String sn = mContext.getIntent().getStringExtra(EXTRA_DEPLOY_TO_SN);
        originName = mContext.getIntent().getStringExtra(EXTRA_DEPLOY_ORIGIN_NAME_ADDRESS);
        deployType = mContext.getIntent().getIntExtra(EXTRA_DEPLOY_TYPE, -1);
        if (!TextUtils.isEmpty(sn)) {
            getView().updateTvTitle(sn);
        }
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

    public void doChoose(final String text) {
        if (!TextUtils.isEmpty(text)) {
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
        //跟原先名字一样 保存
        if (text.equals(originName)) {
            doResult(text);
            return;
        }
        //基站设备不进行校验
        if (deployType != -1 && deployType == TYPE_SCAN_DEPLOY_STATION) {
            doResult(text);
            return;
        }
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceNameValid(text).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
            @Override
            public void onCompleted(ResponseBase responseBase) {
                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    doResult(text);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
//                if (errorCode==4007108){
////此code为重名
////                }
                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }


            }
        });
//
    }

    private void doResult(String text) {
        save(text);
//        mKeywordEt.clearFocus();
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS;
        eventData.data = text;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
