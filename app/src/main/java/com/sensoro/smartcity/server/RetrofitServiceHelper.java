package com.sensoro.smartcity.server;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.model.AlarmPopupDangerData;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.AlarmCountRsp;
import com.sensoro.smartcity.server.response.AuthRsp;
import com.sensoro.smartcity.server.response.ChangeInspectionTaskStateRsp;
import com.sensoro.smartcity.server.response.ContractAddRsp;
import com.sensoro.smartcity.server.response.ContractInfoRsp;
import com.sensoro.smartcity.server.response.ContractsListRsp;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
import com.sensoro.smartcity.server.response.DeployDeviceDetailRsp;
import com.sensoro.smartcity.server.response.DeployRecordRsp;
import com.sensoro.smartcity.server.response.DeployStationInfoRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmTimeRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.DeviceHistoryListRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.server.response.DeviceStatusRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;
import com.sensoro.smartcity.server.response.DeviceUpdateFirmwareDataRsp;
import com.sensoro.smartcity.server.response.DevicesAlarmPopupConfigRsp;
import com.sensoro.smartcity.server.response.DevicesMergeTypesRsp;
import com.sensoro.smartcity.server.response.InspectionTaskDeviceDetailRsp;
import com.sensoro.smartcity.server.response.InspectionTaskExceptionDeviceRsp;
import com.sensoro.smartcity.server.response.InspectionTaskExecutionRsp;
import com.sensoro.smartcity.server.response.InspectionTaskInstructionRsp;
import com.sensoro.smartcity.server.response.InspectionTaskModelRsp;
import com.sensoro.smartcity.server.response.LoginRsp;
import com.sensoro.smartcity.server.response.MalfunctionCountRsp;
import com.sensoro.smartcity.server.response.MalfunctionListRsp;
import com.sensoro.smartcity.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.smartcity.server.response.QiNiuToken;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UpdateRsp;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.server.RetrofitService.SCOPE_DEMO;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_MASTER;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_MOCHA;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_TEST;

public class RetrofitServiceHelper {
    private static final long DEFAULT_TIMEOUT = 8 * 1000;
    private final String HEADER_SESSION_ID = "x-session-id";
    private final String HEADER_USER_AGENT = "User-Agent";
    private final String HEADER_INTERNATIONALIZATION_HEADER = "accept-language";
    private final String HEADER_CONTENT_TYPE = "Content-Type";
    private final String HEADER_ACCEPT = "Accept";
    private volatile int mUrlType = -1;
    private volatile String sessionId = null;
    public volatile String BASE_URL = SCOPE_MASTER;//http://mocha-iot-api.mocha.server.sensoro.com-----http://iot-api
    private RetrofitService retrofitService;
    private final Retrofit.Builder builder;
    private final Gson gson;

    public static RetrofitServiceHelper getInstance() {
        return RetrofitServiceHelperHolder.instance;
    }

    private static class RetrofitServiceHelperHolder {
        private static final RetrofitServiceHelper instance = new RetrofitServiceHelper();
    }

    private RetrofitServiceHelper() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(double.class, new NumberDeserializer())
                .registerTypeAdapter(int.class, new NumberDeserializer())
                .registerTypeAdapter(float.class, new NumberDeserializer())
                .registerTypeAdapter(long.class, new NumberDeserializer())
                .registerTypeAdapter(short.class, new NumberDeserializer())
                .registerTypeAdapter(Number.class, new NumberDeserializer())
                .registerTypeAdapter(JsonObject.class, new JsonObjectDeserializer())
                .registerTypeAdapter(String.class, new JsonStringDeserializer())
                .registerTypeAdapter(String.class, new StringDeserializer());
        gson = gsonBuilder.create();
        //支持RxJava
        builder = new Retrofit.Builder().baseUrl(BASE_URL).client(getNewClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        retrofitService = builder.build().create(RetrofitService.class);
    }


    /**
     * 获取当前的sessionID（为空时从文件中获取）
     *
     * @return
     */
    public String getSessionId() {
        if (TextUtils.isEmpty(sessionId)) {
            sessionId = PreferencesHelper.getInstance().getSessionId();
        }
        return sessionId;
    }

    /**
     * 保存sessionID
     *
     * @param sessionId
     */
    public void saveSessionId(String sessionId) {
        this.sessionId = sessionId;
        PreferencesHelper.getInstance().saveSessionId(sessionId);
    }

    /**
     * 取消登录
     */
    public void clearLoginDataSessionId() {
        this.sessionId = null;
        PreferencesHelper.getInstance().clearLoginDataSessionId();
    }

    /**
     * 0-正式 1-demo 2- 测试
     *
     * @param urlType
     */
    public void saveBaseUrlType(int urlType) {
        mUrlType = urlType;
        switch (urlType) {
            case 0:
                BASE_URL = SCOPE_MASTER;
                break;
            case 1:
                BASE_URL = SCOPE_DEMO;
                break;
            case 2:
                BASE_URL = SCOPE_TEST;
                break;
            case 3:
                BASE_URL = SCOPE_MOCHA;
                break;
            default:
                BASE_URL = SCOPE_MASTER;
                break;
        }
        retrofitService = builder.baseUrl(BASE_URL).build().create(RetrofitService.class);
        PreferencesHelper.getInstance().saveBaseUrlType(urlType);
    }

    /**
     * 获取并设置当前的baseUrl类型
     *
     * @return
     */
    public int getBaseUrlType() {
        if (mUrlType == -1) {
            mUrlType = 0;
            mUrlType = PreferencesHelper.getInstance().getBaseUrlType();
            if (mUrlType != 0) {
                switch (mUrlType) {
                    case 1:
                        BASE_URL = SCOPE_DEMO;
                        break;
                    case 2:
                        BASE_URL = SCOPE_TEST;
                        break;
                    case 3:
                        BASE_URL = SCOPE_MOCHA;
                        break;
                    default:
                        BASE_URL = SCOPE_MASTER;
                        break;
                }
                retrofitService = builder.baseUrl(BASE_URL).build().create(RetrofitService.class);
            }
        }
        return mUrlType;
    }

    public Gson getGson() {
        return gson;
    }


    private OkHttpClient getNewClient() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String s) {
                try {
                    LogUtils.loge(this, "retrofit------------>" + s);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //
        final Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                //header
                if (AppUtils.isChineseLanguage()) {
                    builder.headers(original.headers())
                            .header(HEADER_INTERNATIONALIZATION_HEADER, "zh-CN")
                            .header(HEADER_USER_AGENT, "Android/" +
                                    Build.VERSION.RELEASE);
//                        .addHeader(HEADER_ACCEPT, "application/json")
//                        .addHeader(HEADER_CONTENT_TYPE, "application/json;charset=UTF-8");
                } else {
                    builder.headers(original.headers())
                            .header(HEADER_INTERNATIONALIZATION_HEADER, "en-US")
                            .header(HEADER_USER_AGENT, "Android/" +
                                    Build.VERSION.RELEASE);
//                        .addHeader(HEADER_ACCEPT, "application/json")
//                        .addHeader(HEADER_CONTENT_TYPE, "application/json;charset=UTF-8");
                }

                if (!TextUtils.isEmpty(getSessionId())) {
                    builder.header(HEADER_SESSION_ID, getSessionId());
                }
                //
                builder.method(original.method(), original.body());
                Request request = builder.build();
                //
                Response response = chain.proceed(request);
                //重定向
//                boolean redirect = response.isRedirect();
                int code = response.code();
//                    if (redirect && (code == 308 || code == 307)) {
                //仅针对308和307重定向问题
                if (code == 308 || code == 307) {
                    String location = response.header("Location");
                    if (location != null && location.length() > 1) {
                        if (location.startsWith("/")) {
                            location = location.substring(1);
                        }
                        Request newRequest = request.newBuilder().url(BASE_URL + location)
                                .build();
                        response = chain.proceed(newRequest);
                    }
                }
                return response;
            }
        };

        //自定义缓存设置
        final CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        //这个是控制缓存的最大生命时间
        cacheBuilder.maxAge(0, TimeUnit.SECONDS);
        //这个是控制缓存的过时时间
        cacheBuilder.maxStale(7, TimeUnit.DAYS);
        final CacheControl cacheControl = cacheBuilder.build();
        //缓存拦截器
        final Interceptor cacheControlInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetWorkUtils.isNetworkConnected(SensoroCityApplication.getInstance())) {
                    request = request.newBuilder().cacheControl(cacheControl).build();
                }
                Response originalResponse = chain.proceed(request);
                if (NetWorkUtils.isNetworkConnected(SensoroCityApplication.getInstance())) {
                    // 有网络时 设置缓存为默认值
                    String cacheControl = request.cacheControl().toString();
                    return originalResponse.newBuilder()
                            .header("Cache-Control", cacheControl)
                            .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                } else {
                    // 无网络时 设置超时为1周
                    int maxStale = 60 * 60 * 24 * 7;
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
            }
        };

        final OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        //cookie
        final ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(SensoroCityApplication.getInstance().getApplicationContext()));
        //cache
        final File httpCacheDirectory = new File(SensoroCityApplication.getInstance().getCacheDir(), "responses");
        final int cacheSize = 10 * 1024 * 1024; // 10 MiB
        final Cache cache = new Cache(httpCacheDirectory, cacheSize);
        //
        return builder
                .addInterceptor(interceptor)
                .addInterceptor(cacheControlInterceptor)
                .cookieJar(cookieJar)
                .cache(cache)
                .addNetworkInterceptor(logging)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
//        Set-Cookie: koa:sess=neIm_GoFaHWc-WcwIfv2Sw-8ClbnPKsk; path=/; expires=Fri, 07 Sep 2018 09:25:15 GMT; httponly
    }

    public void cancelAllRsp() {
        RxApiManager.getInstance().cancelAll();
        this.sessionId = null;
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
        Observable<LoginRsp> login = retrofitService.login(account, pwd, phoneId, "android", true);
        RxApiManager.getInstance().add("login", login.subscribe());
        return login;
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
        Observable<ResponseBase> logout = retrofitService.logout(phoneId, uid, body);
        RxApiManager.getInstance().add("logout", logout.subscribe());
        return logout;
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
    public Observable<DeviceAlarmLogRsp> getDeviceAlarmLogList(int page, String sn, String deviceName, String phone
            , String search, Long beginTime, Long endTime, String unionTypes) {
        Observable<DeviceAlarmLogRsp> deviceAlarmLogList = retrofitService.getDeviceAlarmLogList(10, page, sn, deviceName, phone, search, beginTime, endTime, unionTypes);
        RxApiManager.getInstance().add("getDeviceAlarmLogList", deviceAlarmLogList.subscribe());
        return deviceAlarmLogList;
    }

    /**
     * 获取故障信息日志
     *
     * @param page
     * @param sn
     * @param deviceName
     * @param search
     * @param beginTime
     * @param endTime
     * @return
     */
    public Observable<MalfunctionListRsp> getDeviceMalfunctionLogList(int page, String sn, String deviceName, String search, Long beginTime, Long endTime) {
        Observable<MalfunctionListRsp> deviceMalfunctionLogList = retrofitService.getDeviceMalfunctionLogList(20, page, sn, deviceName, search, beginTime, endTime);
        RxApiManager.getInstance().add("getDeviceMalfunctionLogList", deviceMalfunctionLogList.subscribe());
        return deviceMalfunctionLogList;
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
    public Observable<DeviceInfoListRsp> getDeviceBriefInfoList(int page, String sensorTypes, String mergeTypes, Integer status, String
            search) {
        Observable<DeviceInfoListRsp> deviceBriefInfoList = retrofitService.getDeviceBriefInfoList(page, 20, 1, 1,
                sensorTypes, mergeTypes, status, search);
        RxApiManager.getInstance().add("getDeviceBriefInfoList", deviceBriefInfoList.subscribe());
        return deviceBriefInfoList;
    }

    /**
     * 主页top信息
     *
     * @return
     */
    public Observable<DeviceTypeCountRsp> getDeviceTypeCount() {
        Observable<DeviceTypeCountRsp> deviceTypeCount = retrofitService.getDeviceTypeCount();
        RxApiManager.getInstance().add("getDeviceTypeCount", deviceTypeCount.subscribe());
        return deviceTypeCount;
    }

    /**
     * 获取账户列表
     *
     * @param search
     * @return
     */
    public Observable<UserAccountRsp> getUserAccountList(String search, Integer page, Integer offset, Integer limit) {
        Observable<UserAccountRsp> userAccountList = retrofitService.getUserAccountList(search, page, 20, offset, limit);
        RxApiManager.getInstance().add("getUserAccountList", userAccountList.subscribe());
        return userAccountList;
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
        Observable<UserAccountControlRsp> userAccountControlRspObservable = retrofitService.doAccountControl(uid, body);
        RxApiManager.getInstance().add("doAccountControl", userAccountControlRspObservable.subscribe());
        return userAccountControlRspObservable;
    }

    /**
     * 查询账号下的部署记录
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public Observable<DeployRecordRsp> getDeployRecordList(String sn, String searchText, Long beginTime, Long endTime, String owners, String signalQuality, Integer limit, Integer offset, Boolean group) {
        Observable<DeployRecordRsp> deployRecordList = retrofitService.getDeployRecordList(sn, searchText, beginTime, endTime, owners, signalQuality, limit, offset, group);
        RxApiManager.getInstance().add("getDeployRecordList", deployRecordList.subscribe());
        return deployRecordList;
    }

    public Observable<DeviceStatusRsp> getDeviceRealStatus(String sn) {
        Observable<DeviceStatusRsp> realStatus = retrofitService.getRealStatus(sn);
        RxApiManager.getInstance().add("getDeviceRealStatus", realStatus.subscribe());
        return realStatus;
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
    public Observable<DeviceDeployRsp> doDevicePointDeploy(String sn, double lon, double lat, List<String> tags, String
            name, String contact, String content, String wxPhone, List<String> imgUrls, DeployControlSettingData deployControlSettingData, String forceReason, Integer status, String signalQuality) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            JSONArray jsonArray = new JSONArray();
            if (tags != null && tags.size() > 0) {
                for (String temp : tags) {
                    jsonArray.put(temp);
                }
            }
            jsonObject.put("tags", jsonArray);
            if (name != null) {
                jsonObject.put("name", name);
            }
            if (contact != null) {
                jsonObject.put("contact", contact);
            }
            if (content != null) {
                jsonObject.put("content", content);
            }
            if (imgUrls != null && imgUrls.size() > 0) {
                JSONArray jsonArrayImg = new JSONArray();
                for (String url : imgUrls) {
                    jsonArrayImg.put(url);
                }
                jsonObject.put("imgUrls", jsonArrayImg);
            }
            if (!TextUtils.isEmpty(wxPhone)) {
                jsonObject.put("wxPhone", wxPhone);
            }
            if (!TextUtils.isEmpty(forceReason)) {
                jsonObject.put("forceReason", forceReason);
                if (status != null) {
                    jsonObject.put("status", status);
                }
                if (!TextUtils.isEmpty(signalQuality)) {
                    jsonObject.put("signalQuality", signalQuality);
                }
            }

//            if (settingMap != null) {
//                JSONObject jsonObjectOut = new JSONObject();
//                for (Map.Entry<String, DeployControlSettingData> entrySet : settingMap.entrySet()) {
//                    String key = entrySet.getKey();
//                    if (!TextUtils.isEmpty(key)) {
//                        DeployControlSettingData value = entrySet.getValue();
//                        JSONObject jsonObjectIn = new JSONObject();
//                        jsonObjectIn.put("initValue", value.getSwitchSpec());
//                        Double diameterValue = value.getWireDiameter();
//                        if (diameterValue != null) {
//                            jsonObjectIn.put("wireDiameter", diameterValue);
//                        }
//                        int wireMaterial = value.getWireMaterial();
//                        jsonObjectIn.put("wireMaterial", wireMaterial);
//                        jsonObjectOut.put(key, jsonObjectIn);
//
//                    }
//                }
//                jsonObject.put("config", jsonObjectOut);
//            }
            if (deployControlSettingData != null) {
                JSONObject jsonObjectOut = new JSONObject();
                Integer switchSpec = deployControlSettingData.getSwitchSpec();
                if (switchSpec != null) {
                    jsonObjectOut.put("switchSpec", switchSpec);
                }
                Double wireDiameter = deployControlSettingData.getWireDiameter();
                if (wireDiameter != null) {
                    jsonObjectOut.put("wireDiameter", wireDiameter);
                }
                Integer wireMaterial = deployControlSettingData.getWireMaterial();
                if (wireMaterial != null) {
                    jsonObjectOut.put("wireMaterial", wireMaterial);
                }
                jsonObject.put("config", jsonObjectOut);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeviceDeployRsp> deviceDeployRspObservable = retrofitService.doDevicePointDeploy(sn, body);
        RxApiManager.getInstance().add("doDevicePointDeploy", deviceDeployRspObservable.subscribe());
        return deviceDeployRspObservable;
    }

    /**
     * 获取蓝牙信息，包括蓝牙密码和基带信息
     *
     * @param sn
     * @param longitude
     * @param latitude
     * @return
     */
    public Observable<DeployDeviceDetailRsp> getDeployDeviceDetail(String sn, Double longitude, Double latitude) {
        Observable<DeployDeviceDetailRsp> deployDeviceDetail = retrofitService.getDeployDeviceDetail(sn, longitude, latitude);
        RxApiManager.getInstance().add("deployDeviceDetail", deployDeviceDetail.subscribe());
        return deployDeviceDetail;
    }

    public Observable<DeviceDeployRsp> doInspectionChangeDeviceDeploy(String oldSn, String newSn, String taskId, Integer reason, double lon, double lat, List<String> tags, String
            name, String contact, String content, List<String> imgUrls, String wxPhone, String forceReason, Integer status, String signalQuality) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(newSn)) {
                jsonObject.put("newSn", newSn);
            }
            if (!TextUtils.isEmpty(taskId)) {
                jsonObject.put("taskId", taskId);
            }
            if (reason != null) {
                jsonObject.put("reason", reason);
            }
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            JSONArray jsonArray = new JSONArray();
            if (tags != null && tags.size() > 0) {
                for (String temp : tags) {
                    jsonArray.put(temp);
                }
            }
            jsonObject.put("tags", jsonArray);
            if (!TextUtils.isEmpty(name)) {
                jsonObject.put("name", name);
            }
            if (!TextUtils.isEmpty(contact)) {
                jsonObject.put("contact", contact);
            }
            if (!TextUtils.isEmpty(content)) {
                jsonObject.put("content", content);
            }
            if (imgUrls != null && imgUrls.size() > 0) {
                JSONArray jsonArrayImg = new JSONArray();
                for (String url : imgUrls) {
                    jsonArrayImg.put(url);
                }
                jsonObject.put("imgUrls", jsonArrayImg);
            }
            if (!TextUtils.isEmpty(wxPhone)) {
                jsonObject.put("wxPhone", wxPhone);
            }
            if (!TextUtils.isEmpty(forceReason)) {
                jsonObject.put("forceReason", forceReason);
                if (status != null) {
                    jsonObject.put("status", status);
                }
                if (!TextUtils.isEmpty(signalQuality)) {
                    jsonObject.put("signalQuality", signalQuality);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeviceDeployRsp> inspectionChangeDeviceDeployRspObservable = retrofitService.doInspectionChangeDeviceDeploy(oldSn, body);
        RxApiManager.getInstance().add("doInspectionChangeDeviceDeploy", inspectionChangeDeviceDeployRspObservable.subscribe());
        return inspectionChangeDeviceDeployRspObservable;
    }

    /**
     * 基站部署
     *
     * @param sn
     * @param lon
     * @param lat
     * @param tags
     * @param name
     * @return
     */
    public Observable<DeployStationInfoRsp> doStationDeploy(String sn, double lon, double lat, List<String> tags, String
            name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            JSONArray jsonArray = new JSONArray();
            if (tags != null && tags.size() > 0) {
                for (String temp : tags) {
                    jsonArray.put(temp);
                }
            }
            jsonObject.put("tags", jsonArray);
            if (name != null) {
                jsonObject.put("name", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeployStationInfoRsp> stationInfoRspObservable = retrofitService.doStationDeploy(sn, body);
        RxApiManager.getInstance().add("doStationDeploy", stationInfoRspObservable.subscribe());
        return stationInfoRspObservable;
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
        Observable<DeviceAlarmItemRsp> deviceAlarmItemRspObservable = retrofitService.doAlarmConfirm(id, body);
        RxApiManager.getInstance().add("deviceAlarmItemRspObservable", deviceAlarmItemRspObservable.subscribe());
        return deviceAlarmItemRspObservable;
    }

    /**
     * 查找部署时间
     *
     * @param sn
     * @return
     */
    public Observable<DeviceAlarmTimeRsp> getDeviceAlarmTime(String sn) {
        Observable<DeviceAlarmTimeRsp> deviceAlarmTime = retrofitService.getDeviceAlarmTime(sn);
        RxApiManager.getInstance().add("getDeviceAlarmTime", deviceAlarmTime.subscribe());
        return deviceAlarmTime;
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
        Observable<DeviceRecentRsp> hours = retrofitService.getDeviceHistoryList(sn, startTime, endTime, "hours");
        RxApiManager.getInstance().add("getDeviceHistoryList", hours.subscribe());
        return hours;
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
        Observable<DeviceInfoListRsp> deviceDetailInfoList = retrofitService.getDeviceDetailInfoList(sns, search, all);
        RxApiManager.getInstance().add("getDeviceDetailInfoList", deviceDetailInfoList.subscribe());
        return deviceDetailInfoList;
    }

    /**
     * 返回单个基站详情
     *
     * @param sn
     * @return
     */
    public Observable<DeployStationInfoRsp> getStationDetail(String sn) {
        Observable<DeployStationInfoRsp> stationDetail = retrofitService.getStationDetail(sn);
        RxApiManager.getInstance().add("getStationDetail", stationDetail.subscribe());
        return stationDetail;
    }


    /**
     * 修改提交确认预警信息备注(图片)
     *
     * @param id
     * @param remark
     * @return
     */
    public Observable<DeviceAlarmItemRsp> doUpdatePhotosUrl(String id, Integer displayStatus, Integer reason, Integer
            place, String remark, boolean isReconfirm, List<ScenesData> scenesDataList) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (displayStatus != null) {
                jsonObject.put("displayStatus", displayStatus);
            }
            if (reason != null) {
                jsonObject.put("reason", reason);
            }
            if (place != null) {
                jsonObject.put("place", place);
            }
            if (!TextUtils.isEmpty(remark)) {
                jsonObject.put("remark", remark);
            }
            jsonObject.put("source", "app");
            if (isReconfirm) {
                jsonObject.put("type", "reconfirm");
            } else {
                jsonObject.put("type", "confirm");
            }
            //
            if (scenesDataList != null) {
                JSONArray jsonArray = new JSONArray();
                for (ScenesData scenesData : scenesDataList) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("type", scenesData.type);
                    jsonObject1.put("url", scenesData.url);
                    if (!TextUtils.isEmpty(scenesData.thumbUrl)) {
                        jsonObject1.put("thumbUrl", scenesData.thumbUrl);
                    }
                    jsonArray.put(jsonObject1);
                }
                jsonObject.put("scenes", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeviceAlarmItemRsp> deviceAlarmItemRspObservable = retrofitService.doUpdatePhotosUrl(id, body);
        RxApiManager.getInstance().add("doUpdatePhotosUrl", deviceAlarmItemRspObservable.subscribe());
        return deviceAlarmItemRspObservable;
    }

    /**
     * 修改提交确认预警信息备注(图片)
     *
     * @param id
     * @param remark
     * @return
     */
    public Observable<DeviceAlarmItemRsp> doUpdatePhotosUrl(String id, HashMap<String, Integer> map, List<AlarmPopupDangerData> alarmPopupDangerDataList, String remark, boolean isReconfirm, List<ScenesData> scenesDataList) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (map != null) {
                Set<Map.Entry<String, Integer>> entries = map.entrySet();
                for (Map.Entry<String, Integer> entry : entries) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    jsonObject.put(key, value);
                }
            }
            if (alarmPopupDangerDataList != null && alarmPopupDangerDataList.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (AlarmPopupDangerData alarmPopupDangerData : alarmPopupDangerDataList) {
                    JSONObject jsonObjectDanger = new JSONObject();
                    jsonObjectDanger.put("place", alarmPopupDangerData.place);
                    if (alarmPopupDangerData.action != null && alarmPopupDangerData.action.size() > 0) {
                        JSONArray jsonArray1 = new JSONArray();
                        for (String str : alarmPopupDangerData.action) {
                            jsonArray1.put(str);
                        }
                        jsonObjectDanger.put("action", jsonArray1);
                    }
                    jsonArray.put(jsonObjectDanger);
                }
                jsonObject.put("danger", jsonArray);
            }
            if (!TextUtils.isEmpty(remark)) {
                jsonObject.put("remark", remark);
            }
            jsonObject.put("source", "app");
            if (isReconfirm) {
                jsonObject.put("type", "reconfirm");
            } else {
                jsonObject.put("type", "confirm");
            }
            //
            if (scenesDataList != null) {
                JSONArray jsonArray = new JSONArray();
                for (ScenesData scenesData : scenesDataList) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("type", scenesData.type);
                    jsonObject1.put("url", scenesData.url);
                    if (!TextUtils.isEmpty(scenesData.thumbUrl)) {
                        jsonObject1.put("thumbUrl", scenesData.thumbUrl);
                    }
                    jsonArray.put(jsonObject1);
                }
                jsonObject.put("scenes", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeviceAlarmItemRsp> deviceAlarmItemRspObservable = retrofitService.doUpdatePhotosUrl(id, body);
        RxApiManager.getInstance().add("doUpdatePhotosUrl", deviceAlarmItemRspObservable.subscribe());
        return deviceAlarmItemRspObservable;
    }

    /**
     * 获取七牛token
     *
     * @return
     */
    public Observable<QiNiuToken> getQiNiuToken() {
        Observable<QiNiuToken> qiNiuToken = retrofitService.getQiNiuToken();
        RxApiManager.getInstance().add("getQiNiuToken", qiNiuToken.subscribe());
        return qiNiuToken;
    }

    /**
     * 获取合同模板
     *
     * @return
     */
    public Observable<ContractsTemplateRsp> getContractstemplate() {
        Observable<ContractsTemplateRsp> contractstemplate = retrofitService.getContractsTemplate();
        RxApiManager.getInstance().add("getContractsTemplate", contractstemplate.subscribe());
        return contractstemplate;
    }

    public Observable<ContractAddRsp> getNewContract(Integer contractType, String cardId,
                                                     Integer sex, String enterpriseCardId,
                                                     String enterpriseRegisterId,
                                                     String customerName,
                                                     String customerEnterpriseName,
                                                     String customerEnterpriseValidity,
                                                     //必选
                                                     String customerAddress,
                                                     String customerPhone,
                                                     String placeType,
                                                     List<ContractsTemplateInfo> devicesList,
                                                     int payTimes,
                                                     //可选
                                                     Boolean confirmed,
                                                     int serviceTime, int firstPayTimes) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (contractType != null) {
                jsonObject.put("contract_type", contractType);
            }
//            jsonObject.put("created_type", createType);
            if (cardId != null) {
                jsonObject.put("card_id", cardId);
            }
            if (sex != null) {
                jsonObject.put("sex", sex);
            }
            if (enterpriseCardId != null) {
                jsonObject.put("enterprise_card_id", enterpriseCardId);
            }
            if (enterpriseRegisterId != null) {
                jsonObject.put("enterprise_register_id", enterpriseRegisterId);
            }
            if (customerName != null) {
                jsonObject.put("customer_name", customerName);
            }
            if (customerEnterpriseName != null) {
                jsonObject.put("customer_enterprise_name", customerEnterpriseName);
            }
            if (customerEnterpriseValidity != null) {
                jsonObject.put("customer_enterprise_validity", customerEnterpriseValidity);
            }
            jsonObject.put("customer_address", customerAddress);
            jsonObject.put("customer_phone", customerPhone);
            jsonObject.put("place_type", placeType);
            JSONArray jsonArray = new JSONArray();
            if (devicesList != null) {
                for (ContractsTemplateInfo contractsTemplateInfo : devicesList) {
                    String deviceType = contractsTemplateInfo.getDeviceType();
                    String hardwareVersion = contractsTemplateInfo.getHardwareVersion();
                    int quantity = contractsTemplateInfo.getQuantity();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("deviceType", deviceType);
                    jsonObject1.put("hardwareVersion", hardwareVersion);
                    jsonObject1.put("quantity", quantity);
                    jsonArray.put(jsonObject1);

                }
            }
            jsonObject.put("devices", jsonArray);
            jsonObject.put("payTimes", payTimes);
            if (confirmed != null) {
                jsonObject.put("confirmed", confirmed);
            }
            jsonObject.put("serviceTime", serviceTime);
            jsonObject.put("firstPayTimes", firstPayTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ContractAddRsp> contractAddRspObservable = retrofitService.newContract(body);
        RxApiManager.getInstance().add("getNewContract", contractAddRspObservable.subscribe());
        return contractAddRspObservable;
    }

    public Observable<ContractInfoRsp> getContractInfo(String id) {
        Observable<ContractInfoRsp> contractInfo = retrofitService.getContractInfo(id);
        RxApiManager.getInstance().add("contractInfo", contractInfo.subscribe());
        return contractInfo;
    }

    /**
     * 合同检索
     *
     * @param contractType
     * @param beginTime
     * @param endTime
     * @param limit
     * @param offset
     * @return
     */
    public Observable<ContractsListRsp> searchContract(Integer contractType, String customerName, Integer confirmed, Long beginTime, Long endTime, Integer
            limit, Integer offset) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonObject1 = new JSONObject();
            if (contractType != null) {
                jsonObject1.put("contract_type", contractType);
            }

            if (confirmed != null) {
                if (confirmed == 1) {
                    jsonObject1.put("confirmed", false);
                } else {
                    jsonObject1.put("confirmed", true);
                }
            }
            if (!TextUtils.isEmpty(customerName)) {
                jsonObject1.put("customer_name", customerName);
            }
            if (beginTime != null) {
                jsonObject1.put("beginTime", beginTime);
            }
            if (endTime != null) {
                jsonObject1.put("endTime", endTime);
            }
            jsonObject.put("where", jsonObject1);
            if (limit != null) {
                jsonObject.put("limit", limit);
            }
            if (offset != null) {
                jsonObject.put("offset", offset);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ContractsListRsp> contractsListRspObservable = retrofitService.searchContract(body);
        RxApiManager.getInstance().add("searchContract", contractsListRspObservable.subscribe());
        return contractsListRspObservable;
    }

    /**
     * 检索大屏登录扫描结果
     *
     * @param qrcodeId
     * @return
     */
    public Observable<ResponseBase> getLoginScanResult(String qrcodeId) {
        Observable<ResponseBase> loginScanResult = retrofitService.getLoginScanResult(qrcodeId);
        RxApiManager.getInstance().add("getLoginScanResult", loginScanResult.subscribe());
        return loginScanResult;
    }

    /**
     * 进入大屏登录
     *
     * @param qrcodeId
     * @return
     */
    public Observable<ResponseBase> scanLoginIn(String qrcodeId) {
        Observable<ResponseBase> responseBaseObservable = retrofitService.scanLoginIn(qrcodeId);
        RxApiManager.getInstance().add("scanLoginIn", responseBaseObservable.subscribe());
        return responseBaseObservable;
    }

    /**
     * 取消大屏登录
     *
     * @param qrcodeId
     * @return
     */
    public Observable<ResponseBase> scanLoginCancel(String qrcodeId) {
        Observable<ResponseBase> responseBaseObservable = retrofitService.scanLoginCancel(qrcodeId);
        RxApiManager.getInstance().add("scanLoginCancel", responseBaseObservable.subscribe());
        return responseBaseObservable;
    }

    /**
     * 二次验证
     *
     * @param code
     * @return
     */
    public Observable<AuthRsp> doubleCheck(String code) {
        return retrofitService.doubleCheck(code);
    }

    /**
     * 获取报警次数
     *
     * @param startTime
     * @param endTime
     * @param displayStatus
     * @param sn
     * @return
     */
    public Observable<AlarmCountRsp> getAlarmCount(Long startTime, Long endTime, String[] displayStatus, String sn) {
        StringBuilder stringBuilder = new StringBuilder();
        if (displayStatus != null && displayStatus.length > 0) {
            for (int i = 0; i < displayStatus.length; i++) {
                if (i == displayStatus.length - 1) {
                    stringBuilder.append(displayStatus[i]);
                } else {
                    stringBuilder.append(displayStatus[i]).append(",");
                }
            }
        }
        if (TextUtils.isEmpty(stringBuilder)) {
            return retrofitService.getAlarmCount(startTime, endTime, null, sn);
        } else {
            return retrofitService.getAlarmCount(startTime, endTime, stringBuilder.toString(), sn);
        }
    }

    /**
     * 获取半年故障次数
     *
     * @param startTime
     * @param endTime
     * @param sn
     * @return
     */
    public Observable<MalfunctionCountRsp> getMalfunctionCount(Long startTime, Long endTime, String[] malfunctionStatus, String sn) {
        StringBuilder stringBuilder = new StringBuilder();
        if (malfunctionStatus != null && malfunctionStatus.length > 0) {
            for (int i = 0; i < malfunctionStatus.length; i++) {
                if (i == malfunctionStatus.length - 1) {
                    stringBuilder.append(malfunctionStatus[i]);
                } else {
                    stringBuilder.append(malfunctionStatus[i]).append(",");
                }
            }
        }
        if (TextUtils.isEmpty(stringBuilder)) {
            return retrofitService.getMalfunctionCount(startTime, endTime, null, sn);
        } else {
            return retrofitService.getMalfunctionCount(startTime, endTime, stringBuilder.toString(), sn);
        }
    }

    public Observable<ResponseBase> doUploadInspectionResult(String id, String sn, String taskId, Integer status,
                                                             Integer malfunctionHandle,
                                                             Long startTime, Long finishTime, String remark, List<ScenesData> scenesDataList, List<Integer> tags) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonObject1 = new JSONObject();
            if (!TextUtils.isEmpty(id)) {
                jsonObject1.put("_id", id);
            }
            if (!TextUtils.isEmpty(sn)) {
                jsonObject1.put("sn", sn);
            }
            if (!TextUtils.isEmpty(taskId)) {
                jsonObject1.put("taskId", taskId);
            }
            jsonObject.put("condition", jsonObject1);
            //
            JSONObject jsonObject2 = new JSONObject();
            if (status != null) {
                jsonObject2.put("status", status);
            }
            if (malfunctionHandle != null) {
                jsonObject2.put("malfunctionHandle", malfunctionHandle);
            }
            if (startTime != null && startTime != 0) {
                jsonObject2.put("startTime", startTime);
            }
            if (finishTime != null && finishTime != 0) {
                jsonObject2.put("finishTime", finishTime);
            }
            if (!TextUtils.isEmpty(remark)) {
                jsonObject2.put("remark", remark);
            }
            if (scenesDataList != null && scenesDataList.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (ScenesData scenesData : scenesDataList) {
                    JSONObject jsonObject3 = new JSONObject();
                    String type = scenesData.type;
                    String url = scenesData.url;
                    String thumbUrl = scenesData.thumbUrl;
                    jsonObject3.put("type", type);
                    jsonObject3.put("url", url);
                    jsonObject3.put("thumbUrl", thumbUrl);
                    jsonArray.put(jsonObject3);
                }
                jsonObject2.put("imgAndVideo", jsonArray);

            }

            if (tags != null && tags.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (Integer tag : tags) {
                    jsonArray.put(tag);
                }
                jsonObject2.put("malfunctions", jsonArray);
            }
            jsonObject.put("doc", jsonObject2);
            //

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ResponseBase> uploadInspectionResult = retrofitService.uploadInspectionResult(body);
        RxApiManager.getInstance().add("uploadInspectionResult", uploadInspectionResult.subscribe());
        return uploadInspectionResult;
    }

    public Observable<InspectionTaskExecutionRsp> getInspectTaskExecution(String taskId) {
        return retrofitService.getInspectTaskExecution(taskId);
    }

    public Observable<InspectionTaskDeviceDetailRsp> getInspectionDeviceList(String taskId, String search, String sn, Integer finish, String deviceTypes, Integer offset, Integer limit) {
//        StringBuilder stringBuilder = new StringBuilder();
//        if (deviceTypes!=null&&deviceTypes.size()>0){
//            for (String deviceType:deviceTypes){
//                stringBuilder.append(deviceType);
//            }
//        }
        Observable<InspectionTaskDeviceDetailRsp> inspectionDeviceList = retrofitService.getInspectionDeviceList(taskId, search, sn, finish, deviceTypes, offset, limit);
        RxApiManager.getInstance().add("inspectionDeviceList", inspectionDeviceList.subscribe());
        return inspectionDeviceList;
    }

    /**
     * 获取巡检任务列表
     *
     * @param search
     * @param finish
     * @param offset
     * @param limit
     * @param startTime
     * @param finishTime
     * @return
     */
    public Observable<InspectionTaskModelRsp> getInspectTaskList(String search, Integer finish, Integer offset, Integer
            limit, Long startTime, Long finishTime) {
        Observable<InspectionTaskModelRsp> inspectTaskList = retrofitService.getInspectTaskList(search, finish, offset, limit,
                startTime, finishTime);
        RxApiManager.getInstance().add("getInspectTaskList", inspectTaskList.subscribe());
        return inspectTaskList;
    }

    /**
     * 改变巡检任务状态，目前只能改为1，执行中
     *
     * @param id
     * @param identifier
     * @param status
     * @return
     */
    public Observable<ChangeInspectionTaskStateRsp> doChangeInspectionTaskState(String id, String identifier, Integer status) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonObject1 = new JSONObject();
            if (id != null) {
                jsonObject1.put("_id", id);
            }
            if (identifier != null) {
                jsonObject1.put("identifier", identifier);
            }
            jsonObject.put("condition", jsonObject1);

            JSONObject jsonObject2 = new JSONObject();
            if (status != null) {
                jsonObject2.put("status", status);
            }
            jsonObject.put("doc", jsonObject2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ChangeInspectionTaskStateRsp> changeInspectionTaskState = retrofitService.changeInspectionTaskState(body);
        RxApiManager.getInstance().add("doChangeInspectionTaskState", changeInspectionTaskState.subscribe());
        return changeInspectionTaskState;
    }

    /**
     * 获取单个巡检设备
     *
     * @param id
     * @param sn
     * @param taskId
     * @param device
     * @return
     */
    public Observable<InspectionTaskExceptionDeviceRsp> getInspectionDeviceDetail(String id, String sn, String taskId, Integer device) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject jsonObject1 = new JSONObject();
            if (id != null) {
                jsonObject1.put("_id", id);
            }
            if (sn != null) {
                jsonObject1.put("sn", sn);
            }
            if (taskId != null) {
                jsonObject1.put("taskId", taskId);
            }
            if (device != null) {
                jsonObject1.put("device", device);
            }


            jsonObject.put("condition", jsonObject1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<InspectionTaskExceptionDeviceRsp> getInspectionDeviceDetail = retrofitService.getInspectionDeviceDetail(body);
        RxApiManager.getInstance().add("getInspectionDeviceDetail", getInspectionDeviceDetail.subscribe());
        return getInspectionDeviceDetail;
    }

    /**
     * 获取巡检内容模板
     *
     * @param deviceType
     * @return
     */
    public Observable<InspectionTaskInstructionRsp> getInspectionTemplate(String deviceType) {
        Observable<InspectionTaskInstructionRsp> inspectionTemplate = retrofitService.getInspectionTemplate(deviceType);
        RxApiManager.getInstance().add("inspectionTemplate", inspectionTemplate.subscribe());
        return inspectionTemplate;
    }

    /**
     * 获取deviceType 对应关系
     *
     * @return
     */
    public Observable<DevicesMergeTypesRsp> getDevicesMergeTypes() {
        Observable<DevicesMergeTypesRsp> devicesMergeTypes = retrofitService.getDevicesMergeTypes();
        RxApiManager.getInstance().add("devicesMergeTypes", devicesMergeTypes.subscribe());
        return devicesMergeTypes;
    }

    public Observable<MonitorPointOperationRequestRsp> doMonitorPointOperation(List<String> snList, String type, Integer interval, List<String> rules, Integer switchSpec, Integer wireMaterial, Double diameter) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray jsonSnList = new JSONArray();
            for (String sn : snList) {
                jsonSnList.put(sn);
            }
            jsonObject.put("snList", jsonSnList);
            jsonObject.put("type", type);

            if (interval != null) {
                jsonObject.put("interval", type);
            }
            if (rules != null) {
                JSONArray jsonRules = new JSONArray();
                for (String rule : rules) {
                    jsonRules.put(rule);
                }
                jsonObject.put("rules", jsonRules);
            }
            JSONObject jsonObjectConfig = new JSONObject();
            if (switchSpec != null) {
                jsonObjectConfig.put("switchSpec", switchSpec);
            }
            if (wireMaterial != null) {
                jsonObjectConfig.put("wireMaterial", wireMaterial);
            }
            if (diameter != null) {
                jsonObjectConfig.put("wireDiameter", diameter);
            }
            if (switchSpec != null || wireMaterial != null || diameter != null) {
                jsonObject.put("config", jsonObjectConfig);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<MonitorPointOperationRequestRsp> doMonitorPointOperation = retrofitService.doMonitorPointOperation(body);
        RxApiManager.getInstance().add("doMonitorPointOperation", doMonitorPointOperation.subscribe());
        return doMonitorPointOperation;
    }

    public Observable<DeviceDeployRsp> doDevicePositionCalibration(String sn, Double lon, Double lat) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Observable<DeviceDeployRsp> doDevicePositionCalibration = retrofitService.doDevicePositionCalibration(sn, body);
        RxApiManager.getInstance().add("doDevicePositionCalibration", doDevicePositionCalibration.subscribe());
        return doDevicePositionCalibration;
    }

    public Observable<ResponseBase> modifyContract(String uid, Integer contractID, Integer contractType, String cardId, Integer sex, String enterpriseCardId,
                                                   String enterpriseRegisterId,
                                                   String customerName,
                                                   String customerEnterpriseName,
                                                   String customerEnterpriseValidity,
                                                   //必选
                                                   String customerAddress,
                                                   String customerPhone,
                                                   String placeType,
                                                   List<ContractsTemplateInfo> devicesList,
                                                   int payTimes,
                                                   //可选
                                                   Boolean confirmed,
                                                   int serviceTime, int firstPayTimes) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(uid)) {
                jsonObject.put("uid", uid);
            }
            jsonObject.put("id", contractID);

            if (contractType != null) {
                jsonObject.put("contract_type", contractType);
            }
//            jsonObject.put("created_type", createType);
            if (!TextUtils.isEmpty(cardId)) {
                jsonObject.put("card_id", cardId);
            }
            if (sex != null) {
                jsonObject.put("sex", sex);
            }
            if (!TextUtils.isEmpty(enterpriseCardId)) {
                jsonObject.put("enterprise_card_id", enterpriseCardId);
            }
            if (!TextUtils.isEmpty(enterpriseRegisterId)) {
                jsonObject.put("enterprise_register_id", enterpriseRegisterId);
            }
            if (!TextUtils.isEmpty(customerName)) {
                jsonObject.put("customer_name", customerName);
            }
            if (!TextUtils.isEmpty(customerEnterpriseName)) {
                jsonObject.put("customer_enterprise_name", customerEnterpriseName);
            }
            if (!TextUtils.isEmpty(customerEnterpriseValidity)) {
                jsonObject.put("customer_enterprise_validity", customerEnterpriseValidity);
            }
            jsonObject.put("customer_address", customerAddress);
            jsonObject.put("customer_phone", customerPhone);
            jsonObject.put("place_type", placeType);
            JSONArray jsonArray = new JSONArray();
            if (devicesList != null) {
                for (ContractsTemplateInfo contractsTemplateInfo : devicesList) {
                    String deviceType = contractsTemplateInfo.getDeviceType();
                    String hardwareVersion = contractsTemplateInfo.getHardwareVersion();
                    int quantity = contractsTemplateInfo.getQuantity();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("deviceType", deviceType);
                    jsonObject1.put("hardwareVersion", hardwareVersion);
                    jsonObject1.put("quantity", quantity);
                    jsonArray.put(jsonObject1);

                }
            }
            jsonObject.put("devices", jsonArray);
            jsonObject.put("payTimes", payTimes);
            if (confirmed != null) {
                jsonObject.put("confirmed", confirmed);
            }
            jsonObject.put("serviceTime", serviceTime);
            jsonObject.put("firstPayTimes", firstPayTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ResponseBase> modifyContract = retrofitService.modifyContract(body);
        RxApiManager.getInstance().add("modifyContract", modifyContract.subscribe());
        return modifyContract;
    }

    /**
     * 检测设备名称是否重名
     *
     * @param name
     * @return
     */
    public Observable<ResponseBase> getDeviceNameValid(String name) {
        return retrofitService.getDeviceNameValid(name);
    }

    /**
     * 解除账号控制返回主账户
     *
     * @return
     */
    public Observable<LoginRsp> backMainAccount() {
        return retrofitService.backMainControlling();
    }

    /**
     * 获取可升级固件版本列表信息
     *
     * @param sn
     * @param deviceType
     * @param band
     * @param fromVersion
     * @param page
     * @param count
     * @return
     */
    public Observable<DeviceUpdateFirmwareDataRsp> getDeviceUpdateVision(String sn, String deviceType, String band, String fromVersion, String hardwareVersion, Integer page, Integer count) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(deviceType)) {
                jsonObject.put("deviceType", deviceType);
            }
            if (!TextUtils.isEmpty(band)) {
                jsonObject.put("band", band);
            }
            if (!TextUtils.isEmpty(fromVersion)) {
                jsonObject.put("fromVersion", fromVersion);
            }
            if (!TextUtils.isEmpty(hardwareVersion)) {
                jsonObject.put("hardwareVersion", hardwareVersion);
            }
            if (page != null) {
                jsonObject.put("page", page);
            }
            if (count != null) {
                jsonObject.put("count", count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeviceUpdateFirmwareDataRsp> deviceUpdateVision = retrofitService.getDeviceUpdateVision(sn, body);
        RxApiManager.getInstance().add("getDeviceUpdateVision", deviceUpdateVision.subscribe());
        return deviceUpdateVision;
    }

    /**
     * 回传版本信息
     *
     * @param sn
     * @param firmwareVersion
     * @return
     */
    public Observable<ResponseBase> upLoadDeviceUpdateVision(String sn, String firmwareVersion) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(firmwareVersion)) {
                jsonObject.put("firmwareVersion", firmwareVersion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ResponseBase> upLoadDeviceUpdateVision = retrofitService.upLoadDeviceUpdateVision(sn, body);
        RxApiManager.getInstance().add("upLoadDeviceUpdateVision", upLoadDeviceUpdateVision.subscribe());
        return upLoadDeviceUpdateVision;
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     * @param observer
     */
    public void downloadDeviceFirmwareFile(String url, final String filePath, CityObserver<Boolean> observer) {
        retrofitService.downloadDeviceFirmwareFile(url).subscribeOn(Schedulers.io()).map(new Func1<ResponseBody, Boolean>() {
            @Override
            public Boolean call(ResponseBody responseBody) {
                return writeResponseBodyToDisk(responseBody, filePath);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String filePath) {
        try {
            File futureStudioIconFile = new File(filePath);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    try {
                        LogUtils.loge("writeResponseBodyToDisk-->> file download: " + fileSizeDownloaded + " of " + fileSize);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取预警确认弹窗的配置文件
     *
     * @return
     */
    public Observable<DevicesAlarmPopupConfigRsp> getDevicesAlarmPopupConfig() {
        Observable<DevicesAlarmPopupConfigRsp> devicesAlarmPopupConfig = retrofitService.getDevicesAlarmPopupConfig();
        RxApiManager.getInstance().add("getDevicesAlarmPopupConfig", devicesAlarmPopupConfig.subscribe());
        return devicesAlarmPopupConfig;
    }


}
