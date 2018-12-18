package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.BuildConfig;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAuthActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.server.response.AuthRsp;
import com.sensoro.smartcity.server.response.DevicesMergeTypesRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AuthActivityPresenter extends BasePresenter<IAuthActivityView> implements Constants, IOnStart {
    private Activity mContext;
    private EventLoginData mEventLoginData;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mEventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra(EXTRA_EVENT_LOGIN_DATA);
    }

    @Override
    public void onDestroy() {

    }

    public void doAuthCheck(String code) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doubleCheck(code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AuthRsp>(this) {

            @Override
            public void onCompleted(AuthRsp authRsp) {
                if (authRsp.isData()) {
                    //请求mergetypes字段
                    getMergeType();
                } else {
                    getView().dismissProgressDialog();
                    getView().toastShort(mContext.getString(R.string.secondary_verification_failed));
                    getView().updateImvStatus(false);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });
    }

    private void getMergeType() {
        RetrofitServiceHelper.INSTANCE.getDevicesMergeTypes().subscribeOn(Schedulers.io()).doOnNext(new Action1<DevicesMergeTypesRsp>() {
            @Override
            public void call(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
                //测试信息
                if (BuildConfig.DEBUG) {
                    DeviceMergeTypesInfo.DeviceMergeTypeConfig config = data.getConfig();
                    Map<String, DeviceTypeStyles> deviceType = config.getDeviceType();
                    for (Map.Entry<String, DeviceTypeStyles> next : deviceType.entrySet()) {
                        String key = next.getKey();
                        DeviceTypeStyles value = next.getValue();
                        LogUtils.loge("getDevicesMergeTypes---DeviceTypeStyles>> " + key + "," + value.toString());
                    }
                    Map<String, MergeTypeStyles> mergeType = config.getMergeType();
                    for (Map.Entry<String, MergeTypeStyles> next : mergeType.entrySet()) {
                        String key = next.getKey();
                        MergeTypeStyles value = next.getValue();
                        LogUtils.loge("getDevicesMergeTypes---MergeTypeStyles>> " + key + "," + value.toString());
                    }
                    Map<String, SensorTypeStyles> sensorType = config.getSensorType();
                    for (Map.Entry<String, SensorTypeStyles> next : sensorType.entrySet()) {
                        String key = next.getKey();
                        SensorTypeStyles value = next.getValue();
                        LogUtils.loge("getDevicesMergeTypes---SensorTypeStyles>> " + key + "," + value.toString());
                    }
                    LogUtils.loge("getDevicesMergeTypes--->> " + deviceType.size() + "," + mergeType.size() + "," + sensorType.size());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(AuthActivityPresenter.this) {
            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                saveLoginDataOpenMain(mEventLoginData);
                LogUtils.loge("DevicesMergeTypesRsp ....." + mEventLoginData.toString());
            }


            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void saveLoginDataOpenMain(EventLoginData eventLoginData) {
        //
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_AUTH_SUC;
        EventBus.getDefault().post(eventData);
        Intent mainIntent = new Intent();
        mainIntent.setClass(mContext, MainActivity.class);
        mainIntent.putExtra(EXTRA_EVENT_LOGIN_DATA, eventLoginData);
        getView().startAC(mainIntent);
        getView().finishAc();
    }

    public void close() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_CANCEL_AUTH;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
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
            getView().autoFillCode(cacheCode);
            doAuthCheck(textFromClip);
        }
    }

    @Override
    public void onStop() {

    }
}
