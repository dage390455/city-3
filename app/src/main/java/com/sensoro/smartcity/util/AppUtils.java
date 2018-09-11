package com.sensoro.smartcity.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        //存储所有已安装程序的包名
        List<String> pName = new ArrayList<>();
        //从info中将报名字逐一取出
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public static boolean isActivityTop(Context context, Class<?> activityClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(activityClass.getName());
    }
}
