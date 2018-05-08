package com.sensoro.smartcity.util;

import android.content.Context;
import android.support.annotation.DimenRes;

/**
 * Created by Mohammad Abbas on 5/10/2016.
 *
 * Update Jun/2016 :: delete methods - color and drawable can handler with ContextCompat
 */
public final class ResourceUtils {

    private ResourceUtils() throws InstantiationException {
        throw new InstantiationException("This utility class is created for instantiation");
    }

    public static float getDimension(Context context, @DimenRes int resourceId) {
        return context.getResources().getDimension(resourceId);
    }

    public static int getDimensionPixelSize(Context context, @DimenRes int resourceId) {
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
