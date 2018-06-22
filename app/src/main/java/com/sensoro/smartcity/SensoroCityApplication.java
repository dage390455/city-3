package com.sensoro.smartcity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.fengmap.android.FMMapSDK;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.push.SensoroPushListener;
import com.sensoro.smartcity.push.SensoroPushManager;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.NotificationUtils;
import com.sensoro.smartcity.util.Repause;
import com.sensoro.smartcity.widget.SensoroToast;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler, Repause
        .Listener, SensoroPushListener {

    private final List<DeviceInfo> mDeviceInfoList = new ArrayList<>();
    public IWXAPI api;
    private static volatile SensoroCityApplication instance;
    public int saveSearchType = Constants.TYPE_DEVICE_NAME;
    private NotificationUtils mNotificationUtils;
    private static boolean isAPPBack = true;
    private static PushHandler pushHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not initView your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public static SensoroCityApplication getInstance() {
        return instance;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(base);
//    }

    public void addData(List<DeviceInfo> list) {
        this.mDeviceInfoList.addAll(list);
    }

    public void setData(List<DeviceInfo> list) {
        this.mDeviceInfoList.clear();
        this.mDeviceInfoList.addAll(list);

    }

    /**
     * 在登录界面不推送
     *
     * @return
     */
    private boolean isNeedPush() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return !name.equals(LoginActivity.class.getName());
    }

    public List<DeviceInfo> getData() {
        return mDeviceInfoList;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDeviceInfoList.clear();
    }

    void init() {
        if (pushHandler == null) {
            pushHandler = new PushHandler();
        }
        SensoroPushManager.getInstance().registerSensoroPushListener(this);
        Repause.init(this);
        Repause.registerListener(this);
        mNotificationUtils = new NotificationUtils(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        FMMapSDK.init(this);
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        SensoroToast.makeText(SensoroCityApplication.this, "程序出错：" + t.getId() + "," + e.getMessage(),
                Toast
                        .LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onApplicationResumed() {
        isAPPBack = false;
    }

    @Override
    public void onApplicationPaused() {
        isAPPBack = true;
    }

    public static void sendMessage(Message msg) {
        pushHandler.sendMessage(msg);
    }

    @Override
    public void onPushCallBack(String message) {
        LogUtils.loge(this, "pushNotification---isAPPBack = " + isAPPBack);
        if (isAPPBack) {
            mNotificationUtils.sendNotification(message);
        }
    }

    /**
     * 用handler
     */
    public static class PushHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof String) {
                String message = (String) msg.obj;
                SensoroPushListener sensoroPushListener = SensoroPushManager.getInstance().getSensoroPushListener();
                if (sensoroPushListener != null) {
                    sensoroPushListener.onPushCallBack(message);
                }
            }
        }
    }
}
