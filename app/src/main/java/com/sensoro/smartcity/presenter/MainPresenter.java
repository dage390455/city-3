package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.igexin.sdk.PushManager;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.fragment.FireSecurityWarnFragment;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.manger.ActivityTaskManager;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.model.NetWorkStateModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.NetWorkUtils;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmDeviceCountsBean;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.DeviceAlarmCount;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.DeviceMergeTypesInfo;
import com.sensoro.common.server.bean.MonitorPointOperationTaskResultInfo;
import com.sensoro.common.server.response.AlarmCountRsp;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.fragment.HomeFragment;
import com.sensoro.smartcity.fragment.MalfunctionFragment;
import com.sensoro.smartcity.fragment.ManagerFragment;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.model.EventAlarmStatusModel;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainPresenter extends BasePresenter<IMainView> implements IOnCreate {

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private Activity mContext;
    //
    private long exitTime = 0;
    private volatile Socket mSocket = null;
    private final MainPresenter.DeviceInfoListener mInfoListener = new MainPresenter.DeviceInfoListener();
    private final MainPresenter.DeviceAlarmCountListener mAlarmCountListener = new MainPresenter.DeviceAlarmCountListener();
    private final DeviceAlarmDisplayStatusListener mAlarmDisplayStatusListener = new DeviceAlarmDisplayStatusListener();
    private final DeviceFlushListener mDeviceFlushListener = new DeviceFlushListener();
    private final DeviceTaskResultListener mTaskResultListener = new DeviceTaskResultListener();
    private final Handler mHandler = new Handler();
    private final MainPresenter.TaskRunnable mRunnable = new MainPresenter.TaskRunnable();
    private final NetWorkTaskRunnable mNetWorkTaskRunnable = new NetWorkTaskRunnable();
    private final FreshAlarmCountTask mFreshAlarmCountTaskRunnable = new FreshAlarmCountTask();
    //
    private FireSecurityWarnFragment warnFragment;
    private HomeFragment homeFragment;
    private ManagerFragment managerFragment;
    private MalfunctionFragment malfunctionFragment;
    private ScreenBroadcastReceiver mScreenReceiver;
    //默认应后后端要求，默认只能支持websocket协议
    private final String[] transports = {"websocket"};
    private boolean needFreshAlarmCount = true;

    private final class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case Intent.ACTION_USER_PRESENT:
                        final EventData eventData = new EventData();
                        eventData.code = Constants.EVENT_DATA_LOCK_SCREEN_ON;
                        eventData.data = true;
                        EventBus.getDefault().post(eventData);
                        break;
                    case Intent.ACTION_LOCALE_CHANGED:
                        try {
                            LogUtils.loge("Language : isChina = " + AppUtils.isChineseLanguage());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        reLogin(false);
                        break;
                    case CONNECTIVITY_ACTION:
                        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (manager != null) {
                            EventData netCanUseData = new EventData();
                            netCanUseData.code = Constants.NetworkInfo;
                            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                            if (activeNetwork != null) {
                                if (activeNetwork.isConnected()) {
                                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                                        //当前WiFi连接可用
                                        netCanUseData.data = ConnectivityManager.TYPE_WIFI;
                                        reconnect();
                                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                                        //当前移动网络连接可用
                                        netCanUseData.data = ConnectivityManager.TYPE_MOBILE;
                                        reconnect();
                                    }
                                } else {
                                    netCanUseData.data = -1;
                                    try {
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>当前没有网络连接，请确保你已经打开网络 ");
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>info.getTypeName()" + activeNetwork.getTypeName());
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>getSubtypeName()" + activeNetwork.getSubtypeName());
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>getState()" + activeNetwork.getState());
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>getDetailedState()"
                                                + activeNetwork.getDetailedState().name());
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>getDetailedState()" + activeNetwork.getExtraInfo());
                                        LogUtils.loge("CONNECTIVITY_ACTION--->>getType()" + activeNetwork.getType());
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                }

                            } else {   // 当前没有网络连接，请确保你已经打开网络
                                netCanUseData.data = -1;
                            }
                            EventBus.getDefault().post(netCanUseData);
                        }
                        break;

                }
            }
        }
    }

    /**
     * 检测到语言变化重新登录
     */
    private void reLogin(boolean isCrash) {
        if (isAttachedView()) {
            RetrofitServiceHelper.getInstance().cancelAllRsp();
            RetrofitServiceHelper.getInstance().clearLoginDataSessionId();
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (isCrash) {
                // 杀掉进程
                ActivityTaskManager.getInstance().AppExit(mContext);
            } else {
                // 清除activity
                ActivityTaskManager.getInstance().finishAllActivity();
            }
            mContext.startActivity(intent);
        }
    }

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        //提前获取一次
        if (PreferencesHelper.getInstance().getLocalDevicesMergeTypes() == null) {
            openLogin();
            return;
        }
        //保存一遍当前的版本信息
        PreferencesHelper.getInstance().saveCurrentVersionCode(AppUtils.getVersionCode(mContext));
//        if (true){
//            Intent intent = new Intent(mContext, RecyclerViewActivity.class);
//            getView().startAC(intent);
//            getView().finishAc();
//            return;
//        }
        //
        initViewPager();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //注册息屏事件广播
                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(Intent.ACTION_SCREEN_ON);
//                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                intentFilter.addAction(Intent.ACTION_USER_PRESENT);
                intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        CONNECTIVITY_CHANGE WIFI_STATE_CHANGED&STATE_CHANGE
                mScreenReceiver = new ScreenBroadcastReceiver();
                mContext.registerReceiver(mScreenReceiver, intentFilter);
            }
        }, 500);
        //每次初始化静默拉取一次预警弹窗的配置项
        RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
            @Override
            public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
                PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
            }
        });
    }

    private void initViewPager() {
        //
        homeFragment = new HomeFragment();
        warnFragment = new FireSecurityWarnFragment();
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
        final EventLoginData eventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra(Constants.EXTRA_EVENT_LOGIN_DATA);
        //
        if (null != eventLoginData) {
            //赋值
            try {
                LogUtils.loge("onDataEvent ---->>>" + eventLoginData.toString());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            PreferencesHelper.getInstance().saveUserData(eventLoginData);
            //显示账户信息
//            getView().showAccountInfo(mEventLoginData.userName, mEventLoginData.phone);
            freshAccountType();
            if (!PushManager.getInstance().isPushTurnedOn(ContextUtils.getContext())) {
                PushManager.getInstance().turnOnPush(ContextUtils.getContext());
            }
            mHandler.postDelayed(mRunnable, 3000L);
            mHandler.postDelayed(mNetWorkTaskRunnable, 10 * 1000L);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createSocket();
                }
            }, 1500);
            mHandler.post(mFreshAlarmCountTaskRunnable);
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
                if (hasDeviceBriefControl()) {
                    synchronized (MainPresenter.DeviceInfoListener.class) {
                        for (Object arg : args) {
                            if (arg instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) arg;
                                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String json = jsonObject.toString();
                                try {
                                    LogUtils.loge(this, "socket-->>> DeviceInfoListener jsonArray = " + json);
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }

                                try {
                                    DeviceInfo data = RetrofitServiceHelper.getInstance().getGson().fromJson(json,
                                            DeviceInfo.class);
                                    EventBus.getDefault().post(data);
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
                if (hasDeviceBriefControl()) {
                    synchronized (MainPresenter.DeviceAlarmCountListener.class) {
                        for (Object arg : args) {
                            if (arg instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) arg;
                                String json = jsonObject.toString();
                                try {
                                    LogUtils.loge(this, "socket-->>> DeviceAlarmCountListener jsonArray = " + json);
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                try {
                                    DeviceAlarmCount deviceAlarmCount = RetrofitServiceHelper.getInstance().getGson().fromJson(json, DeviceAlarmCount.class);
                                    List<DeviceAlarmCount.AllBean> all = deviceAlarmCount.getAll();
                                    DeviceAlarmCount.AllBean allBean = all.get(0);
                                    AlarmDeviceCountsBean alarmDeviceCountsBean = allBean.getCounts();
                                    EventBus.getDefault().post(alarmDeviceCountsBean);
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
                if (hasAlarmInfoControl()) {
                    synchronized (MainPresenter.DeviceAlarmDisplayStatusListener.class) {
                        for (Object arg : args) {
                            if (arg instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) arg;
                                String json = jsonObject.toString();
                                try {
                                    LogUtils.loge(this, "socket-->>> DeviceAlarmDisplayStatusListener json = " + json);
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                try {
                                    DeviceAlarmLogInfo deviceAlarmLogInfo = RetrofitServiceHelper.getInstance().getGson().fromJson(json, DeviceAlarmLogInfo.class);
                                    String event = deviceAlarmLogInfo.getEvent();
                                    EventAlarmStatusModel eventAlarmStatusModel = new EventAlarmStatusModel();
                                    eventAlarmStatusModel.deviceAlarmLogInfo = deviceAlarmLogInfo;
                                    switch (event) {
                                        case "create":
                                            // 做一些预警发生的逻辑
                                            eventAlarmStatusModel.status = Constants.MODEL_ALARM_STATUS_EVENT_CODE_CREATE;
                                            break;
                                        case "recovery":
                                            // 做一些预警恢复的逻辑
                                            eventAlarmStatusModel.status = Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY;
                                            break;
                                        case "confirm":
                                            // 做一些预警被确认的逻辑
                                            eventAlarmStatusModel.status = Constants.MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM;
                                            break;
                                        case "reconfirm":
                                            // 做一些预警被再次确认的逻辑
                                            eventAlarmStatusModel.status = Constants.MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM;
                                            break;
                                        default:
                                            // 未知逻辑 可以联系我确认 有可能是bug
                                            break;
                                    }
                                    EventBus.getDefault().post(eventAlarmStatusModel);
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

    private final class DeviceFlushListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (MainPresenter.DeviceFlushListener.class) {
                    //TODO 设备删除做的操作
                    EventBus.getDefault().post(Constants.EVENT_DATA_DEVICE_SOCKET_FLUSH);
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
                synchronized (MainPresenter.DeviceTaskResultListener.class) {
                    for (Object arg : args) {
                        if (arg instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject) arg;
                            String json = jsonObject.toString();
                            try {
                                LogUtils.loge(this, "socket-->>> DeviceTaskResultListener json = " + json);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            MonitorPointOperationTaskResultInfo monitorPointOperationTaskResultInfo = RetrofitServiceHelper.getInstance().getGson().fromJson(json, MonitorPointOperationTaskResultInfo.class);
                            EventBus.getDefault().post(monitorPointOperationTaskResultInfo);
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
            if (mSocket != null && !mSocket.connected()) {
                boolean reconnect = reconnect();
                try {
                    LogUtils.loge("mSocket  断开---->>> reconnect = " + reconnect);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            mHandler.postDelayed(mRunnable, 10 * 1000);
        }
    }

    private final class NetWorkTaskRunnable implements Runnable {

        @Override
        public void run() {
            //检查网络状态和app更新
            ThreadPoolManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean pingNetCanUse = NetWorkUtils.ping();
                        NetWorkStateModel netWorkStateModel = new NetWorkStateModel();
                        netWorkStateModel.ping = pingNetCanUse;
                        EventBus.getDefault().post(netWorkStateModel);
                        //TODO 暂时去掉频繁后台请求
//                            Beta.checkUpgrade(false, false);

                        LogUtils.loge("TaskRunnable == pingNetCanUse = " + pingNetCanUse + ",检查更新");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
                        mHandler.postDelayed(mNetWorkTaskRunnable, 30 * 1000);
                    }
                }
            });

        }

    }

    private void createSocket() {
        try {
            String sessionId = RetrofitServiceHelper.getInstance().getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            options.path = "/city";
            options.transports = transports;
            mSocket = IO.socket(RetrofitServiceHelper.getInstance().BASE_URL + "app", options);
            if (hasDeviceBriefControl()) {
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_FLUSH, mDeviceFlushListener);
            }
            if (hasAlarmInfoControl()) {
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
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

    private boolean reconnect() {
        try {
            if (mSocket != null) {
                mSocket.disconnect();
                if (hasDeviceBriefControl()) {
                    mSocket.off(Constants.SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                    mSocket.off(Constants.SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                    mSocket.off(Constants.SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
                    mSocket.off(Constants.SOCKET_EVENT_DEVICE_FLUSH, mDeviceFlushListener);
                }
                if (hasAlarmInfoControl()) {
                    mSocket.off(Constants.SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
                }

                mSocket = null;
            }
            String sessionId = RetrofitServiceHelper.getInstance().getSessionId();
            IO.Options options = new IO.Options();
            //
            options.query = "session=" + sessionId;
            options.forceNew = true;
            options.path = "/city";
            options.transports = transports;
            mSocket = IO.socket(RetrofitServiceHelper.getInstance().BASE_URL + "app", options);
            if (hasDeviceBriefControl()) {
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_FLUSH, mDeviceFlushListener);
            }
            if (hasAlarmInfoControl()) {
                mSocket.on(Constants.SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            }
            if (hasAlarmInfoControl() || hasDeviceBriefControl()) {
                return mSocket.connect().connected();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (mScreenReceiver != null) {
            mContext.unregisterReceiver(mScreenReceiver);
        }

        mHandler.removeCallbacksAndMessages(null);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mSocket != null) {
            mSocket.disconnect();
            if (hasDeviceBriefControl()) {
                mSocket.off(Constants.SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket.off(Constants.SOCKET_EVENT_DEVICE_ALARM_COUNT, mAlarmCountListener);
                mSocket.off(Constants.SOCKET_EVENT_DEVICE_TASK_RESULT, mTaskResultListener);
                mSocket.off(Constants.SOCKET_EVENT_DEVICE_FLUSH, mDeviceFlushListener);
            }
            if (hasAlarmInfoControl()) {
                mSocket.off(Constants.SOCKET_EVENT_DEVICE_ALARM_DISPLAY, mAlarmDisplayStatusListener);
            }
            mSocket = null;
        }
        mFragmentList.clear();
        try {
            LogUtils.loge(this, "onDestroy");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventAlarmStatusModel eventAlarmStatusModel) {
        switch (eventAlarmStatusModel.status) {
            case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CREATE:
            case Constants.MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM:
                needFreshAlarmCount = true;
                break;
            default:
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_SESSION_ID_OVERTIME:
                reLogin(false);
                break;
            case Constants.EVENT_DATA_APP_CRASH:
                //APP 崩溃
                try {
                    LogUtils.loge("app crash ------>>>> EVENT_DATA_APP_CRASH");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                reLogin(true);
                break;
            case Constants.EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().setBottomBarSelected(0);
                break;
            case Constants.EVENT_DATA_SEARCH_MERCHANT:
                if (data instanceof EventLoginData) {
                    EventLoginData eventLoginData = (EventLoginData) data;
                    changeAccount(eventLoginData);
                }
                break;
            case Constants.EVENT_DATA_CHECK_MERGE_TYPE_CONFIG_DATA:
                //mergeTypeConfig配置参数需要更新
                RetrofitServiceHelper.getInstance().getDevicesMergeTypes().subscribeOn(Schedulers.io()).doOnNext(new Consumer<ResponseResult<DeviceMergeTypesInfo>>() {
                    @Override
                    public void accept(ResponseResult<DeviceMergeTypesInfo> devicesMergeTypesRsp) throws Exception {
                        DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                        PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceMergeTypesInfo>>(MainPresenter.this) {
                    @Override
                    public void onCompleted(ResponseResult<DeviceMergeTypesInfo> devicesMergeTypesRsp) {
                        try {
                            LogUtils.loge("更新配置参数成功 .....");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        try {
                            LogUtils.loge("更新配置参数失败 -->>" + errorMsg);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetWorkStateModel netWorkStateModel) {
        if (netWorkStateModel != null) {
            if (!netWorkStateModel.ping) {
                if (!SensoroCityApplication.getInstance().isAPPBack) {
                    getView().toastShort(mContext.getString(R.string.disconnected_from_network));
                    try {
                        com.sensoro.common.utils.LogUtils.loge("CONNECTIVITY_ACTION msg");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }

    private void freshAlarmCount() {
        if (!hasAlarmInfoControl()) {
            needFreshAlarmCount = false;
            return;
        }
        String[] str = {"0"};

        RetrofitServiceHelper.getInstance().getAlarmCount(null, null, str, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AlarmCountRsp>(this) {
            @Override
            public void onCompleted(AlarmCountRsp alarmCountRsp) {
                int count = alarmCountRsp.getCount();
                getView().setAlarmWarnCount(count);
                needFreshAlarmCount = false;
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().setAlarmWarnCount(0);
            }
        });
    }

    private final class FreshAlarmCountTask implements Runnable {

        @Override
        public void run() {
            if (needFreshAlarmCount) {
                freshAlarmCount();
            }
            mHandler.postDelayed(mFreshAlarmCountTaskRunnable, 3000);
        }

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
    //----------------
    //TODO 暂时不通过检测wifi状态
//                    //        CONNECTIVITY_CHANGE WIFI_STATE_CHANGED&STATE_CHANGE
//                    // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager
//                    // .WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
//                    // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
//                    // 当然刚打开wifi肯定还没有连接到有效的无线
//                    case WifiManager.WIFI_STATE_CHANGED_ACTION:
//                        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//                        LogUtils.loge("wifiState" + wifiState);
//                        switch (wifiState) {
//                            case WifiManager.WIFI_STATE_DISABLED:
////                                APP.getInstance().setEnablaWifi(false);
//                                break;
//                            case WifiManager.WIFI_STATE_DISABLING:
//
//                                break;
//                            case WifiManager.WIFI_STATE_ENABLING:
//                                break;
//                            case WifiManager.WIFI_STATE_ENABLED:
////                                APP.getInstance().setEnablaWifi(true);
//                                break;
//                            case WifiManager.WIFI_STATE_UNKNOWN:
//                                break;
//                            default:
//                                break;
//                        }
//                        break;
//                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
//                        Parcelable parcelableExtra = intent
//                                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//                        if (null != parcelableExtra) {
//                            NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                            NetworkInfo.State state = networkInfo.getState();
//                            boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
//                            LogUtils.loge("isConnected" + isConnected);
//                            if (isConnected) {
////                                APP.getInstance().setWifi(true);
//                            } else {
////                                APP.getInstance().setWifi(false);
//                            }
//                        }
//                        break;
}
