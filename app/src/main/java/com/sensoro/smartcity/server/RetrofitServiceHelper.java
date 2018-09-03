package com.sensoro.smartcity.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;
import com.sensoro.smartcity.server.response.ContractAddRsp;
import com.sensoro.smartcity.server.response.ContractsListRsp;
import com.sensoro.smartcity.server.response.ContractsTemplateRsp;
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
import com.sensoro.smartcity.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_SESSION_ID;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_URL;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_LOGIN_ID;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_SCOPE;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_SPLASH_LOGIN_DATA;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_DEMO;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_MASTER;
import static com.sensoro.smartcity.server.RetrofitService.SCOPE_TEST;

public enum RetrofitServiceHelper {
    INSTANCE;
    private static final long DEFAULT_TIMEOUT = 8 * 1000;
    private final String HEADER_SESSION_ID = "x-session-id";
    private final String HEADER_USER_AGENT = "User-Agent";
    private final String HEADER_CONTENT_TYPE = "Content-Type";
    private final String HEADER_ACCEPT = "Accept";
    private volatile int mUrlType = -1;
    private String sessionId = null;
    public volatile String BASE_URL = SCOPE_MASTER;//http://mocha-iot-api.mocha.server.sensoro.com-----http://iot-api
    private RetrofitService retrofitService;
    private final Retrofit.Builder builder;
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

    /**
     * 获取当前的sessionID（为空时从文件中获取）
     *
     * @return
     */
    public String getSessionId() {
        if (TextUtils.isEmpty(sessionId)) {
            SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                    .MODE_PRIVATE);
            sessionId = sp.getString(PREFERENCE_KEY_SESSION_ID, null);
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
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_SESSION_ID, sessionId);
        editor.apply();
    }

    public void clearSessionId() {
        this.sessionId = null;
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_LOGIN_ID, Context
                .MODE_PRIVATE).edit().clear().apply();
        SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SPLASH_LOGIN_DATA, Context
                .MODE_PRIVATE).edit().clear().apply();
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
            default:
                BASE_URL = SCOPE_MASTER;
                break;
        }
        retrofitService = builder.baseUrl(BASE_URL).build().create(RetrofitService.class);
        SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SCOPE, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREFERENCE_KEY_URL, urlType);
        editor.apply();
    }

    /**
     * 获取并设置当前的baseUrl类型
     *
     * @return
     */
    public int getBaseUrlType() {
        if (mUrlType == -1) {
            mUrlType = 0;
            try {
                SharedPreferences sp = SensoroCityApplication.getInstance().getSharedPreferences(PREFERENCE_SCOPE, Context
                        .MODE_PRIVATE);
                mUrlType = sp.getInt(PREFERENCE_KEY_URL, 0);
                if (mUrlType != 0) {
                    switch (mUrlType) {
                        case 1:
                            BASE_URL = SCOPE_DEMO;
                            break;
                        case 2:
                            BASE_URL = SCOPE_TEST;
                            break;
                        default:
                            BASE_URL = SCOPE_MASTER;
                            break;
                    }
                    retrofitService = builder.baseUrl(BASE_URL).build().create(RetrofitService.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                                Build.VERSION.RELEASE);
//                        .addHeader(HEADER_ACCEPT, "application/json")
//                        .addHeader(HEADER_CONTENT_TYPE, "application/json;charset=UTF-8");
                if (!TextUtils.isEmpty(getSessionId())) {
                    builder.addHeader(HEADER_SESSION_ID, getSessionId());
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
    public Observable<DeviceAlarmLogRsp> getDeviceAlarmLogList(int page, String sn, String deviceName, String phone,
                                                               Long beginTime, Long endTime, String unionTypes) {
        Observable<DeviceAlarmLogRsp> deviceAlarmLogList = retrofitService.getDeviceAlarmLogList(10, page, sn, deviceName, phone, beginTime, endTime, unionTypes);
        RxApiManager.getInstance().add("getDeviceAlarmLogList", deviceAlarmLogList.subscribe());
        return deviceAlarmLogList;
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
        Observable<DeviceInfoListRsp> deviceBriefInfoList = retrofitService.getDeviceBriefInfoList(page, 20, 1, 1,
                sensorTypes, status, search);
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
            name, String contact, String content, List<String> imgUrls) {
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
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<DeviceDeployRsp> deviceDeployRspObservable = retrofitService.doDevicePointDeploy(sn, body);
        RxApiManager.getInstance().add("doDevicePointDeploy", deviceDeployRspObservable.subscribe());
        return deviceDeployRspObservable;
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
        Observable<StationInfoRsp> stationInfoRspObservable = retrofitService.doStationDeploy(sn, body);
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
    public Observable<StationInfoRsp> getStationDetail(String sn) {
        Observable<StationInfoRsp> stationDetail = retrofitService.getStationDetail(sn);
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
    public Observable<DeviceAlarmItemRsp> doUpdatePhotosUrl(String id, int statusResult, int statusType, int
            statusPlace, String remark, boolean isReconfirm, List<String> imagesUrl) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("displayStatus", statusResult);
            jsonObject.put("reason", statusType);
            jsonObject.put("place", statusPlace);
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
        Observable<ContractsTemplateRsp> contractstemplate = retrofitService.getContractstemplate();
        RxApiManager.getInstance().add("getContractstemplate", contractstemplate.subscribe());
        return contractstemplate;
    }

    public Observable<ContractAddRsp> getNewContract(Integer contractType, int createType, String cardId,
                                                     Integer sex, String enterpriseCardId,
                                                     String enterpriseRegisterId,
                                                     String customerName,
                                                     String customerEnterpriseName,
                                                     String customerEnterpriseValidity,
                                                     //必选
                                                     String customerAddress,
                                                     String customerPhone,
                                                     String placeType,
                                                     ArrayList<ContractsTemplateInfo> devicesList,
                                                     int payTimes,
                                                     //可选
                                                     Boolean confirmed,
                                                     int serviceTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (contractType != null) {
                jsonObject.put("contract_type", contractType);
            }
            jsonObject.put("created_type", createType);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Observable<ContractAddRsp> contractAddRspObservable = retrofitService.newContract(body);
        RxApiManager.getInstance().add("getNewContract", contractAddRspObservable.subscribe());
        return contractAddRspObservable;
    }

    public Observable<ContractsListRsp> searchContract(Integer contractType, Long beginTime, Long endTime, Integer
            limit,
                                                       Integer offset) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jsonObject1 = new JSONObject();
            if (contractType != null) {
                jsonObject1.put("contract_type", contractType);
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

    public Observable<ResponseBase> getLoginScanResult(String qrcodeId) {
        Observable<ResponseBase> loginScanResult = retrofitService.getLoginScanResult(qrcodeId);
        RxApiManager.getInstance().add("getLoginScanResult", loginScanResult.subscribe());
        return loginScanResult;
    }

    public Observable<ResponseBase> scanLoginIn(String qrcodeId) {
        Observable<ResponseBase> responseBaseObservable = retrofitService.scanLoginIn(qrcodeId);
        RxApiManager.getInstance().add("scanLoginIn", responseBaseObservable.subscribe());
        return responseBaseObservable;
    }

    public Observable<ResponseBase> scanLoginCancel(String qrcodeId) {
        Observable<ResponseBase> responseBaseObservable = retrofitService.scanLoginCancel(qrcodeId);
        RxApiManager.getInstance().add("scanLoginCancel", responseBaseObservable.subscribe());
        return responseBaseObservable;
    }
}
