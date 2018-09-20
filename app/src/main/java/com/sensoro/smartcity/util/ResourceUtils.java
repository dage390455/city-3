package com.sensoro.smartcity.util;

import android.content.Context;
import android.support.annotation.DimenRes;

import java.io.UnsupportedEncodingException;

/**
 * Created by Mohammad Abbas on 5/10/2016.
 * <p>
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

    public static int getByteFromWords(String words) {
        byte[] bytes;
        try {
            bytes = words.getBytes("UTF-8");
            return bytes.length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
