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
import com.sensoro.smartcity.fragment.MalfunctionFragment;
import com.sensoro.smartcity.fragment.ManagerFragment;
import com.sensoro.smartcity.fragment.WarnFragment;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.AlarmDeviceCountsBean;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.NetWorkUtils;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceAlarmCount;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.MonitorPointOperationTaskResultInfo;
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
    private final DeviceTaskResultListener mTaskResultListener = new DeviceTaskResultListener();
    private final Handler mHandler = new Handler();
    private final MainPresenter.TaskRunnable mRunnable = new MainPresenter.TaskRunnable();
    //
    private WarnFragment warnFragment;
    private HomeFragment homeFragment;
    private ManagerFragment managerFragment;
    private MalfunctionFragment malfunctionFragment;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        //提前获取一次
        if (PreferencesHelper.getInstance().getLocalDevicesMergeTypes() == null) {
            openLogin();
            return;
        }
        initViewPager();
    }

    private void initViewPager() {
        //
        homeFragment = new HomeFragment();
        warnFragment = new WarnFragment();
        managerFragment = new ManagerFragment();
        malfunctionFragment = new MalfunctionFragment();
        if (mFragmentList.size() > 0) {
            mFragmentList.clear();
        }
        mFragmentList.add(homeFragment);
        mFragmentList.add(warnFragment);
        mFragmentList.add(malfunctionFragment);
        mFragmentList.add(managerFragment);
        getView().updateMainPageAdapterData(mFragmentList);
        //
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
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createSocket();
                }
            }, 2000);
            freshAlarmCount();
        } else {
            openLogin();
        }
    }

    private boolean hasDeviceBriefControl() {
        return PreferencesHelper.getInstance().getUserData().hasDeviceBrief;
    }

    private boolean hasAlarmInfoControl() {
        return PreferencesHelper.getInstance().getUserData().hasAlarmInfo;
    }

    private boolean hasMalfunctionControl() {
        return PreferencesHelper.getInstance().getUserData().hasMalfunction;
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
                            LogUtils.loge(this, "socket-->>> DeviceInfoListener jsonArray = " + json);
                            if (hasDeviceBriefControl()) {
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
                            LogUtils.loge(this, "socket-->>> DeviceAlarmCountListener jsonArray = " + json);
                            if (hasDeviceBriefControl()) {
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
                            LogUtils.loge(this, "socket-->>> DeviceAlarmDisplayStatusListener json = " + json);
                            if (hasAlarmInfoControl()) {
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

    private final class DeviceTaskResultListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (MainPresenter.DeviceInfoListener.class) {
                    for (Object arg : args) {
                        if (arg instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) arg;
                            String json = jsonObject.toString();
                            LogUtils.loge(this, "socket-->>> DeviceTaskResultListener json = " + json);
                            MonitorPointOperationTaskResultInfo monitorPointOperationTaskResultInfo = RetrofitServiceHelper.INSTANCE.getGson().fromJson(json, MonitorPointOperationTaskResultInfo.class);
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT;
                            eventData.data = monitorPointOperationTaskResultInfo;
                            EventBus.getDefault().post(eventData);
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
            //检查网络状态和app更新
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final boolean ping = NetWorkUtils.ping();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!ping) {
                                getView().toastShort(mContext.getString(R.string.disconnected_from_network));
                            }
                            Beta.checkUpgrade(false, false);
                            mHandler.postDelayed(mRunnable, 5 * 1000);
                            LogUtils.loge("TaskRunnable == ping = " + ping + ",检查更新");
                        }
                    });
                }
            });
        }
    }

    private void createSocket() {
        try {
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            options.path = "/city";
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL + "app", options);
            if (hasDeviceBriefControl()) {
                mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.on(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket.on(SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
            }
            if (hasAlarmInfoControl()) {
                mSocket.on(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            }
            if (hasAlarmInfoControl() || hasDeviceBriefControl()) {
                mSocket.connect();
            }
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
        if (hasDeviceBriefControl()) {
            getView().setHasDeviceBriefControl(true);
            getView().setBottomBarSelected(0);
            getView().setHasAlarmInfoControl(hasAlarmInfoControl());
            getView().setHasMalfunctionControl(hasMalfunctionControl());
        } else {
            getView().setHasDeviceBriefControl(false);
            if (hasAlarmInfoControl()) {
                getView().setBottomBarSelected(1);
                getView().setHasAlarmInfoControl(true);
                getView().setHasMalfunctionControl(hasMalfunctionControl());
            } else {
                getView().setHasAlarmInfoControl(false);
                if (hasMalfunctionControl()) {
                    getView().setBottomBarSelected(2);
                    getView().setHasMalfunctionControl(true);
                } else {
                    getView().setHasMalfunctionControl(false);
                    getView().setBottomBarSelected(3);
                }

            }
        }

    }

    private void changeAccount(EventLoginData eventLoginData) {
        //
        PreferencesHelper.getInstance().saveUserData(eventLoginData);
        freshAccountType();
        if (hasAlarmInfoControl()) {
            freshAlarmCount();
        }
        reconnect();
    }

    private void reconnect() {
        try {
            if (mSocket != null) {
                mSocket.disconnect();
                if (hasDeviceBriefControl()) {
                    mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                    mSocket.off(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                    mSocket.off(SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
                }
                if (hasAlarmInfoControl()) {
                    mSocket.off(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
                }

                mSocket = null;
            }
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            //
            options.query = "session=" + sessionId;
            options.forceNew = true;
            options.path = "/city";
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL + "app", options);
            if (hasDeviceBriefControl()) {
                mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.on(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket.off(SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
            }
            if (hasAlarmInfoControl()) {
                mSocket.on(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            }
            if (hasAlarmInfoControl() || hasDeviceBriefControl()) {
                mSocket.connect();
            }
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
            if (hasDeviceBriefControl()) {
                mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.off(SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
            }
            if (hasAlarmInfoControl()) {
                mSocket.off(SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            }
            mSocket = null;
        }
        mFragmentList.clear();
        LogUtils.loge(this, "onDestroy");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SESSION_ID_OVERTIME:
                RetrofitServiceHelper.INSTANCE.cancelAllRsp();
                openLogin();
                break;
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().setBottomBarSelected(0);
                break;
            case EVENT_DATA_SEARCH_MERCHANT:
                if (data instanceof EventLoginData) {
                    EventLoginData eventLoginData = (EventLoginData) data;
                    changeAccount(eventLoginData);
                }
                break;
            case EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS:
                if (data instanceof EventAlarmStatusModel) {
                    switch (((EventAlarmStatusModel) data).status) {
                        case MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
                        case MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                            freshAlarmCount();
                            break;
                        default:
                            break;
                    }

                }
                break;
        }
    }

    private void freshAlarmCount() {
        if (!hasAlarmInfoControl()) {
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
                getView().setAlarmWarnCount(0);
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getView().isHomeFragmentChecked()) {
                if (homeFragment.onBackPressed()) {
                    return true;
                }
            }
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
        // 对照片信息统一处理
        AlarmPopUtils.handlePhotoIntent(requestCode, resultCode, data);
        if (managerFragment != null) {
            managerFragment.handlerActivityResult(requestCode, resultCode, data);
        }
    }
}
