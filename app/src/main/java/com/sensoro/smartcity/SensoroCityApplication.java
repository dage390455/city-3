package com.sensoro.smartcity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.push.SensoroPushListener;
import com.sensoro.smartcity.push.SensoroPushManager;
import com.sensoro.smartcity.server.ISmartCityServer;
import com.sensoro.smartcity.server.SmartCityServerImpl;
import com.sensoro.smartcity.server.bean.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends MultiDexApplication implements SensoroPushListener{

    public ISmartCityServer smartCityServer;
    public static PushHandler pushHandler = new PushHandler();
    private List<DeviceInfo> mDeviceInfoList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

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
        smartCityServer = SmartCityServerImpl.getInstance(getApplicationContext());
        SensoroPushManager.getInstance().registerSensoroPushListener(this);
    }


    public void showNotify(String message) {
        NotificationManager nom = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        //注意：通知一定要有图标，没有设置图标的通知，通知不显示
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Sensoro City")
                .setContentText(message)
                .setSmallIcon(R.drawable.push)//设置图标
                .setContentIntent(pi)
                .build();
        nom.notify(1, notification);


    }

    @Override
    public void onPushCallBack(String message) {
        showNotify(message);
    }

    public static class PushHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            String data = (String)msg.obj;
            SensoroPushListener sensoroPushListener = SensoroPushManager.getInstance().getSensoroPushListener();
            if (sensoroPushListener != null) {
                sensoroPushListener.onPushCallBack(data);
            }
        }
    }


}
