package com.sensoro.smartcity.server;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmTimeRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.DeviceHistoryListRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;
import com.sensoro.smartcity.server.response.LoginRsp;
import com.sensoro.smartcity.server.response.QiNiuToken;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.StationInfoRsp;
import com.sensoro.smartcity.server.response.UpdateRsp;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

import static com.sensoro.smartcity.server.RetrofitService.SCOPE_MASTER;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_MOCHA;

public enum RetrofitServiceHelper {
    INSTANCE;
    private static final long DEFAULT_TIMEOUT = 8 * 1000;
    private final String HEADER_SESSION_ID = "x-session-id";
    private final String HEADER_USER_AGENT = "User-Agent";
    private final String HEADER_CONTENT_TYPE = "Content-Type";
    private final String HEADER_ACCEPT = "Accept";


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    private String sessionId = null;

    public volatile String BASE_URL = SCOPE_MASTER;//http://mocha-iot-api.mocha.server.sensoro.com-----http://iot-api
    private RetrofitService retrofitService;
    private final Retrofit.Builder builder;

    public Gson getGson() {
        return gson;
    }

    private final Gson gson;

    RetrofitServiceHelper() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(double.class, new NumberDeserializer())
                .registerTypeAdapter(int.class, new NumberDeserializer())
                .registerTypeAdapter(float.class, new NumberDeserializer())
                .registerTypeAdapter(long.class, new NumberDeserializer())
                .registerTypeAdapter(short.class, new NumberDeserializer())
                .registerTypeAdapter(Number.class, new NumberDeserializer());
        gson = gsonBuilder.create();
        //支持RxJava
        builder = new Retrofit.Builder().baseUrl(BASE_URL).client(getNewClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        retrofitService = builder.build().create(RetrofitService.class);
    }


    private OkHttpClient getNewClient() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String s) {
                LogUtils.loge(this, "retrofit------------>" + s);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        final Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                //header
                builder.headers(original.headers())
                        .addHeader(HEADER_USER_AGENT, "Android/" +
                                Build.VERSION.RELEASE)
                        .addHeader(HEADER_ACCEPT, "application/json")
                        .addHeader(HEADER_CONTENT_TYPE, "application/json;charset=UTF-8");
                if (!TextUtils.isEmpty(sessionId)) {
                    builder.addHeader(HEADER_SESSION_ID, sessionId);
                }
                //
                builder.method(original.method(), original.body());
                Request request = builder.build();
                //
                Response response = chain.proceed(request);
                //重定向
//                boolean redirect = response.isRedirect();
                int code = response.code();
                try {
//                    if (redirect && (code == 308 || code == 307)) {
                    //仅针对308和307重定向问题
                    if (code == 308 || code == 307) {
                        String location = response.header("Location");
                        if (location.startsWith("/")) {
                            location = location.substring(1);
                        }
                        Request newRequest = request.newBuilder().url(BASE_URL + location)
                                .build();
                        response = chain.proceed(newRequest);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }
        };
        final OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        return builder
                .addInterceptor(interceptor)
                .addNetworkInterceptor(logging)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }


    public void setDemoTypeBaseUrl(boolean isDemo) {
        BASE_URL = isDemo ? SCOPE_MOCHA : SCOPE_MASTER;
        retrofitService = builder.baseUrl(BASE_URL).build().create(RetrofitService.class);
    }

    /**
     * 登录
     *
     * @param account
     * @param pwd
     * @param phoneId
     * @return
     */
    public Observable<LoginRsp> login(String account, String pwd, String phoneId) {
        return retrofitService.login(account, pwd, phoneId, "android");
    }

    /**
     * 登出
     *
     * @param phoneId
     * @param uid
     * @return
     */
    public Observable<ResponseBase> logout(String phoneId, String uid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneId", phoneId);
            jsonObject.put("uid", uid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.logout(phoneId, uid, phoneId, uid, body);
    }

    /**
     * 获取预警信息日志
     *
     * @param page
     * @param sn
     * @param deviceName
     * @param phone
     * @param beginTime
     * @param endTime
     * @param unionTypes
     * @return
     */
    public Observable<DeviceAlarmLogRsp> getDeviceAlarmLogList(int page, String sn, String deviceName, String phone,
                                                               Long beginTime, Long endTime, String unionTypes) {
        return retrofitService.getDeviceAlarmLogList(10, page, sn, deviceName, phone, beginTime, endTime, unionTypes);
    }

    /**
     * 获取升级信息
     *
     * @return
     */
    public Observable<UpdateRsp> getUpdateInfo() {
        return retrofitService.getUpdateInfo();
    }

    /**
     * 主页传感器信息
     *
     * @param page
     * @param sensorTypes
     * @param status
     * @param search
     * @return
     */
    public Observable<DeviceInfoListRsp> getDeviceBriefInfoList(int page, String sensorTypes, Integer status, String
            search) {
        return retrofitService.getDeviceBriefInfoList(page, 20, 1, 1,
                sensorTypes, status, search);
    }

    /**
     * 主页top信息
     *
     * @return
     */
    public Observable<DeviceTypeCountRsp> getDeviceTypeCount() {
        return retrofitService.getDeviceTypeCount();
    }

    /**
     * 获取账户列表
     *
     * @param search
     * @return
     */
    public Observable<UserAccountRsp> getUserAccountList(String search) {
        return retrofitService.getUserAccountList(search, "100000");
    }

    /**
     * 账户切换
     *
     * @param uid
     * @param phoneId
     * @return
     */
    public Observable<UserAccountControlRsp> doAccountControl(String uid, String phoneId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneId", phoneId);
            jsonObject.put("phoneType", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        application/json;charset=UTF-8
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doAccountControl(uid, body);
    }

    /**
     * 上传部署信息数据
     *
     * @param sn
     * @param lon
     * @param lat
     * @param tags
     * @param name
     * @param contact
     * @param content
     * @return
     */
    public Observable<DeviceDeployRsp> doDevicePointDeploy(String sn, double lon, double lat, String tags, String
            name, String contact,
                                                           String content) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            if (tags != null) {
                if (BASE_URL.equals(SCOPE_MASTER)) {
                    jsonObject.put("tags", tags);
                } else {
                    String[] split = tags.split(",");
                    JSONArray jsonArray = new JSONArray();
                    for (String temp : split) {
                        jsonArray.put(temp);
                    }
                    jsonObject.put("tags", jsonArray);
                }
            }
            if (name != null) {
                jsonObject.put("name", name);
            }
            if (contact != null) {
                jsonObject.put("contact", contact);
            }
            if (contact != null) {
                jsonObject.put("content", content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doDevicePointDeploy(sn, body);
    }

    public Observable<StationInfoRsp> doStationDeploy(String sn, double lon, double lat, String tags, String
            name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            if (tags != null) {
                String[] split = tags.split(",");
                JSONArray jsonArray = new JSONArray();
                for (String temp : split) {
                    jsonArray.put(temp);
                }
                jsonObject.put("tags", jsonArray);
            }
            if (name != null) {
                jsonObject.put("name", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doStationDeploy(sn, body);
    }

    /**
     * 修改提交确认预警信息备注
     *
     * @param id
     * @param status
     * @param remark
     * @return
     */
    public Observable<DeviceAlarmItemRsp> doAlarmConfirm(String id, int status, String remark, boolean isReconfirm) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("displayStatus", status);
            jsonObject.put("remark", remark);
            jsonObject.put("source", "app");
            if (isReconfirm) {
                jsonObject.put("type", "reconfirm");
            } else {
                jsonObject.put("type", "confirm");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doAlarmConfirm(id, body);
    }

    /**
     * 查找部署时间
     *
     * @param sn
     * @return
     */
    public Observable<DeviceAlarmTimeRsp> getDeviceAlarmTime(String sn) {
        return retrofitService.getDeviceAlarmTime(sn);
    }

    public Observable<DeviceHistoryListRsp> getDeviceHistoryList(String sn, int count) {
        return retrofitService.getDeviceHistoryList(sn, count);
    }

    /**
     * 获取设备日志信息
     *
     * @param sn
     * @param startTime
     * @param endTime
     * @return
     */
    public Observable<DeviceRecentRsp> getDeviceHistoryList(String sn, long startTime, long endTime) {
        return retrofitService.getDeviceHistoryList(sn, startTime, endTime, "hours");
    }

    /**
     * 获取设备信息
     *
     * @param sns
     * @param search
     * @param all
     * @return
     */
    public Observable<DeviceInfoListRsp> getDeviceDetailInfoList(String sns, String search, int all) {
        return retrofitService.getDeviceDetailInfoList(sns, search, all);
    }

    /**
     * 返回单个基站详情
     *
     * @param sn
     * @return
     */
    public Observable<StationInfoRsp> getStationDetail(String sn) {
        return retrofitService.getStationDetail(sn);
    }


    /**
     * 修改提交确认预警信息备注(图片)
     *
     * @param id
     * @param remark
     * @return
     */
    public Observable<DeviceAlarmItemRsp> doUpdatePhotosUrl(String id, int statusResult, int statusType, int
            statusPlace, String remark, boolean isReconfirm, List<String> imagesUrl) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("displayStatus", statusResult);
            jsonObject.put("reason", statusType);
            jsonObject.put("place", statusPlace);
            if (!TextUtils.isEmpty(remark)){
                jsonObject.put("remark", remark);
            }
            jsonObject.put("source", "app");
            if (isReconfirm) {
                jsonObject.put("type", "reconfirm");
            } else {
                jsonObject.put("type", "confirm");
            }
            //
            if (imagesUrl != null) {
                JSONArray jsonArray = new JSONArray();
                for (String url : imagesUrl) {
                    jsonArray.put(url);
                }
                jsonObject.put("images", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doUpdatePhotosUrl(id, body);
    }

    /**
     * 获取七牛token
     *
     * @return
     */
    public Observable<QiNiuToken> getQiNiuToken() {
        return retrofitService.getQiNiuToken();
    }
}
