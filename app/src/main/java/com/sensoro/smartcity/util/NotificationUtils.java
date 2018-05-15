package com.sensoro.smartcity.util;

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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MainActivity;

//import android.app.NotificationChannel;

/**
 * Created by LaoZhao on 2017/11/19.
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";
    private volatile int noID = 1;
    private Class<?> aClass = MainActivity.class;

    public NotificationUtils(Context context) {
        super(context);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
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
//        final Intent intent = new Intent(Intent.ACTION_MAIN);
        final Intent intent = new Intent(this, aClass);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this, aClass));//用ComponentName得到class对象
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        final PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        return new Notification.Builder(getApplicationContext(), id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setContentTitle("Sensoro City")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi).setPriority(Notification.PRIORITY_MAX);
    }

    private NotificationCompat.Builder getNotification_25(String content) {
//        final Intent intent = new Intent(Intent.ACTION_MAIN);
        final Intent intent = new Intent(this, aClass);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(this, aClass));//用ComponentName得到class对象
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
        return new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setContentTitle("Sensoro City")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi).setPriority(Notification.PRIORITY_MAX);
    }

    public void sendNotification(String content) {
        if (isMainACLived()) {
            aClass = MainActivity.class;
        } else {
            aClass = LoginActivity.class;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (content).build();
            getManager().notify(noID++, notification);
        } else {
            Notification notification = getNotification_25(content).build();
            getManager().notify(noID++, notification);
        }
    }

    private boolean isMainACLived() {
        final String name = "com.sensoro.smartcity.activity/MainActivity";
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), name);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            return false;
        } else {
            return true;
        }
    }
}