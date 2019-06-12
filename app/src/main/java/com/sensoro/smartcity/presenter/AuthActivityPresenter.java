package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceMergeTypesInfo;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.SensorTypeStyles;
import com.sensoro.common.server.response.AuthRsp;
import com.sensoro.common.server.response.DevicesMergeTypesRsp;
import com.sensoro.smartcity.BuildConfig;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.imainviews.IAuthActivityView;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AuthActivityPresenter extends BasePresenter<IAuthActivityView> implements  IOnStart {
    private Activity mContext;
    private EventLoginData mEventLoginData;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mEventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra(Constants.EXTRA_EVENT_LOGIN_DATA);
    }

    @Override
    public void onDestroy() {

    }

    public void doAuthCheck(String code) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().doubleCheck(code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AuthRsp>(this) {

            @Override
            public void onCompleted(AuthRsp authRsp) {
                if (authRsp.isData()) {
                    //请求mergetypes字段
                    getMergeType();
                } else {
                    if (isAttachedView()) {
                        getView().dismissProgressDialog();
                        getView().toastShort(mContext.getString(R.string.secondary_verification_failed));
                        getView().updateImvStatus(false);
                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (isAttachedView()){
                    getView().toastShort(errorMsg);
                    getView().dismissProgressDialog();
                }
            }
        });
    }

    private void getMergeType() {
        RetrofitServiceHelper.getInstance().getDevicesMergeTypes().subscribeOn(Schedulers.io()).doOnNext(new Consumer<DevicesMergeTypesRsp>() {
            @Override
            public void accept(DevicesMergeTypesRsp devicesMergeTypesRsp) throws Exception {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
                //测试信息
                if (BuildConfig.DEBUG) {
                    DeviceMergeTypesInfo.DeviceMergeTypeConfig config = data.getConfig();
                    Map<String, DeviceTypeStyles> deviceType = config.getDeviceType();
                    for (Map.Entry<String, DeviceTypeStyles> next : deviceType.entrySet()) {
                        String key = next.getKey();
                        DeviceTypeStyles value = next.getValue();
                        try {
                            LogUtils.loge("getDevicesMergeTypes---DeviceTypeStyles>> " + key + "," + value.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    Map<String, MergeTypeStyles> mergeType = config.getMergeType();
                    for (Map.Entry<String, MergeTypeStyles> next : mergeType.entrySet()) {
                        String key = next.getKey();
                        MergeTypeStyles value = next.getValue();
                        try {
                            LogUtils.loge("getDevicesMergeTypes---MergeTypeStyles>> " + key + "," + value.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    Map<String, SensorTypeStyles> sensorType = config.getSensorType();
                    for (Map.Entry<String, SensorTypeStyles> next : sensorType.entrySet()) {
                        String key = next.getKey();
                        SensorTypeStyles value = next.getValue();
                        try {
                            LogUtils.loge("getDevicesMergeTypes---SensorTypeStyles>> " + key + "," + value.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    try {
                        LogUtils.loge("getDevicesMergeTypes--->> " + deviceType.size() + "," + mergeType.size() + "," + sensorType.size());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(AuthActivityPresenter.this) {
            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                saveLoginDataOpenMain(mEventLoginData);
                try {
                    LogUtils.loge("DevicesMergeTypesRsp ....." + mEventLoginData.toString());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }


            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (isAttachedView()){
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            }
        });
    }

    private void saveLoginDataOpenMain(EventLoginData eventLoginData) {
        //
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_AUTH_SUC;
        EventBus.getDefault().post(eventData);
        Intent mainIntent = new Intent();
        mainIntent.setClass(mContext, MainActivity.class);
        mainIntent.putExtra(Constants.EXTRA_EVENT_LOGIN_DATA, eventLoginData);
        if (isAttachedView()){
            getView().startAC(mainIntent);
            getView().finishAc();
        }
    }

    public void close() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_CANCEL_AUTH;
        EventBus.getDefault().post(eventData);
        if (isAttachedView()){
            getView().finishAc();
        }
    }

    @Override
    public void onStart() {
        String textFromClip = AppUtils.getTextFromClip(mContext);
        if (!TextUtils.isEmpty(textFromClip) && textFromClip.length() == 6) {
            char[] chars = textFromClip.toCharArray();
            final List<String> cacheCode = new ArrayList<>();
            for (char c : chars) {
                String intStr = String.valueOf(c);
                try {
                    Integer.parseInt(intStr);
                } catch (Exception e) {
                    return;
                }
                cacheCode.add(intStr);
            }
            if (isAttachedView()){
                getView().autoFillCode(cacheCode);
            }
            doAuthCheck(textFromClip);
        }
    }

    @Override
    public void onStop() {

    }
}
