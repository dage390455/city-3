package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.igexin.sdk.PushManager;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.fragment.HomeFragment;
import com.sensoro.smartcity.fragment.ManagerFragment;
import com.sensoro.smartcity.fragment.WarnFragment;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmDeviceCountsBean;
import com.sensoro.smartcity.server.bean.DeviceAlarmCount;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.NetWorkUtils;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.AlarmCountRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;
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

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private Activity mContext;
    //
    private long exitTime = 0;
    private volatile Socket mSocket = null;
    private final MainPresenter.DeviceInfoListener mInfoListener = new MainPresenter.DeviceInfoListener();
    private final MainPresenter.DeviceAlarmCountListener mAlarmCountListener = new MainPresenter.DeviceAlarmCountListener();
    private final DeviceAlarmDisplayStatusListener mAlarmDisplayStatusListener = new DeviceAlarmDisplayStatusListener();
    private final Handler mHandler = new Handler();
    private final MainPresenter.TaskRunnable mRunnable = new MainPresenter.TaskRunnable();
    //
    private WarnFragment warnFragment;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        initViewPager();
    }

    private void initViewPager() {
        onCreate();
        //
        HomeFragment homeFragment = new HomeFragment();
        warnFragment = new WarnFragment();
        ManagerFragment managerFragment = new ManagerFragment();
        if (mFragmentList.size() > 0) {
            mFragmentList.clear();
        }
        mFragmentList.add(homeFragment);
        mFragmentList.add(warnFragment);
        mFragmentList.add(managerFragment);
        getView().updateMainPageAdapterData(mFragmentList);
        //
        Beta.init(mContext.getApplicationContext(), false);
        final EventLoginData eventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra(EXTRA_EVENT_LOGIN_DATA);
        //
        if (null != eventLoginData) {
            //赋值
            LogUtils.loge("onDataEvent ---->>>" + eventLoginData.toString());
            PreferencesHelper.getInstance().saveUserData(eventLoginData);
            //显示账户信息
//            getView().showAccountInfo(mEventLoginData.userName, mEventLoginData.phone);
            freshAccountType();
            if (!PushManager.getInstance().isPushTurnedOn(SensoroCityApplication.getInstance())) {
                PushManager.getInstance().turnOnPush(SensoroCityApplication.getInstance());
            }
            mHandler.postDelayed(mRunnable, 3000L);
            freshAlarmCount();
        } else {
            openLogin();
        }
        LogUtils.loge(this, "refreshData");
    }

    /**
     * 超级用户
     *
     * @return
     */
    private boolean isSupperAccount() {
        return PreferencesHelper.getInstance().getUserData() != null && PreferencesHelper.getInstance().getUserData().isSupperAccount;
    }

    private final class DeviceInfoListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (MainPresenter.DeviceInfoListener.class) {
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
                synchronized (MainPresenter.DeviceInfoListener.class) {
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

    private final class DeviceAlarmDisplayStatusListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (MainPresenter.DeviceInfoListener.class) {
                    for (Object arg : args) {
                        if (arg instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) arg;
                            String json = jsonObject.toString();
                            LogUtils.loge(this, "DeviceAlarmDisplayStatusListener json = " + json);
                            if (!isSupperAccount()) {
                                try {
                                    DeviceAlarmLogInfo deviceAlarmLogInfo = RetrofitServiceHelper.INSTANCE.getGson().fromJson(json, DeviceAlarmLogInfo.class);
                                    String event = deviceAlarmLogInfo.getEvent();
                                    EventAlarmStatusModel eventAlarmStatusModel = new EventAlarmStatusModel();
                                    eventAlarmStatusModel.deviceAlarmLogInfo = deviceAlarmLogInfo;
                                    switch (event) {
                                        case "create":
                                            // 做一些预警发生的逻辑
                                            eventAlarmStatusModel.status = MODEL_ALARM_STATUS_EVENT_CODE_CREATE;
                                            break;
                                        case "recovery":
                                            // 做一些预警恢复的逻辑
                                            eventAlarmStatusModel.status = MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY;
                                            break;
                                        case "confirm":
                                            // 做一些预警被确认的逻辑
                                            eventAlarmStatusModel.status = MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM;
                                            break;
                                        case "reconfirm":
                                            // 做一些预警被再次确认的逻辑
                                            eventAlarmStatusModel.status = MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM;
                                            break;
                                        default:
                                            // 未知逻辑 可以联系我确认 有可能是bug
                                            break;
                                    }
                                    EventData eventData = new EventData();
                                    eventData.code = EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS;
                                    eventData.data = eventAlarmStatusModel;
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

    private final class TaskRunnable implements Runnable {

        @Override
        public void run() {
            //检查网络状态
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final boolean ping = NetWorkUtils.ping();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ping) {
                                getView().toastShort("似乎断开了网路连接");
                            }
                        }
                    });
                }
            });
            //检查更新
//            Beta.checkUpgrade(false, false);
            if (!isSupperAccount()) {
                createSocket();
            }
        }
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
            mSocket.on(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    //没有登录跳转登录界面
    private void openLogin() {
        Intent loginIntent = new Intent();
        loginIntent.setClass(mContext, LoginActivity.class);
        getView().startAC(loginIntent);
        getView().finishAc();
    }

    /**
     * 简单判断账户类型
     */
    private void freshAccountType() {
        if (isSupperAccount()) {
            getView().setRbChecked(R.id.ac_main_rb_manage);
            getView().setSuperAccount(true);
        } else {
            getView().setRbChecked(R.id.ac_main_rb_main);
            getView().setSuperAccount(false);
        }
    }

    public void changeAccount(EventLoginData eventLoginData) {
        //
        PreferencesHelper.getInstance().saveUserData(eventLoginData);
//        getView().showAccountInfo(mEventLoginData.userName, mEventLoginData.phone);
//        if (indexFragment != null) {
        if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {
            getView().setSuperAccount(true);
            getView().setRbChecked(R.id.ac_main_rb_manage);
//                merchantSwitchFragment.requestDataByDirection(DIRECTION_DOWN, true);
        } else {
            getView().setSuperAccount(false);
            getView().setRbChecked(R.id.ac_main_rb_main);
            freshAlarmCount();
        }
//            merchantSwitchFragment.refreshData(mEventLoginData.userName, mEventLoginData.phone, mEventLoginData.phoneId);
        //
//            getView().updateMenuPager(MenuPageFactory.createMenuPageList(mEventLoginData));
//            getView().setCurrentPagerItem(0);
//            getView().setMenuSelected(0);
        reconnect();
//        }

    }

    private void reconnect() {
        try {
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.off(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket.off(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
                mSocket = null;
            }
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.on(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
            mSocket.on(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
            mSocket.off(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            mSocket = null;
        }
        mFragmentList.clear();
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
//            if (contractFragment != null) {
//                contractFragment.requestDataByDirection(DIRECTION_DOWN, false);
//            }
        } else if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            if (data != null && data instanceof DeviceInfo) {
                refreshDeviceInfo((DeviceInfo) data);
            }
            getView().setRbChecked(R.id.ac_main_rb_main);
        } else if (code == EVENT_DATA_SEARCH_MERCHANT) {
            if (data != null && data instanceof EventLoginData) {
                EventLoginData eventLoginData = (EventLoginData) data;
                changeAccount(eventLoginData);
            }
        }
//        else if (code == EVENT_DATA_ALARM_TOTAL_COUNT) {
//            if (data != null && data instanceof Integer) {
//                getView().setAlarmWarnCount((Integer) data);
//            }
//        }
        else if (code == EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS) {
            if (data != null && data instanceof EventAlarmStatusModel) {
                switch (((EventAlarmStatusModel) data).status) {
                    case MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
                    case MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                        freshAlarmCount();
                        break;
                    default:
                        break;
                }

            }
        }
//        LogUtils.loge(this, eventData.toString());
    }

    private void freshAlarmCount() {
        if (isSupperAccount()) {
            return;
        }
        String[] str = {"0"};
        RetrofitServiceHelper.INSTANCE.getAlarmCount(null, null, str, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AlarmCountRsp>(this) {
            @Override
            public void onCompleted(AlarmCountRsp alarmCountRsp) {
                int count = alarmCountRsp.getCount();
                getView().setAlarmWarnCount(count);
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return false;
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            getView().toastShort(mContext.getResources().getString(R.string.exit_main));
            exitTime = System.currentTimeMillis();
        } else {
            getView().finishAc();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO 对照片信息统一处理
        AlarmPopUtils.handlePhotoIntent(requestCode, resultCode, data);
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
