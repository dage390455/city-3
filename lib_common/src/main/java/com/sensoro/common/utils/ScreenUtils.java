package com.sensoro.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

import static android.view.View.NO_ID;

public class ScreenUtils {
    public static int heightNavBarNotExisted;
    public static int heightNavBarExisted;

    //    以下代码为判断底部导航的高度问题
//获取底部导航的高度
    public static int getBottomStatusHeight(Context context) {

        int totalHeight = getRealScreenHeight(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight - contentHeight;
    }


    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    public static int getRealScreenHeight(Context context) {
        int realHeight = 0;
        WindowManager windowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            realHeight = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();

        }

        return realHeight > 0 ? realHeight : getScreenHeight(context);
    }

    //获取屏幕高度 不包含虚拟按键=
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }


    private static final String NAVIGATION = "navigationBarBackground";

    // 该方法需要在View完全被绘制出来之后调用，否则判断不了
    //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
    public static boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != NO_ID && NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId())) && vp.getChildAt(i).getVisibility() == View.VISIBLE) {
                    return true;
                }
            }
        }
        return false;
    }


    public abstract static class OnNavigationStateListener {
        public abstract void onNavigationState(boolean isShowing, int height);
    }


    public static void isNavigationBarExist(Activity activity, final OnNavigationStateListener onNavigationStateListener) {
        if (activity == null) {
            return;
        }
        View rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (onNavigationStateListener != null) {
                    boolean isShown = checkNavigationBarShow(activity, activity.getWindow());

                    onNavigationStateListener.onNavigationState(isShown, 0);
                }
            }
        });

    }


    public static boolean checkNavigationBarShow(@NonNull Context context, @NonNull Window window) {
        boolean show;
        Display display = window.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);

        View decorView = window.getDecorView();
        Configuration conf = context.getResources().getConfiguration();
        if (Configuration.ORIENTATION_LANDSCAPE == conf.orientation) {
            View contentView = decorView.findViewById(android.R.id.content);
            show = (point.x != contentView.getWidth());
        } else {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            show = (rect.bottom != point.y);
        }
        return show;
    }


    public static int getNavigationBarHeight(Activity mActivity) {

        Resources resources = mActivity.getResources();

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelSize(resourceId);


        return height;

    }


    public int getStatusBarHeight(Activity mActivity) {

        Resources resources = mActivity.getResources();

        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");

        int height = resources.getDimensionPixelSize(resourceId);


        return height;

    }

}
