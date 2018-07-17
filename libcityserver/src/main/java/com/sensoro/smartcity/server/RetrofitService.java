package com.sensoro.smartcity.server;


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

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitService {
    String SCOPE_MOCHA = "https://demo-city-api.sensoro.com/";
    String SCOPE_MASTER = "https://city-api.sensoro.com/";

    String LOGIN = "sessions";
    String LOGOUT = "sessions/current";
    String USER_ACCOUNT_LIST = "users";
    String DEVICE_INFO_LIST = "prov1/devices/details/app";
    //
    String STATION_INFO = "stations/";
    String STATION_DEPLOY = "stations/app/";

    String DEVICE_HISTORY_LIST = "prov1/logs/list/ltime/app";
    String DEVICE_ALARM_TIME = "details/alarm/ltime";
    String DEVICE_ALARM_HISTORY = "prov1/alarms/list/app";
    String DEVICE_ALARM_LOG = "alarmplay";
    String DEVICE_BRIEF_LIST = "stats/device/brief/app";
    String DEVICE_TYPE_COUNT = "prov1/devices/status/count";
    String APP_UPDATE = "http://api.fir" +
            ".im/apps/latest/599519bbca87a829360005f8?api_token=72af8ff1c6587c51e8e9a28209f71713";

    @FormUrlEncoded
    @POST(LOGIN)
    Observable<LoginRsp> login(@Field("phone") String phone, @Field("password") String pwd, @Field("phoneId") String
            phoneId, @Field("phoneType") String phoneType);

    @GET(DEVICE_HISTORY_LIST)
    Observable<DeviceHistoryListRsp> getDeviceHistoryList(@Query("sn") String sn, @Query("count") int count);

    /**
     * type 默认给hours
     *
     * @param sn
     * @param beginTime
     * @param endTime
     * @param type
     * @return
     */
    @GET("details/{sn}/statistics/es")
    Observable<DeviceRecentRsp> getDeviceHistoryList(@Path("sn") String sn, @Query("beginTime") long beginTime,
                                                     @Query("endTime") long endTime, @Query("type") String type);

    @GET(DEVICE_INFO_LIST)
    Observable<DeviceInfoListRsp> getDeviceDetailInfoList(@Query("sns") String sns, @Query("search") String search,
                                                          @Query("all") int all);

    @GET(DEVICE_BRIEF_LIST)
    Observable<DeviceInfoListRsp> getDeviceBriefInfoList(@Query("page") int page, @Query("count")
            int count, @Query
                                                                 ("all") int all,
                                                         @Query("showIndoorDevice") int showIndoorDevice, @Query
                                                                 ("sensorTypes") String sensorTypes, @Query("status")
                                                                 Integer status, @Query("search") String search);

    @GET(DEVICE_TYPE_COUNT)
    Observable<DeviceTypeCountRsp> getDeviceTypeCount();

    @GET(DEVICE_ALARM_TIME)
    Observable<DeviceAlarmTimeRsp> getDeviceAlarmTime(@Query("sn") String sn);

    @GET(DEVICE_ALARM_LOG)
    Observable<DeviceAlarmLogRsp> getDeviceAlarmLogList(@Query("count") int count, @Query("page") int page, @Query
            ("sn") String sn, @Query("deviceName") String deviceName, @Query("phone")
                                                                String phone, @Query("beginTime") Long beginTime,
                                                        @Query("endTime") Long endTime
            , @Query("unionTypes") String unionTypes);

    @GET(USER_ACCOUNT_LIST)
    Observable<UserAccountRsp> getUserAccountList(@Query("search") String search, @Query("limit") String limit);

    @GET(APP_UPDATE)
    Observable<UpdateRsp> getUpdateInfo();

    @PUT("alarmplay/{id}")
    Observable<DeviceAlarmItemRsp> doAlarmConfirm(@Path("id") String id, @Body RequestBody requestBody);

    @POST("users/{uid}/controlling")
    Observable<UserAccountControlRsp> doAccountControl(@Path("uid") String uid, @Body RequestBody requestBody);

    @POST("devices/app/{sn}")
    Observable<DeviceDeployRsp> doDevicePointDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @HTTP(method = "DELETE", path = LOGOUT, hasBody = true)
    Observable<ResponseBase> logout(@Header("phoneId") String phoneId, @Header("uid")
            String uid, @Query("phoneId") String phoneId_q, @Query("uid") String uid_q, @Body RequestBody requestBody);

    @GET(STATION_INFO + "{sn}")
    Observable<StationInfoRsp> getStationDetail(@Path("sn") String sn);

    //
    @POST(STATION_DEPLOY + "{sn}")
    Observable<StationInfoRsp> doStationDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @PUT("alarmplay/{id}")
    Observable<DeviceAlarmItemRsp> doUpdatePhotosUrl(@Path("id") String id, @Body RequestBody requestBody);

    @GET("tools/qiniu/token")
    Observable<QiNiuToken> getQiNiuToken();

}
