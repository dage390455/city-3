package com.sensoro.common.server;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.RxApiManager;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.model.DeployContactModel;
import com.sensoro.common.model.SecurityRisksAdapterModel;
import com.sensoro.common.server.bean.AlarmCameraLiveBean;
import com.sensoro.common.server.bean.AlarmCloudVideoBean;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.AlarmTime;
import com.sensoro.common.server.bean.BaseStationChartDetailModel;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.common.server.bean.BaseStationInfo;
import com.sensoro.common.server.bean.ChangeInspectionTaskStateInfo;
import com.sensoro.common.server.bean.ContractAddInfo;
import com.sensoro.common.server.bean.ContractListInfo;
import com.sensoro.common.server.bean.ContractsTemplateInfo;
import com.sensoro.common.server.bean.DeployCameraUploadInfo;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeployNameplateInfo;
import com.sensoro.common.server.bean.DeployRecordInfo;
import com.sensoro.common.server.bean.DeployStationInfo;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.DeviceCameraDetailInfo;
import com.sensoro.common.server.bean.DeviceCameraFacePic;
import com.sensoro.common.server.bean.DeviceCameraHistoryBean;
import com.sensoro.common.server.bean.DeviceCameraInfo;
import com.sensoro.common.server.bean.DeviceCameraPersonFaceBean;
import com.sensoro.common.server.bean.DeviceHistoryInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.DeviceMergeTypesInfo;
import com.sensoro.common.server.bean.DeviceTypeCount;
import com.sensoro.common.server.bean.DeviceUpdateFirmwareData;
import com.sensoro.common.server.bean.HandleAlarmData;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetailModel;
import com.sensoro.common.server.bean.InspectionTaskExceptionDeviceModel;
import com.sensoro.common.server.bean.InspectionTaskExecutionModel;
import com.sensoro.common.server.bean.InspectionTaskInstructionModel;
import com.sensoro.common.server.bean.InspectionTaskModel;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.bean.SecurityAlarmTimelineData;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.server.response.AlarmCountRsp;
import com.sensoro.common.server.response.MalfunctionCountRsp;
import com.sensoro.common.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.common.server.response.QiNiuToken;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.bean.SecurityAlarmListData;
import com.sensoro.common.server.security.bean.SecurityWarnRecord;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sensoro.common.server.security.constants.SecurityConstants.SECURITY_ALARMLIST_PAGE_COUNT;

public class RetrofitServiceHelper {
    private static final long DEFAULT_TIMEOUT = 8 * 1000;
    private final String HEADER_SESSION_ID = "x-session-id";
    private final String HEADER_SESSION_TOKEN = "Authorization";
    private final String HEADER_USER_AGENT = "User-Agent";
    private final String HEADER_INTERNATIONALIZATION_HEADER = "accept-language";
    private final String HEADER_CONTENT_TYPE = "Content-Type";
    private final String HEADER_ACCEPT = "Accept";
    private volatile int mUrlType = -1;
    private String sessionId = null;
    private String token = null;
    public String BASE_URL = RetrofitService.SCOPE_MASTER;//http://mocha-iot-api.mocha.server.sensoro.com-----http://iot-api
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
        gsonBuilder
                //序列化null
//                .serializeNulls()
                // 设置日期时间格式，另有2个重载方法。在序列化和反序化时均生效
                .setDateFormat("yyyy-MM-dd")
                //格式化输出。设置后，gson序列号后的字符串为一个格式化的字符串
                .setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(short.class, new ShortDeserializer())
                .registerTypeAdapter(int.class, new IntDeserializer())
                .registerTypeAdapter(long.class, new LongDeserializer())
                .registerTypeAdapter(float.class, new FloatDeserializer())
                .registerTypeAdapter(double.class, new DoubleDeserializer())
                .registerTypeAdapter(Short.class, new ShortDeserializer())
                .registerTypeAdapter(Integer.class, new IntDeserializer())
                .registerTypeAdapter(Long.class, new LongDeserializer())
                .registerTypeAdapter(Float.class, new FloatDeserializer())
                .registerTypeAdapter(Double.class, new DoubleDeserializer())
                .registerTypeAdapter(String.class, new StringDeserializer())
                .registerTypeAdapter(JsonObject.class, new JsonObjectDeserializer())
                .registerTypeAdapter(JsonArray.class, new JsonArrayDeserializer());
        //
//        gsonBuilder.registerTypeHierarchyAdapter(List.class, new ListDeserializer());

        gson = gsonBuilder.create();
        //支持RxJava

        builder = new Retrofit.Builder().baseUrl(BASE_URL).client(getNewClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
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
     * 获取当前的sessionToken（为空时从文件中获取）
     *
     * @return
     */
    public String getSessionToken() {
        if (TextUtils.isEmpty(token)) {
            token = PreferencesHelper.getInstance().getSessionToken();
        }
        return token;
    }

    /**
     * 保存sessionID
     *
     * @param sessionId
     */
    public void saveSessionId(String sessionId, String token) {
        this.sessionId = sessionId;
        this.token = token;
        PreferencesHelper.getInstance().saveSessionId(sessionId, token);
    }

    /**
     * 取消登录
     */
    public void clearLoginDataSessionId() {
        this.sessionId = null;
        this.token = null;
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
                BASE_URL = RetrofitService.SCOPE_MASTER;
                break;
            case 1:
                BASE_URL = RetrofitService.SCOPE_DEMO;
                break;
            case 2:
                BASE_URL = RetrofitService.SCOPE_TEST;
                break;
            case 3:
                BASE_URL = RetrofitService.SCOPE_PRE;
                break;
            case 4:
                //开发环境
                BASE_URL = RetrofitService.SCOPE_DEVELOPER;
                break;
            case 5:
                BASE_URL = "https://" + PreferencesHelper.getInstance().getMyBaseUrl() + "-api.sensoro.com/";
                break;
            default:
                BASE_URL = RetrofitService.SCOPE_MASTER;
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
                        BASE_URL = RetrofitService.SCOPE_DEMO;
                        break;
                    case 2:
                        BASE_URL = RetrofitService.SCOPE_TEST;
                        break;
                    case 3:
                        BASE_URL = RetrofitService.SCOPE_PRE;
                        break;
                    case 4:
                        BASE_URL = RetrofitService.SCOPE_DEVELOPER;
                        break;
                    case 5:
                        //自定义
                        BASE_URL = "https://" + PreferencesHelper.getInstance().getMyBaseUrl() + "-api.sensoro.com/";
                        break;
                    default:
                        BASE_URL = RetrofitService.SCOPE_MASTER;
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
                if (!TextUtils.isEmpty(getSessionToken())) {
                    builder.header(HEADER_SESSION_TOKEN, "Bearer " + getSessionToken());
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
        cacheBuilder.noCache();
        final CacheControl cacheControl = cacheBuilder.build();
        //缓存拦截器
        final Interceptor cacheControlInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetWorkUtils.isNetworkConnected(ContextUtils.getContext())) {
                    //
//                    NetWorkStateModel netWorkStateModel = new NetWorkStateModel();
//                    netWorkStateModel.ping = false;
//                    EventBus.getDefault().post(netWorkStateModel);
                    //
                    request = request.newBuilder().cacheControl(cacheControl).build();
                }
                Response originalResponse = chain.proceed(request);
                if (NetWorkUtils.isNetworkConnected(ContextUtils.getContext())) {
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
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(ContextUtils.getContext()));
        //cache
        final File httpCacheDirectory = new File(ContextUtils.getContext().getCacheDir(), "responses");
        final int cacheSize = 10 * 1024 * 1024; // 10 MiB
        final Cache cache = new Cache(httpCacheDirectory, cacheSize);
        //
        return builder
                .addInterceptor(interceptor)
//                .addInterceptor(cacheControlInterceptor)
//                .cookieJar(cookieJar)
//                .cache(cache)
                .addNetworkInterceptor(logging)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
//        Set-Cookie: koa:sess=neIm_GoFaHWc-WcwIfv2Sw-8ClbnPKsk; path=/; expires=Fri, 07 Sep 2018 09:25:15 GMT; httponly
    }

    public void cancelAllRsp() {
        RxApiManager.getInstance().cancelAll();
        this.sessionId = null;
        this.token = null;
    }

    /**
     * 登录
     *
     * @param account
     * @param pwd
     * @param phoneId
     * @return
     */
    public Observable<ResponseResult<UserInfo>> login(String account, String pwd, String phoneId) {
        return retrofitService.login(account, pwd, phoneId, "android", true);
    }


    /**
     * 登出
     *
     * @param phoneId
     * @param uid
     * @return
     */
    public Observable<ResponseResult> logout(String phoneId, String uid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneId", phoneId);
            jsonObject.put("uid", uid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.logout(phoneId, uid, body);
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
    public Observable<ResponseResult<List<DeviceAlarmLogInfo>>> getDeviceAlarmLogList(int page, String sn, String deviceName, String phone
            , String search, Long beginTime, Long endTime, String unionTypes) {
        return retrofitService.getDeviceAlarmLogList(10, page, sn, deviceName, phone, search, beginTime, endTime, unionTypes);
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
    public Observable<ResponseResult<List<MalfunctionListInfo>>> getDeviceMalfunctionLogList(int page, String sn, String deviceName, String search, Long beginTime, Long endTime) {
        return retrofitService.getDeviceMalfunctionLogList(20, page, sn, deviceName, search, beginTime, endTime);
    }

    /**
     * 获取升级信息
     *
     * @return
     */
    public Observable<ResponseResult> getUpdateInfo() {
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

    public Observable<ResponseResult<List<DeviceInfo>>> getDeviceBriefInfoList(int page, String sensorTypes, String mergeTypes, Integer status, String
            search) {
        return retrofitService.getDeviceBriefInfoList(null, null, null, page, Constants.pageSize, 1, 1,
                sensorTypes, mergeTypes, status, search);
    }

    public Observable<ResponseResult<List<DeviceInfo>>> getDeviceBriefInfoList(List<String> sns, int page, String sensorTypes, String mergeTypes, Integer status, String
            search) {
        return retrofitService.getDeviceBriefInfoList(null, null, sns, page, Constants.pageSize, 1, 1,
                sensorTypes, mergeTypes, status, search);
    }

    public Observable<ResponseResult<List<DeviceInfo>>> getDeviceBriefInfoList(String order, String sort, int page, String sensorTypes, String mergeTypes, Integer status, String
            search) {
        return retrofitService.getDeviceBriefInfoList(order, sort, null, page, Constants.pageSize, 1, 1,
                sensorTypes, mergeTypes, status, search);
    }

    /**
     * 主页top信息
     *
     * @return
     */
    public Observable<ResponseResult<DeviceTypeCount>> getDeviceTypeCount() {
        return retrofitService.getDeviceTypeCount();
    }

    /**
     * 获取账户列表
     *
     * @param search
     * @return
     */
    public Observable<ResponseResult<List<UserInfo>>> getUserAccountList(String search, Integer page, Integer offset, Integer limit) {
        return retrofitService.getUserAccountList(search, page, 20, offset, limit);
    }

    /**
     * 账户切换
     *
     * @param uid
     * @param phoneId
     * @return
     */
    public Observable<ResponseResult<UserInfo>> doAccountControl(String uid, String phoneId) {
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
     * 查询账号下的部署记录
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public Observable<ResponseResult<List<DeployRecordInfo>>> getDeployRecordList(String sn, String searchText, Long beginTime, Long endTime, String owners, String signalQuality, Integer limit, Integer offset, Boolean group) {
        return retrofitService.getDeployRecordList(sn, searchText, beginTime, endTime, owners, signalQuality, limit, offset, group);
    }

    public Observable<ResponseResult<DeviceInfo>> getDeviceRealStatus(String sn) {
        return retrofitService.getRealStatus(sn);
    }

    /**
     * 上传部署信息数据
     *
     * @param sn
     * @param lon
     * @param lat
     * @param tags
     * @param name
     * @param contacts
     * @param wxPhone
     * @param imgUrls
     * @param deployControlSettingData
     * @param forceReason
     * @param status
     * @param signalQuality
     * @return
     */
    public Observable<ResponseResult<DeviceInfo>> doDevicePointDeploy(String sn, double lon, double lat, List<String> tags, String
            name, List<DeployContactModel> contacts, String wxPhone, List<String> imgUrls, DeployControlSettingData deployControlSettingData, String forceReason, Integer status, String signalQuality) {
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


            if (contacts != null && contacts.size() > 0) {
                JSONArray jsonArrayContact = new JSONArray();
                for (DeployContactModel contactModel : contacts) {
                    JSONObject object = new JSONObject();
                    if (!TextUtils.isEmpty(contactModel.name)) {
                        object.put("contact", contactModel.name);
                    }
                    if (!TextUtils.isEmpty(contactModel.phone)) {
                        object.put("content", contactModel.phone);
                    }
                    object.put("types", "phone");
                    jsonArrayContact.put(object);
                }
                jsonObject.put("notifications", jsonArrayContact);
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
                Integer inputValue = deployControlSettingData.getInputValue();
                if (inputValue != null) {
                    jsonObjectOut.put("inputValue", inputValue);
                }
                Integer transformer = deployControlSettingData.getTransformer();
                if (transformer != null) {
                    jsonObjectOut.put("transformer", transformer);
                }
                Integer recommTrans = deployControlSettingData.getRecommTrans();
                if (recommTrans != null) {
                    jsonObjectOut.put("recommTrans", recommTrans);
                }
                //
                List<DeployControlSettingData.wireData> input = deployControlSettingData.getInput();
                if (input != null && input.size() > 0) {
                    JSONArray jsonArrayInput = new JSONArray();
                    for (DeployControlSettingData.wireData wireData : input) {
                        JSONObject jsonObjectInput = new JSONObject();
                        Integer wireMaterial1 = wireData.getWireMaterial();
                        if (wireMaterial1 != null) {
                            jsonObjectInput.put("wireMaterial", wireMaterial1);
                        }
                        Double wireDiameter1 = wireData.getWireDiameter();
                        if (wireDiameter1 != null) {
                            jsonObjectInput.put("wireDiameter", wireDiameter1);
                        }
                        Integer count = wireData.getCount();
                        if (count != null) {
                            jsonObjectInput.put("count", count);
                        }
                        jsonArrayInput.put(jsonObjectInput);
                    }
                    jsonObjectOut.put("input", jsonArrayInput);
                }
                //
                List<DeployControlSettingData.wireData> output = deployControlSettingData.getOutput();
                if (output != null && output.size() > 0) {
                    JSONArray jsonArrayOutput = new JSONArray();
                    for (DeployControlSettingData.wireData wireData : output) {
                        JSONObject jsonObjectOutput = new JSONObject();
                        Integer wireMaterial1 = wireData.getWireMaterial();
                        if (wireMaterial1 != null) {
                            jsonObjectOutput.put("wireMaterial", wireMaterial1);
                        }
                        Double wireDiameter1 = wireData.getWireDiameter();
                        if (wireDiameter1 != null) {
                            jsonObjectOutput.put("wireDiameter", wireDiameter1);
                        }
                        Integer count = wireData.getCount();
                        if (count != null) {
                            jsonObjectOutput.put("count", count);
                        }
                        jsonArrayOutput.put(jsonObjectOutput);
                    }
                    jsonObjectOut.put("output", jsonArrayOutput);
                }
                jsonObject.put("config", jsonObjectOut);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doDevicePointDeploy(sn, body);
    }

    public Observable<ResponseResult<DeviceInfo>> doDevicePointDeploy(String sn, double lon, double lat, List<String> tags, String
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
                Integer inputValue = deployControlSettingData.getInputValue();
                if (inputValue != null) {
                    jsonObjectOut.put("inputValue", inputValue);
                }
                Integer transformer = deployControlSettingData.getTransformer();
                if (transformer != null) {
                    jsonObjectOut.put("transformer", transformer);
                }
                Integer recommTrans = deployControlSettingData.getRecommTrans();
                if (recommTrans != null) {
                    jsonObjectOut.put("recommTrans", recommTrans);
                }
                //
                List<DeployControlSettingData.wireData> input = deployControlSettingData.getInput();
                if (input != null && input.size() > 0) {
                    JSONArray jsonArrayInput = new JSONArray();
                    for (DeployControlSettingData.wireData wireData : input) {
                        JSONObject jsonObjectInput = new JSONObject();
                        Integer wireMaterial1 = wireData.getWireMaterial();
                        if (wireMaterial1 != null) {
                            jsonObjectInput.put("wireMaterial", wireMaterial1);
                        }
                        Double wireDiameter1 = wireData.getWireDiameter();
                        if (wireDiameter1 != null) {
                            jsonObjectInput.put("wireDiameter", wireDiameter1);
                        }
                        Integer count = wireData.getCount();
                        if (count != null) {
                            jsonObjectInput.put("count", count);
                        }
                        jsonArrayInput.put(jsonObjectInput);
                    }
                    jsonObjectOut.put("input", jsonArrayInput);
                }
                //
                List<DeployControlSettingData.wireData> output = deployControlSettingData.getOutput();
                if (output != null && output.size() > 0) {
                    JSONArray jsonArrayOutput = new JSONArray();
                    for (DeployControlSettingData.wireData wireData : output) {
                        JSONObject jsonObjectOutput = new JSONObject();
                        Integer wireMaterial1 = wireData.getWireMaterial();
                        if (wireMaterial1 != null) {
                            jsonObjectOutput.put("wireMaterial", wireMaterial1);
                        }
                        Double wireDiameter1 = wireData.getWireDiameter();
                        if (wireDiameter1 != null) {
                            jsonObjectOutput.put("wireDiameter", wireDiameter1);
                        }
                        Integer count = wireData.getCount();
                        if (count != null) {
                            jsonObjectOutput.put("count", count);
                        }
                        jsonArrayOutput.put(jsonObjectOutput);
                    }
                    jsonObjectOut.put("output", jsonArrayOutput);
                }
                jsonObject.put("config", jsonObjectOut);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doDevicePointDeploy(sn, body);
    }

    /**
     * 获取蓝牙信息，包括蓝牙密码和基带信息
     *
     * @param sn
     * @param longitude
     * @param latitude
     * @return
     */
    public Observable<ResponseResult<DeviceInfo>> getDeployDeviceDetail(String sn, Double longitude, Double latitude) {
        return retrofitService.getDeployDeviceDetail(sn, longitude, latitude);
    }

    public Observable<ResponseResult<DeviceInfo>> doInspectionChangeDeviceDeploy(String oldSn, String newSn, String taskId, Integer reason, double lon, double lat, List<String> tags, String
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
        return retrofitService.doInspectionChangeDeviceDeploy(oldSn, body);
    }

    public Observable<ResponseResult<DeviceInfo>> doInspectionChangeDeviceDeploy(String oldSn, String newSn, String taskId, Integer reason, double lon, double lat, List<String> tags, String
            name, List<DeployContactModel> contacts, List<String> imgUrls, String wxPhone, String forceReason, Integer status, String signalQuality) {
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


            if (contacts != null && contacts.size() > 0) {
                JSONArray jsonArrayContact = new JSONArray();
                for (DeployContactModel contactModel : contacts) {
                    JSONObject object = new JSONObject();
                    if (!TextUtils.isEmpty(contactModel.name)) {
                        object.put("contact", contactModel.name);
                    }
                    if (!TextUtils.isEmpty(contactModel.phone)) {
                        object.put("content", contactModel.phone);
                    }
                    object.put("types", "phone");
                    jsonArrayContact.put(object);
                }
                jsonObject.put("notifications", jsonArrayContact);
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
        return retrofitService.doInspectionChangeDeviceDeploy(oldSn, body);
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
    public Observable<ResponseResult<DeployStationInfo>> doStationDeploy(String sn, double lon, double lat, List<String> tags, String
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
    public Observable<ResponseResult<DeviceAlarmLogInfo>> doAlarmConfirm(String id, int status, String remark, boolean isReconfirm) {
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
    public Observable<ResponseResult<AlarmTime>> getDeviceAlarmTime(String sn) {
        return retrofitService.getDeviceAlarmTime(sn);
    }

    public Observable<ResponseResult<List<DeviceHistoryInfo>>> getDeviceHistoryList(String sn, int count) {
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
    public Observable<ResponseResult> getDeviceHistoryList(String sn, long startTime, long endTime) {
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
    public Observable<ResponseResult<List<DeviceInfo>>> getDeviceDetailInfoList(String sns, String search, int all) {
        return retrofitService.getDeviceDetailInfoList(sns, search, all);
    }

    /**
     * 返回单个基站详情
     *
     * @param sn
     * @return
     */
    public Observable<ResponseResult<DeployStationInfo>> getStationDetail(String sn) {
        return retrofitService.getStationDetail(sn);
    }


    /**
     * 修改提交确认预警信息备注(图片)
     *
     * @param id
     * @param remark
     * @return
     */
    public Observable<ResponseResult<DeviceAlarmLogInfo>> doUpdatePhotosUrl(String id, Integer displayStatus, Integer reason, Integer
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
        return retrofitService.doUpdatePhotosUrl(id, body);
    }

    /**
     * 修改提交确认预警信息备注(图片)
     *
     * @param id
     * @param remark
     * @return
     */
    public Observable<ResponseResult<DeviceAlarmLogInfo>> doUpdatePhotosUrl(String id, Map<String, Integer> map, List<SecurityRisksAdapterModel> alarmPopupDangerDataList, String remark, boolean isReconfirm, List<ScenesData> scenesDataList) {
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
                for (SecurityRisksAdapterModel alarmPopupDangerData : alarmPopupDangerDataList) {
                    JSONObject jsonObjectDanger = new JSONObject();
                    jsonObjectDanger.put("place", alarmPopupDangerData.place);
                    if (alarmPopupDangerData.action.size() > 0) {
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
            if (scenesDataList != null && scenesDataList.size() > 0) {
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

    /**
     * 获取合同模板
     *
     * @return
     */
    public Observable<ResponseResult<ArrayList<ContractsTemplateInfo>>> getContractstemplate() {
        return retrofitService.getContractsTemplate();
    }

    public Observable<ResponseResult<ContractAddInfo>> getNewContract(Integer contractType, String cardId,
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
        return retrofitService.newContract(body);
    }

    public Observable<ResponseResult<ContractListInfo>> getContractInfo(String id) {
        return retrofitService.getContractInfo(id);
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
    public Observable<ResponseResult<List<ContractListInfo>>> searchContract(Integer contractType, String customerName, Integer confirmed, Long beginTime, Long endTime, Integer
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
        return retrofitService.searchContract(body);
    }

    /**
     * 检索大屏登录扫描结果
     *
     * @param qrcodeId
     * @return
     */
    public Observable<ResponseResult> getLoginScanResult(String qrcodeId) {
        return retrofitService.getLoginScanResult(qrcodeId);
    }

    /**
     * 进入大屏登录
     *
     * @param qrcodeId
     * @return
     */
    public Observable<ResponseResult> scanLoginIn(String qrcodeId) {
        return retrofitService.scanLoginIn(qrcodeId);
    }

    /**
     * 取消大屏登录
     *
     * @param qrcodeId
     * @return
     */
    public Observable<ResponseResult> scanLoginCancel(String qrcodeId) {
        return retrofitService.scanLoginCancel(qrcodeId);
    }

    /**
     * 二次验证
     *
     * @param code
     * @return
     */
    public Observable<ResponseResult<Boolean>> doubleCheck(String code) {
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

    public Observable<ResponseResult> doUploadInspectionResult(String id, String sn, String taskId, Integer status,
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
        return retrofitService.uploadInspectionResult(body);
    }

    public Observable<ResponseResult<InspectionTaskExecutionModel>> getInspectTaskExecution(String taskId) {
        return retrofitService.getInspectTaskExecution(taskId);
    }

    public Observable<ResponseResult<InspectionTaskDeviceDetailModel>> getInspectionDeviceList(String taskId, String search, String sn, Integer finish, String deviceTypes, Integer offset, Integer limit) {
//        StringBuilder stringBuilder = new StringBuilder();
//        if (deviceTypes!=null&&deviceTypes.size()>0){
//            for (String deviceType:deviceTypes){
//                stringBuilder.append(deviceType);
//            }
//        }
        return retrofitService.getInspectionDeviceList(taskId, search, sn, finish, deviceTypes, offset, limit);
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
    public Observable<ResponseResult<InspectionTaskModel>> getInspectTaskList(String search, Integer finish, Integer offset, Integer
            limit, Long startTime, Long finishTime) {
        return retrofitService.getInspectTaskList(search, finish, offset, limit,
                startTime, finishTime);
    }

    /**
     * 改变巡检任务状态，目前只能改为1，执行中
     *
     * @param id
     * @param identifier
     * @param status
     * @return
     */
    public Observable<ResponseResult<ChangeInspectionTaskStateInfo>> doChangeInspectionTaskState(String id, String identifier, Integer status) {
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
        return retrofitService.changeInspectionTaskState(body);
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
    public Observable<ResponseResult<InspectionTaskExceptionDeviceModel>> getInspectionDeviceDetail(String id, String sn, String taskId, Integer device) {
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
        return retrofitService.getInspectionDeviceDetail(body);
    }

    /**
     * 获取巡检内容模板
     *
     * @param deviceType
     * @return
     */
    public Observable<ResponseResult<InspectionTaskInstructionModel>> getInspectionTemplate(String deviceType) {
        return retrofitService.getInspectionTemplate(deviceType);
    }

    /**
     * 获取deviceType 对应关系
     *
     * @return
     */
    public Observable<ResponseResult<DeviceMergeTypesInfo>> getDevicesMergeTypes() {
        return retrofitService.getDevicesMergeTypes();
    }

    public Observable<MonitorPointOperationRequestRsp> doMonitorPointOperation(List<String> snList, String type, Integer interval, List<String> rules, Integer inputValue, Integer switchSpec, Integer wireMaterial, Double diameter, Integer beepMuteTime) {
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
            if (inputValue != null) {
                jsonObjectConfig.put("inputValue", inputValue);
            }
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
            if (beepMuteTime != null) {
                JSONObject jsonParameters = new JSONObject();
                jsonParameters.put("beepMuteTime", beepMuteTime);
                jsonObject.put("parameters", jsonParameters);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doMonitorPointOperation(body);
    }

    /**
     * 针对三相电的
     *
     * @param snList
     * @param type
     * @param deployControlSettingData
     * @return
     */
    public Observable<MonitorPointOperationRequestRsp> doMonitorPointOperation(List<String> snList, String type, DeployControlSettingData deployControlSettingData) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray jsonSnList = new JSONArray();
            for (String sn : snList) {
                jsonSnList.put(sn);
            }
            jsonObject.put("snList", jsonSnList);
            jsonObject.put("type", type);
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
                Integer inputValue = deployControlSettingData.getInputValue();
                if (inputValue != null) {
                    jsonObjectOut.put("inputValue", inputValue);
                }
                Integer transformer = deployControlSettingData.getTransformer();
                if (transformer != null) {
                    jsonObjectOut.put("transformer", transformer);
                }
                Integer recommTrans = deployControlSettingData.getRecommTrans();
                if (recommTrans != null) {
                    jsonObjectOut.put("recommTrans", recommTrans);
                }
                //
                List<DeployControlSettingData.wireData> input = deployControlSettingData.getInput();
                if (input != null && input.size() > 0) {
                    JSONArray jsonArrayInput = new JSONArray();
                    for (DeployControlSettingData.wireData wireData : input) {
                        JSONObject jsonObjectInput = new JSONObject();
                        Integer wireMaterial1 = wireData.getWireMaterial();
                        if (wireMaterial1 != null) {
                            jsonObjectInput.put("wireMaterial", wireMaterial1);
                        }
                        Double wireDiameter1 = wireData.getWireDiameter();
                        if (wireDiameter1 != null) {
                            jsonObjectInput.put("wireDiameter", wireDiameter1);
                        }
                        Integer count = wireData.getCount();
                        if (count != null) {
                            jsonObjectInput.put("count", count);
                        }
                        jsonArrayInput.put(jsonObjectInput);
                    }
                    jsonObjectOut.put("input", jsonArrayInput);
                }
                //
                List<DeployControlSettingData.wireData> output = deployControlSettingData.getOutput();
                if (output != null && output.size() > 0) {
                    JSONArray jsonArrayOutput = new JSONArray();
                    for (DeployControlSettingData.wireData wireData : input) {
                        JSONObject jsonObjectOutput = new JSONObject();
                        Integer wireMaterial1 = wireData.getWireMaterial();
                        if (wireMaterial1 != null) {
                            jsonObjectOutput.put("wireMaterial", wireMaterial1);
                        }
                        Double wireDiameter1 = wireData.getWireDiameter();
                        if (wireDiameter1 != null) {
                            jsonObjectOutput.put("wireDiameter", wireDiameter1);
                        }
                        Integer count = wireData.getCount();
                        if (count != null) {
                            jsonObjectOutput.put("count", count);
                        }
                        jsonArrayOutput.put(jsonObjectOutput);
                    }
                    jsonObjectOut.put("output", jsonArrayOutput);
                }
                jsonObject.put("config", jsonObjectOut);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doMonitorPointOperation(body);
    }

    public Observable<ResponseResult<DeviceInfo>> doDevicePositionCalibration(String sn, Double lon, Double lat) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        return retrofitService.doDevicePositionCalibration(sn, body);
    }

    public Observable<ResponseResult> modifyContract(String uid, Integer contractID, Integer contractType, String cardId, Integer sex, String enterpriseCardId,
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
        return retrofitService.modifyContract(body);
    }

    /**
     * 检测设备名称是否重名
     *
     * @param name
     * @return
     */
    public Observable<ResponseResult> getDeviceNameValid(String name) {
        return retrofitService.getDeviceNameValid(name);
    }

    /**
     * 解除账号控制返回主账户
     *
     * @return
     */
    public Observable<ResponseResult<UserInfo>> backMainAccount() {
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
    public Observable<ResponseResult<List<DeviceUpdateFirmwareData>>> getDeviceUpdateVision(String sn, String deviceType, String band, String fromVersion, String hardwareVersion, Integer page, Integer count) {
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
        return retrofitService.getDeviceUpdateVision(sn, body);
    }

    /**
     * 回传版本信息
     *
     * @param sn
     * @param firmwareVersion
     * @return
     */
    public Observable<ResponseResult> upLoadDeviceUpdateVision(String sn, String firmwareVersion) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(firmwareVersion)) {
                jsonObject.put("firmwareVersion", firmwareVersion);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.upLoadDeviceUpdateVision(sn, body);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     * @param observer
     */
    public void downloadDeviceFirmwareFile(String url, final String filePath, CityObserver<Boolean> observer) {
        retrofitService.downloadDeviceFirmwareFile(url).subscribeOn(Schedulers.io()).map(new Function<ResponseBody, Boolean>() {
            @Override
            public Boolean apply(ResponseBody responseBody) {
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
    public Observable<ResponseResult<AlarmPopupDataBean>> getDevicesAlarmPopupConfig() {
        return retrofitService.getDevicesAlarmPopupConfig();
    }

    /**
     * 通过sn获取摄像头详情
     *
     * @param sn
     * @return
     */
    public Observable<ResponseResult<DeviceCameraDetailInfo>> getDeviceCamera(String sn) {
        return retrofitService.getDeviceCamera(sn);
    }

    /**
     * 获取用户下摄像头列表
     *
     * @param pageSize
     * @param page
     * @param search
     * @return
     */
    public Observable<ResponseResult<List<DeviceCameraInfo>>> getDeviceCameraList(Integer pageSize, Integer page, String search) {
        return retrofitService.getDeviceCameraList(pageSize, page, search);
    }

    /**
     * 获取用户下全量摄像头列表
     *
     * @return
     */
    public Observable<ResponseResult> getDeviceCameraMapList() {
        return retrofitService.getDeviceCameraMapList();
    }

    /**
     * 获取摄像头详情
     *
     * @param pageSize
     * @param startTime
     * @param endTime
     * @return
     */
    public Observable<ResponseResult<List<DeviceCameraFacePic>>> getDeviceCameraFaceList(List<String> cids, Integer pageSize, Integer limit, String minID, String startTime, String endTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (cids != null && cids.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String cid : cids) {
                    jsonArray.put(cid);
                }
                jsonObject.put("cids", jsonArray);
            }
            if (pageSize != null) {
                jsonObject.put("pageSize", pageSize);
            }
            if (limit != null) {
                jsonObject.put("limit", limit);
            }
            if (minID != null) {
                jsonObject.put("minId", minID);
            }

            if (!TextUtils.isEmpty(startTime)) {
                jsonObject.put("startTime", startTime);
            }
            if (!TextUtils.isEmpty(endTime)) {
                jsonObject.put("endTime", endTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.getDeviceCameraFaceList(body);
    }

    public Observable<ResponseResult<List<AlarmCameraLiveBean>>> getAlarmCamerasDetail(List<String> cameraIds) {
        StringBuilder sb = new StringBuilder();
        if (cameraIds != null && cameraIds.size() > 0) {
            for (String cameraId : cameraIds) {
                sb.append(cameraId).append(",");
            }

            sb.deleteCharAt(sb.length() - 1);
        }
        return retrofitService.getAlarmCamerasDetail(sb.toString());

    }

    public Observable<ResponseResult<List<DeviceCameraHistoryBean>>> getDeviceCameraPlayHistoryAddress(String cid, String beginTime, String endTime, String mediaType) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(cid)) {
                jsonObject.put("cid", cid);
            }

            if (!TextUtils.isEmpty(beginTime)) {
                jsonObject.put("beginTime", beginTime);
            }
            if (!TextUtils.isEmpty(endTime)) {
                jsonObject.put("endTime", endTime);
            }
            if (!TextUtils.isEmpty(mediaType)) {
                jsonObject.put("mediaType", mediaType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.getDeviceCameraPlayHistoryAddress(body);
    }

    public Observable<ResponseResult<List<DeviceCameraInfo>>> getDeviceGroupCameraList(String _id, Integer pageSize, Integer page, String search) {
        return retrofitService.getDeviceGroupCameraList(_id, pageSize, page, search);
    }

    public Observable<ResponseResult<List<DeviceCameraPersonFaceBean>>> getDeviceCameraPersonFace(String id,
                                                                                                  Long startTime, Long endTime,
                                                                                                  Integer score, Integer offset, Integer limit, List<String> cameraIds) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(id)) {
                jsonObject.put("id", id);
            }

            if (startTime != null) {
                jsonObject.put("startTime", startTime);
            }

            if (endTime != null) {
                jsonObject.put("endTime", endTime);
            }

            if (offset != null) {
                jsonObject.put("offset", offset);
            }

            if (limit != null) {
                jsonObject.put("limit", limit);
            }

            if (score != null) {
                jsonObject.put("score", score);
            }

            if (cameraIds != null && cameraIds.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String cameraId : cameraIds) {
                    jsonArray.put(cameraId);
                }
                jsonObject.put("cameraIds", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.getDeviceCameraPersonFace(requestBody);

    }

    /**
     * 100.026 获取安装方式和朝向选择字典
     *
     * @return
     */
    public Observable<ResponseResult<List<CameraFilterModel>>> getCameraFilter() {
        return retrofitService.getCameraFilter();
    }

    public Observable<ResponseResult<List<CameraFilterModel>>> getStationFilter() {
        return retrofitService.getStationFilter();
    }

    public Observable<ResponseResult<List<DeviceCameraInfo>>> getDeviceCameraListByFilter(Integer pageSize, Integer page, String search, Map<String, String> mapFilter) {
        return retrofitService.getDeviceCameraListByFilter(pageSize, page, search, mapFilter);
    }


    public Observable<ResponseResult<List<BaseStationInfo>>> getBaseStationListByFilter(Integer pageSize, Integer page, String search, Map<String, String> mapFilter) {
        return retrofitService.getBaseStationListByFilter(pageSize, page, search, mapFilter);
    }

    public Observable<ResponseResult<BaseStationDetailModel>> getBaseStatioDetail(String stationsn) {
        return retrofitService.getBaseStationDetail(stationsn);
    }

    public Observable<ResponseResult<List<BaseStationChartDetailModel>>> getBaseStationChartDetail(String stationsn, String type, String interval, long from, long to) {
        return retrofitService.getBaseStationChartDetail(stationsn, type, interval, from, to);
    }

    public Observable<ResponseResult<List<NamePlateInfo>>> getNameplateList(Integer pageSize, Integer page, String search, String deviceFlag, String deployFlag) {
        return retrofitService.getNameplateList(pageSize, page, search, deviceFlag);
    }

    public Observable<ResponseResult<Integer>> deleteNameplate(String nameplateId) {
        return retrofitService.deleteNameplate(nameplateId);
    }

    public Observable<ResponseResult<NamePlateInfo>> getNameplateDetail(String nameplateId, Boolean isAuthUser) {
        return retrofitService.getNameplateDetail(nameplateId, isAuthUser);
    }

    public Observable<ResponseResult<List<NamePlateInfo>>> getNameplateBindDevices(Integer page, Integer count, String nameplateId) {
//        return retrofitService.getNameplateBindDevices(nameplateId);
        return retrofitService.getNameplateBindDevices(page, count, nameplateId);
    }

    public Observable<ResponseResult<List<NamePlateInfo>>> getNameplateUnbindDevices(Integer page, Integer count, String nameplateId, String searchText) {
        return retrofitService.getNameplateUnbindDevices(page, count, nameplateId, searchText);
    }


    /**
     * 解绑铭牌设备
     *
     * @param nameplateId
     * @param sns
     * @return
     */
    public Observable<ResponseResult<Integer>> unbindNameplateDevice(String nameplateId, List<String> sns) {
        JSONObject jsonObject = new JSONObject();
        if (sns != null && sns.size() > 0) {
            try {
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < sns.size(); i++) {
                    jsonArray.put(sns.get(i));
                }
                jsonObject.put("nameplateId", nameplateId);
                jsonObject.put("snList", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.unbindNameplateDevice(requestBody);
    }


    /**
     * 更新铭牌基本信息
     *
     * @param nameplateId
     * @param name
     * @param tags
     * @return
     */
    public Observable<ResponseResult<Integer>> updateNameplate(String nameplateId, String name, List<String> tags) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            if (null != tags && tags.size() > 0) {
                for (String tag : tags) {
                    jsonArray.put(tag);
                }
            }
            jsonObject.put("tags", jsonArray);
            if (!TextUtils.isEmpty(name)) {
                jsonObject.put("name", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.updateNameplate(nameplateId, body);
    }
//    public Observable<BaseStationDetailRsp> updateStationLocation(String stationsn) {
//        return retrofitService.updateStationLocation(stationsn);
//    }


    public Observable<ResponseResult<BaseStationDetailModel>> updateStationLocation(String sn, Double lon, Double lat) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        return retrofitService.updateStationLocation(sn, body);
    }

    public Observable<ResponseResult<List<AlarmCloudVideoBean>>> getCloudVideo(String[] eventIds) {
        JSONObject jsonObject = new JSONObject();
        if (eventIds != null && eventIds.length > 0) {
            try {
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < eventIds.length; i++) {
                    jsonArray.put(eventIds[i]);
                }
                jsonObject.put("eventIds", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.getCloudVideo(requestBody);
    }

    /**
     * 摄像头部署
     *
     * @param sn
     * @param name
     * @param label
     * @param mobilePhone
     * @param latitude
     * @param longitude
     * @param imgUrls
     * @param location
     * @param installationMode
     * @param orientation
     * @return
     */
    public Observable<ResponseResult<DeployCameraUploadInfo>> doUploadDeployCamera(String sn, String name, List<String> label, String mobilePhone, String latitude, String longitude,
                                                                                   List<String> imgUrls, String location, String installationMode, String orientation, String createStatus) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(sn)) {
                jsonObject.put("sn", sn);
            }
            if (!TextUtils.isEmpty(name)) {
                jsonObject.put("name", name);
            }
            if (!TextUtils.isEmpty(mobilePhone)) {
                jsonObject.put("mobilePhone", mobilePhone);
            }
            if (!TextUtils.isEmpty(latitude)) {
                jsonObject.put("latitude", latitude);
            }
            if (!TextUtils.isEmpty(longitude)) {
                jsonObject.put("longitude", longitude);
            }
            if (!TextUtils.isEmpty(location)) {
                jsonObject.put("location", location);
            }
            if (!TextUtils.isEmpty(installationMode)) {
                jsonObject.put("installationMode", installationMode);
            }
            if (!TextUtils.isEmpty(orientation)) {
                jsonObject.put("orientation", orientation);
            }
            if (!TextUtils.isEmpty(createStatus)) {
                jsonObject.put("createStatus", createStatus);
            }
            if (label != null && label.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String tag : label) {
                    jsonArray.put(tag);
                }
                jsonObject.put("label", jsonArray);
            }
            if (imgUrls != null && imgUrls.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String url : imgUrls) {
                    jsonArray.put(url);
                }
                jsonObject.put("imgUrls", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doUploadDeployCamera(requestBody);
    }

    /**
     * 获取部署接口
     *
     * @param sn
     * @return
     */
    public Observable<ResponseResult<DeviceCameraDetailInfo>> getDeployCameraInfo(String sn) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(sn)) {
                jsonObject.put("sn", sn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.getDeployCameraInfo(requestBody);
    }

    public Observable<ResponseResult<DeployNameplateInfo>> doUploadDeployNameplate(@NonNull String nameplateId, String name, List<String> tags, ArrayList<String> imgUrls, ArrayList<NamePlateInfo> bindList) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(name)) {
                jsonObject.put("name", name);
            }

            if (tags != null && tags.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String tag : tags) {
                    jsonArray.put(tag);
                }
                jsonObject.put("tags", jsonArray);

            }
            if (imgUrls != null && imgUrls.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (String imgUrl : imgUrls) {
                    jsonArray.put(imgUrl);
                }
                jsonObject.put("deployPics", jsonArray);
            }
            if (bindList != null && bindList.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                for (NamePlateInfo info : bindList) {
                    jsonArray.put(info.getSn());
                }
                jsonObject.put("snList", jsonArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doUploadDeployNameplate(nameplateId, requestBody);
    }

    public Observable<ResponseResult<Integer>> doBindDevices(String nameplateId, ArrayList<NamePlateInfo> snList) {
        JSONObject jsonObject = new JSONObject();
        if (snList != null && snList.size() > 0) {
            try {
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < snList.size(); i++) {
                    jsonArray.put(snList.get(i).getSn());
                }
                jsonObject.put("nameplateId", nameplateId);
                jsonObject.put("snList", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.doBindDevice(requestBody);
    }
    //安防=============

    /**
     * 处理安防预警信息
     *
     * @param id              预警id
     * @param isEffective     处理结果，0-无效/1-有效.
     * @param operationDetail 处理备注信息
     * @return
     */
    public Observable<ResponseResult<HandleAlarmData>> handleSecurityAlarm(@NonNull String id, int isEffective, String operationDetail) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("isEffective", isEffective);
            if (!TextUtils.isEmpty(operationDetail)) {
                jsonObject.put("operationDetail", operationDetail);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return retrofitService.handleSecurityAlarm(id, requestBody);
    }

    /**
     * 获取安防预警详情时间轴事件
     *
     * @param id 安防预警ID
     * @return
     */
    public Observable<ResponseResult<SecurityAlarmTimelineData>> getSecurityAlarmTimeLine(@NonNull String id) {
        return retrofitService.getSecurityAlarmTimeLine(id);
    }

    /**
     * 获取安防预警列表
     *
     * @param startTime          查询范围开始时间 精确到ms
     * @param endTime            查询范围结束时间 精确到ms
     * @param alarmOperationType 预警操作类型，1-已处理/2-未处理/3-有效/4-无效
     * @param searchText         搜索关键字
     * @param alarmType          预警日志类型，1-重点人员/2-外来人员/3-人员入侵
     *                           查询条数，默认20
     * @param offset             查询起始位置，默认0
     * @return
     */
    public Observable<ResponseResult<SecurityAlarmListData>> getSecurityAlarmList(int offset, String startTime, String endTime, int alarmOperationType,
                                                                                  String searchText, int alarmType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("limit", SECURITY_ALARMLIST_PAGE_COUNT);
        paramMap.put("offset", offset);
        if (!TextUtils.isEmpty(searchText)) {
            paramMap.put("searchText", searchText);
        }
        if (0 != alarmOperationType) {
            paramMap.put("alarmOperationType", alarmOperationType);
        }
        if (0 != alarmType) {
            paramMap.put("alarmType", alarmType);
        }
        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
            paramMap.put("startTime", startTime);
            paramMap.put("endTime", endTime);
        }
        return retrofitService.getSecurityAlarmList(paramMap);

    }

    /**
     * 获取预警详情信息
     *
     * @param id 预警id.
     * @return
     */
    public Observable<ResponseResult<SecurityAlarmDetailInfo>> getSecurityAlarmDetails(@NonNull String id) {
        return retrofitService.getSecurityAlarmDetails(id);
    }

    /**
     * 获取预警录像信息
     *
     * @param id
     * @return
     */
    public Observable<ResponseResult<SecurityWarnRecord>> getSecurityWarnRecord(@NonNull String id) {
        return retrofitService.getSecurityWarnRecord(id);
    }

    public Observable<ResponseResult<UserInfo>> getPermissionChangeInfo() {
        return retrofitService.getPermissionChangeInfo();
    }
}
