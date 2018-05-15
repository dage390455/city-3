package com.sensoro.smartcity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.fengmap.android.FMMapSDK;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.push.SensoroPushListener;
import com.sensoro.smartcity.push.SensoroPushManager;
import com.sensoro.smartcity.server.SmartCityServerImpl;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.NotificationUtils;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends MultiDexApplication implements SensoroPushListener, Serializable {

    public volatile SmartCityServerImpl smartCityServer;
    public final static PushHandler pushHandler = new PushHandler();
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
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public static SensoroCityApplication getInstance() {
        return instance;
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
        mNotificationUtils = new NotificationUtils(this);
        smartCityServer = SmartCityServerImpl.getInstance(getApplicationContext());
        SensoroPushManager.getInstance().registerSensoroPushListener(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        FMMapSDK.init(this);
    }


//    public void showNotify(String message) {
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.o) {
////            String channelId = "defaultChannel";
////            String channelName = "Channel Name";
////            String channelDescription = "Channel Description";
////            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager
/// .IMPORTANCE_DEFAULT);
////            channel.setDescription(channelDescription);
////
////            builder = new NotificationCompat.Builder(context, channelId);
////        } else {
////            builder = new NotificationCompat.Builder(context);
////        }
//        Log.e("", "showNotify----->" + message);
//        NotificationManager nom = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        //注意：通知一定要有图标，没有设置图标的通知，通知不显示
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("Sensoro City")
//                .setContentText(message)
//                .setSmallIcon(R.drawable.push)//设置图标
//                .setContentIntent(pi)
//                .setDefaults(Notification.DEFAULT_SOUND)
//                .setContentIntent(pi).setPriority(Notification.PRIORITY_MAX)
//                .build();
//        nom.notify(noID++, notification);
//
//
//    }

    @Override
    public void onPushCallBack(String message) {
        mNotificationUtils.sendNotification(message);
//        showNotify(message);
    }

    public static class PushHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            String data = (String) msg.obj;
            SensoroPushListener sensoroPushListener = SensoroPushManager.getInstance().getSensoroPushListener();
            if (sensoroPushListener != null) {
                sensoroPushListener.onPushCallBack(data);
            }
        }
    }

}
