package com.sensoro.smartcity.temp.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sensoro.smartcity.temp.CameraTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yexiaokang on 2019/4/2.
 */
@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class HeaderInterceptor implements Interceptor {

    private static final String AUTHORIZATION = "Authorization";

    private Map<String, String> mHeaders;
    private Context mContext;

    public HeaderInterceptor(Context context) {
        mHeaders = new HashMap<>();
        mContext = context.getApplicationContext();
    }

    public HeaderInterceptor(Map<String, String> headers, Context context) {
        mHeaders = headers;
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        if (mHeaders != null) {
            mHeaders.put("User-Agent", "Android");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            mHeaders.put(AUTHORIZATION, sp.getString(CameraTest.KEY_TOKEN, ""));
            Set<String> keys = mHeaders.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, mHeaders.get(headerKey));
            }
        }
        return chain.proceed(builder.build());
    }
}
