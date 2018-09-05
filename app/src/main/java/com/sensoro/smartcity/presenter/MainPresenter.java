package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.igexin.sdk.PushManager;
import com.lzy.imagepicker.ImagePicker;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.factory.MenuPageFactory;
import com.sensoro.smartcity.fragment.AlarmListFragment;
import com.sensoro.smartcity.fragment.ContractFragment;
import com.sensoro.smartcity.fragment.IndexFragment;
import com.sensoro.smartcity.fragment.MerchantSwitchFragment;
import com.sensoro.smartcity.fragment.PointDeployFragment;
import com.sensoro.smartcity.fragment.ScanLoginFragment;
import com.sensoro.smartcity.fragment.StationDeployFragment;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmDeviceCountsBean;
import com.sensoro.smartcity.model.DeviceAlarmCount;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.model.MenuPageInfo;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<IMainView> implements Constants, IOnCreate {
    private Activity mActivity;
    private long exitTime = 0;
    private final List<Fragment> fragmentList = new ArrayList<>();
    //
    private IndexFragment indexFragment = null;
    private AlarmListFragment alarmListFragment = null;
    private MerchantSwitchFragment merchantSwitchFragment = null;
    private PointDeployFragment pointDeployFragment = null;
    private StationDeployFragment stationDeployFragment = null;
    private ContractFragment contractFragment = null;
    private ScanLoginFragment scanLoginFragment = null;
    //
    private volatile Socket mSocket = null;
    private final DeviceInfoListener mInfoListener = new DeviceInfoListener();
    private final DeviceAlarmCountListener mAlarmCountListener = new DeviceAlarmCountListener();

    private final Handler mHandler = new Handler();
    private final TaskRunnable mRunnable = new TaskRunnable();
    //
    private EventLoginData mEventLoginData;

    /**
     * 超级用户
     *
     * @return
     */
    public boolean isSupperAccount() {
        return mEventLoginData != null && mEventLoginData.isSupperAccount;
    }

    public String getRoles() {
        if (mEventLoginData != null) {
            return mEventLoginData.roles;
        }
        return "";
    }

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        onCreate();
        Beta.init(mActivity.getApplicationContext(), false);
        indexFragment = IndexFragment.newInstance(null);
        alarmListFragment = AlarmListFragment.newInstance("alarm");
        merchantSwitchFragment = MerchantSwitchFragment.newInstance("merchant");
        pointDeployFragment = PointDeployFragment.newInstance("point");
        stationDeployFragment = StationDeployFragment.newInstance("station");
        contractFragment = ContractFragment.newInstance("contract");
        scanLoginFragment = ScanLoginFragment.newInstance("scanLogin");
        //
        if (fragmentList.size() > 0) {
            fragmentList.clear();
        }
        fragmentList.add(indexFragment);
        fragmentList.add(alarmListFragment);
        fragmentList.add(merchantSwitchFragment);
        fragmentList.add(pointDeployFragment);
        fragmentList.add(stationDeployFragment);
        fragmentList.add(scanLoginFragment);
        fragmentList.add(contractFragment);
        getView().updateMainPageAdapterData(fragmentList);
        mEventLoginData = (EventLoginData) mActivity.getIntent().getSerializableExtra("eventLoginData");
        //
        if (null != mEventLoginData) {
            //赋值
            LogUtils.loge("onDataEvent ---->>>" + mEventLoginData.toString());
            getView().showAccountInfo(mEventLoginData.userName, mEventLoginData.phone);
            if (!PushManager.getInstance().isPushTurnedOn(SensoroCityApplication.getInstance())) {
                PushManager.getInstance().turnOnPush(SensoroCityApplication.getInstance());
            }
            mHandler.postDelayed(mRunnable, 3000L);
            freshAccountType();
        } else {
            openLogin();
        }
        LogUtils.loge(this, "initData");
    }


    //没有登录跳转登录界面
    private void openLogin() {
        Intent loginIntent = new Intent();
        loginIntent.setClass(mActivity, LoginActivity.class);
        getView().startAC(loginIntent);
        getView().finishAc();
    }

    public void changeAccount(EventLoginData eventLoginData) {
        mEventLoginData = eventLoginData;
        //
        PreferencesHelper.getInstance().saveUserData(eventLoginData);
        getView().showAccountInfo(mEventLoginData.userName, mEventLoginData.phone);
        if (indexFragment != null) {
            if (mEventLoginData.isSupperAccount) {
                merchantSwitchFragment.requestDataByDirection(DIRECTION_DOWN, true);
            } else {
                indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
            }
            merchantSwitchFragment.refreshData(mEventLoginData.userName, mEventLoginData.phone, mEventLoginData.phoneId);
            //
            getView().updateMenuPager(MenuPageFactory.createMenuPageList(mEventLoginData));
            getView().setCurrentPagerItem(0);
            getView().setMenuSelected(0);
            reconnect();
        }

    }

    public void setAppVersion() {
        PackageManager manager = mActivity.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mActivity.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            int currentVersionCode = info.versionCode; // 版本号
            getView().setAPPVersionCode("City " + appVersionName);
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 简单判断账户类型
     */
    private void freshAccountType() {
        if (isSupperAccount()) {
            getView().setCurrentPagerItem(2);
        } else {
            getView().setCurrentPagerItem(0);
        }
        //TODO 考虑到声明周期问题 暂时延缓后续优化

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().updateMenuPager(MenuPageFactory.createMenuPageList(mEventLoginData));
                if (mEventLoginData.isSupperAccount) {
                    merchantSwitchFragment.requestDataByDirection(DIRECTION_DOWN, true);
                } else {
                    indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
                }
                merchantSwitchFragment.refreshData(mEventLoginData.userName, (mEventLoginData.phone == null ? "" : mEventLoginData.phone), mEventLoginData.phoneId);
                getView().setMenuSelected(0);
            }
        }, 100);
    }


    private void createSocket() {
        try {
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.on(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void reconnect() {
        try {
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.off(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket = null;
            }
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.on(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void logout() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.logout(mEventLoginData.phoneId, mEventLoginData.userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(new CityObserver<ResponseBase>() {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }

            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
                getView().finishAc();
            }


            @Override
            public void onNext(ResponseBase responseBase) {
                if (responseBase.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    RetrofitServiceHelper.INSTANCE.clearSessionId();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    getView().startAC(intent);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacksAndMessages(null);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.off(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
            mSocket = null;
        }
        fragmentList.clear();
        Beta.unInit();
        LogUtils.loge(this, "onDestroy");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        if (code == EVENT_DATA_SESSION_ID_OVERTIME) {
            RetrofitServiceHelper.INSTANCE.cancelAllRsp();
            openLogin();
        } else if (code == EVENT_DATA_FINISH_CODE) {
            if (contractFragment != null) {
                contractFragment.requestDataByDirection(DIRECTION_DOWN, false);
            }
        } else if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            if (data != null && data instanceof DeviceInfo) {
                refreshDeviceInfo((DeviceInfo) data);
            }
            getView().setCurrentPagerItem(0);
            getView().setMenuSelected(0);
        } else if (code == EVENT_DATA_SEARCH_MERCHANT) {
            if (data != null && data instanceof EventLoginData) {
                EventLoginData eventLoginData = (EventLoginData) data;
                changeAccount(eventLoginData);
            }
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (alarmListFragment.onKeyDown(keyCode, event)) {
            exit();
        }
        return false;
    }


    private final class TaskRunnable implements Runnable {

        @Override
        public void run() {
            //检查更新
            Beta.checkUpgrade(false, false);
            if (!isSupperAccount()) {
                createSocket();
            }
        }
    }

    private final class DeviceInfoListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (DeviceInfoListener.class) {
                    for (Object arg : args) {
                        if (arg instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) arg;
                            final JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String json = jsonObject.toString();
//                            LogUtils.loge(this, "jsonArray = " + json);
                            if (!isSupperAccount()) {
                                try {
                                    DeviceInfo data = RetrofitServiceHelper.INSTANCE.getGson().fromJson(json,
                                            DeviceInfo.class);
                                    final EventData eventData = new EventData();
                                    eventData.code = EVENT_DATA_SOCKET_DATA_INFO;
                                    eventData.data = data;
                                    EventBus.getDefault().post(eventData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private final class DeviceAlarmCountListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (DeviceInfoListener.class) {
                    for (Object arg : args) {
                        if (arg instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) arg;
                            String json = jsonObject.toString();
                            LogUtils.loge(this, "DeviceAlarmCountListener jsonArray = " + json);
                            if (!isSupperAccount()) {
                                try {
                                    DeviceAlarmCount deviceAlarmCount = RetrofitServiceHelper.INSTANCE.getGson().fromJson(json, DeviceAlarmCount.class);
                                    List<DeviceAlarmCount.AllBean> all = deviceAlarmCount.getAll();
                                    DeviceAlarmCount.AllBean allBean = all.get(0);
                                    AlarmDeviceCountsBean counts = allBean.getCounts();
                                    final EventData eventData = new EventData();
                                    eventData.code = EVENT_DATA_SOCKET_DATA_COUNT;
                                    eventData.data = counts;
                                    EventBus.getDefault().post(eventData);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            getView().toastShort(mActivity.getResources().getString(R.string.exit_main));
            exitTime = System.currentTimeMillis();
        } else {
            getView().finishAc();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS || resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (alarmListFragment != null) {
                alarmListFragment.handlerActivityResult(requestCode, resultCode, data);
            }
        }

    }

    /**
     * 通过menupage判断类型
     */
    public void clickMenuItem(int menuPageId) {
        switch (menuPageId) {
            case MenuPageInfo.MENU_PAGE_INDEX:
                indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
                getView().setCurrentPagerItem(0);
                break;
            case MenuPageInfo.MENU_PAGE_ALARM:
                alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
                getView().setCurrentPagerItem(1);
                break;
            case MenuPageInfo.MENU_PAGE_MERCHANT:
                merchantSwitchFragment.requestDataByDirection(DIRECTION_DOWN, true);
                getView().setCurrentPagerItem(2);
                break;
            case MenuPageInfo.MENU_PAGE_POINT:
                getView().setCurrentPagerItem(3);
                break;
            case MenuPageInfo.MENU_PAGE_STATION:
                getView().setCurrentPagerItem(4);
                break;
            case MenuPageInfo.MENU_PAGE_SCAN_LOGIN:
                getView().setCurrentPagerItem(5);
                break;
            case MenuPageInfo.MENU_PAGE_CONTRACT:
                contractFragment.requestDataByDirection(DIRECTION_DOWN, true);
                getView().setCurrentPagerItem(6);
                break;
            default:
                break;
        }
    }

    private void refreshDeviceInfo(DeviceInfo deviceInfo) {
        for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
            DeviceInfo tempDeviceInfo = SensoroCityApplication.getInstance().getData().get(i);
            if (deviceInfo.getSn().equals(tempDeviceInfo.getSn())) {
                SensoroCityApplication.getInstance().getData().set(i, deviceInfo);
                break;
            }
        }
    }
}
