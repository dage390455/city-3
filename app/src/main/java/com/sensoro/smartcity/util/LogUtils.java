package com.sensoro.smartcity.util;

import android.util.Log;

import com.sensoro.smartcity.BuildConfig;

public class LogUtils {
    private static boolean isShowLog = BuildConfig.DEBUG;
    private static String TAG = "sensoro_log";

    public static void loge(String msg) {
        if (isShowLog) {
            Log.e(TAG, msg);
        }
    }

    public static void logd(String msg) {
        if (isShowLog) {
            Log.d(TAG, msg);
        }
    }

    public static void loge(Object o, String msg) {
        if (isShowLog) {
            Log.e(TAG + "-->" + o.getClass().getSimpleName(), msg);
        }
    }

    public static void logd(Object o, String msg) {
        if (isShowLog) {
            Log.d(TAG + "-->" + o.getClass().getSimpleName(), msg);
        }
    }
}
