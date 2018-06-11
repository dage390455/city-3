package com.sensoro.smartcity;

import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.fengmap.android.FMMapSDK;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.NotificationUtils;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends MultiDexApplication {

    private final List<DeviceInfo> mDeviceInfoList = new ArrayList<>();
    public IWXAPI api;
    private static volatile SensoroCityApplication instance;
    public int saveSearchType = Constants.TYPE_DEVICE_NAME;
    private NotificationUtils mNotificationUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Toast.makeText(SensoroCityApplication.this, "程序出错：" + thread.getId() + "," + ex.getMessage(), Toast
                        .LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

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
        mNotificationUtils = new NotificationUtils(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        FMMapSDK.init(this);
    }
    public void pushNotification(String message) {
        mNotificationUtils.sendNotification(message);
    }
}
