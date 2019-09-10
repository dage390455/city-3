package com.sensoro.smartcity.util;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.sensoro.common.utils.LogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.cameralibrary.util.LogUtil;

import java.util.List;

import static com.sensoro.smartcity.temp.RecyclerItemViewHolder.TAG;

//import android.app.NotificationChannel;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "SensoroCity";
    public static final String name = "SensoroCity";
    private static volatile int noID = 1;
//    private Class<?> aClass = MainActivity.class;

    public NotificationUtils(Context context) {
        super(context);

    }

    private static void deleteNoNumberNotification(NotificationManager nm, String newChannelId) {
        List<NotificationChannel> notificationChannels = nm.getNotificationChannels();
        if (notificationChannels == null || notificationChannels.size() < 5) {
            return;
        }
        for (NotificationChannel channel : notificationChannels) {
            if (channel.getId() == null || channel.getId().equals(newChannelId)) {
                continue;
            }

            int notificationNumbers = getNotificationNumbers(nm, channel.getId());
            LogUtil.d(TAG, "notificationNumbers: " + notificationNumbers + " channelId:" + channel.getId());
            if (notificationNumbers == 0) {
                Log.i(TAG, "deleteNoNumberNotification: " + channel.getId());
                nm.deleteNotificationChannel(channel.getId());
            }
        }
    }

    /**
     * 获取某个渠道下状态栏上通知显示个数
     *
     * @param mNotificationManager NotificationManager
     * @param channelId            String
     * @return int
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int getNotificationNumbers(NotificationManager mNotificationManager, String channelId) {
        if (mNotificationManager == null || TextUtils.isEmpty(channelId)) {
            return -1;
        }
        int numbers = 0;
        StatusBarNotification[] activeNotifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification item : activeNotifications) {
            Notification notification = item.getNotification();
            if (notification != null) {
                if (channelId.equals(notification.getChannelId())) {
                    numbers++;
                }
            }
        }
        return numbers;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.enableLights(true);
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            synchronized (NotificationUtils.class) {
                if (manager == null) {
                    manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                }
            }
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getChannelNotification(String content) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
//        final Intent intent = new Intent(this, aClass);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this, MainActivity.class));//用ComponentName得到class对象
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        final PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(false)
                .setContentTitle("Sensoro City")
                .setWhen(System.currentTimeMillis())
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_ALL | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(pi).setPriority(Notification.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    private NotificationCompat.Builder getNotification_25(String content) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
//        final Intent intent = new Intent(this, aClass);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this, MainActivity.class));//用ComponentName得到class对象
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);//将经过设置了的Intent绑定给PendingIntent
//        notification.contentIntent = contentIntent;// 通知绑定 PendingIntent
//        notification.flags=Notification.FLAG_AUTO_CANCEL;//设置自动取消
//        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(NOTIFY_ID, notification);


//        final Intent intent = new Intent(this, MainActivity.class);
//        final PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
        final PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(false)
                .setContentTitle("Sensoro City")
                .setWhen(System.currentTimeMillis())
                .setContentText(content)
                .setVibrate(new long[]{0, 100, 200, 300})
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_ALL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pi).setPriority(Notification.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    public void sendNotification(String content) {
//        if (isMainACLived()) {
//            aClass = MainActivity.class;
//        } else {
//            aClass = LoginActivity.class;
//        }
        //只在mainActivity推送通知
//        wakeUpAndUnlock(this);
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (content).build();
            getManager().notify(noID++, notification);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            try {
                LogUtils.loge("sendNotification -->> " + noID);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            Notification notification = getNotification_25(content).build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            getManager().notify(noID++, notification);
            try {
                LogUtils.loge("sendNotification -->> " + noID);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    //唤醒屏幕并解锁
    public static void wakeUpAndUnlock(Context context) {
        try {
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            //解锁
            kl.disableKeyguard();
            //获取电源管理器对象
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            //释放
            wl.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMainACLived() {
        final String name = "com.sensoro.smartcity.activity/MainActivity";
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), name);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo == null;
    }
}