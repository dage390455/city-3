package com.sensoro.smartcity.util;

import android.content.Context;

public class DpUtils {
    public static int dp2px(Context context,int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
