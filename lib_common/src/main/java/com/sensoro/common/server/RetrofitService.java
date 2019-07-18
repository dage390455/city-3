package com.sensoro.common.server;


import com.sensoro.common.model.CameraFilterModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RetrofitService {
    //demo环境
    String SCOPE_DEMO = "https://city-demo-api.sensoro.com/";
    //测试环境
    String SCOPE_TEST = "https://city-test-api.sensoro.com/";
    //预发布环境
    String SCOPE_PRE = "https://city-pre-api.sensoro.com/";
    //    String SCOPE_PRE = "http://xiaolai.ngrok.gkzyk.com/";
    //正式环境
    String SCOPE_MASTER = "https://city-api.sensoro.com/";
    //开发环境
    String SCOPE_DEVELOPER = "https://city-dev-api.sensoro.com/";
//    String SCOPE_DEVELOPER = "https://city-antelope-proxy-dev-api.sensoro.com";
//    String SCOPE_DEVELOPER = "https://city-dev-api.sensoro.com/camera-center";

    String LOGIN = "sessions";
    String LOGOUT = "sessions/current";
    String USER_ACCOUNT_LIST = "users";
    //    String DEVICE_INFO_LIST = "prov1/devices/details/app";
    String DEVICE_INFO_LIST = "prov2/devices/details/app";
    //
    String STATION_INFO = "stations/";
    String STATION_DEPLOY = "stations/app/";

    String DEVICE_HISTORY_LIST = "prov1/logs/list/ltime/app";
    String DEVICE_ALARM_TIME = "details/alarm/ltime";
    String DEVICE_ALARM_HISTORY = "prov1/alarms/list/app";
    String DEVICE_ALARM_LOG = "alarmplay";
    String DEVICE_MALFUNCTION_LOG = "prov1/malfunctions";
    //    String DEVICE_BRIEF_LIST = "stats/device/brief/app";
    String DEVICE_BRIEF_LIST = "prov2/stats/device/brief/app";
    //    String DEVICE_TYPE_COUNT = "prov1/devices/status/count";
    String DEVICE_TYPE_COUNT = "prov2/devices/status/count";
    String DOUBLE_CHECK = "tfa/totp/verify";
    String APP_UPDATE = "http://api.fir" +
            ".im/apps/latest/599519bbca87a829360005f8?api_token=72af8ff1c6587c51e8e9a28209f71713";
    String ALARM_COUNT = "prov1/alarms/count";
    String MALFUNCTION_COUNT = "malfunctions/count";
    String INSPECT_TASK_LIST = "inspect/task/list";
    String INSPECT_TASK_EXECUTION = "/inspect/task/execution";
    String INSPECT_TASK_CHANGE_STATE = "inspect/task/status";
    String INSPECT_TASK_EXCEPTION_DETAIL = "inspect/device/_search";
    String INSPECTION_TASK_GET_TEMPLATE = "inspect/template";
    String GET_DEVICES_MERGE_TYPES = "devices/mergeTypes";
    String GET_DEVICES_ALARM_POPUP_CONFIG = "alarms/config";
    String GET_DEPOLY_RECORD_LIST = "prov1/deploy/list";
    String MONITOR_POINT_OPERATION = "devices/list/task";
    //    String DEPLOY_DEVICE_DETAIL = "devices/detail";
    String DEPLOY_DEVICE_DETAIL = "prov2/devices/detail";

    @FormUrlEncoded
    @POST(LOGIN)
    Observable<ResponseResult<UserInfo>> login(@Field("phone") String phone, @Field("password") String pwd, @Field("phoneId") String
            phoneId, @Field("phoneType") String phoneType, @Field("remember") Boolean needRemember);

    @GET(DEVICE_HISTORY_LIST)
    Observable<ResponseResult<List<DeviceHistoryInfo>>> getDeviceHistoryList(@Query("sn") String sn, @Query("count") int count);

    /**
     * type 默认给hours
     * ---！！！！废弃接口
     *
     * @param sn
     * @param beginTime
     * @param endTime
     * @param type
     * @return
     */
    @GET("details/{sn}/statistics/es")
    Observable<ResponseResult> getDeviceHistoryList(@Path("sn") String sn, @Query("beginTime") long beginTime,
                                                    @Query("endTime") long endTime, @Query("type") String type);

    @GET(DEVICE_INFO_LIST)
    Observable<ResponseResult<List<DeviceInfo>>> getDeviceDetailInfoList(@Query("sns") String sns, @Query("search") String search,
                                                                         @Query("all") int all);

    @GET(DEVICE_BRIEF_LIST)
    Observable<ResponseResult<List<DeviceInfo>>> getDeviceBriefInfoList(@Query("sns") List<String> sns, @Query("page") int page, @Query("count")
            int count, @Query("all") int all, @Query("showIndoorDevice") int showIndoorDevice,
                                                                        @Query("sensorTypes") String sensorTypes, @Query("mergeTypes") String mergeTypes, @Query("status") Integer status, @Query("search") String search);

    @GET(DEVICE_TYPE_COUNT)
    Observable<ResponseResult<DeviceTypeCount>> getDeviceTypeCount();

    @GET(DEVICE_ALARM_TIME)
    Observable<ResponseResult<AlarmTime>> getDeviceAlarmTime(@Query("sn") String sn);

    @GET(DEVICE_ALARM_LOG)
    Observable<ResponseResult<List<DeviceAlarmLogInfo>>> getDeviceAlarmLogList(@Query("count") int count, @Query("page") int page, @Query
            ("sn") String sn, @Query("deviceName") String deviceName, @Query("phone")
                                                                                       String phone, @Query("search") String search, @Query
                                                                                       ("beginTime") Long beginTime,
                                                                               @Query("endTime") Long endTime
            , @Query("unionTypes") String unionTypes);

    @GET(DEVICE_MALFUNCTION_LOG)
    Observable<ResponseResult<List<MalfunctionListInfo>>> getDeviceMalfunctionLogList(@Query("count") int count, @Query("page") int page, @Query
            ("sn") String sn, @Query("deviceName") String deviceName, @Query("search") String search, @Query
                                                                                              ("beginTime") Long beginTime,
                                                                                      @Query("endTime") Long endTime);

    @GET(USER_ACCOUNT_LIST)
    Observable<ResponseResult<List<UserInfo>>> getUserAccountList(@Query("search") String search, @Query("page") Integer page, @Query
            ("count") Integer count, @Query("offset") Integer offset, @Query("limit") Integer limit);

    @GET(APP_UPDATE)
    Observable<ResponseResult> getUpdateInfo();

    @PUT("alarmplay/{id}")
    Observable<ResponseResult<DeviceAlarmLogInfo>> doAlarmConfirm(@Path("id") String id, @Body RequestBody requestBody);

    @POST("users/{uid}/controlling")
    Observable<ResponseResult<UserInfo>> doAccountControl(@Path("uid") String uid, @Body RequestBody requestBody);

    @POST("prov2/devices/app/{sn}")
    Observable<ResponseResult<DeviceInfo>> doDevicePointDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET("devices/realStatus/{sn}")
    Observable<ResponseResult<DeviceInfo>> getRealStatus(@Path("sn") String sn);


    @GET(DEPLOY_DEVICE_DETAIL)
    Observable<ResponseResult<DeviceInfo>> getDeployDeviceDetail(@Query("sn") String sn, @Query("longitude") Double longitude, @Query("latitude") Double latitude);

    //    @HTTP(method = "DELETE", path = LOGOUT, hasBody = true)
//    Observable<ResponseBase> logout(@Header("phoneId") String phoneId, @Header("uid")
//            String uid, @Query("phoneId") String phoneId_q, @Query("uid") String uid_q, @Body RequestBody
// requestBody);
    @HTTP(method = "DELETE", path = LOGOUT, hasBody = true)
    Observable<ResponseResult> logout(@Query("phoneId") String phoneId_q, @Query("uid") String uid_q, @Body RequestBody
            requestBody);

    @GET(STATION_INFO + "{sn}")
    Observable<ResponseResult<DeployStationInfo>> getStationDetail(@Path("sn") String sn);

    //
    @POST(STATION_DEPLOY + "{sn}")
    Observable<ResponseResult<DeployStationInfo>> doStationDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET(GET_DEPOLY_RECORD_LIST)
    Observable<ResponseResult<List<DeployRecordInfo>>> getDeployRecordList(@Query("sn") String sn, @Query("search") String searchText, @Query("beginTime") Long beginTime,
                                                                           @Query("endTime") Long endTime, @Query("owners") String owners, @Query("signalQuality") String signalQuality, @Query("limit") Integer limit, @Query("offset") Integer offset, @Query("group") Boolean group);

    @PUT("alarmplay/{id}")
    Observable<ResponseResult<DeviceAlarmLogInfo>> doUpdatePhotosUrl(@Path("id") String id, @Body RequestBody requestBody);

    @GET("tools/qiniu/token")
    Observable<QiNiuToken> getQiNiuToken();

    @GET("contractsTemplate")
    Observable<ResponseResult<ArrayList<ContractsTemplateInfo>>> getContractsTemplate();

    //    @FormUrlEncoded
//    @POST("contracts")
//    Observable<ResponseBase> newContract(@Field("contract_type") Integer contractType, @Field("card_id") String
// cardId,
//                                         @Field("sex") Integer sex, @Field("enterprise_card_id") String
//                                                 enterpriseCardId,
//                                         @Field("enterprise_register_id") String enterpriseRegisterId,
//                                         @Field("customer_name") String customerName,
//                                         @Field("customer_enterprise_name") String customerEnterpriseName,
//                                         @Field("customer_enterprise_validity") Integer customerEnterpriseValidity,
//                                         @Field("customer_address") String customerAddress,
//                                         @Field("customer_phone") String customerPhone,
//                                         @Field("place_type") String placeType,
//                                         @Body RequestBody requestBody,
//                                         @Field("payTimes") int payTimes, @Field("confirmed") Boolean confirmed,
//                                         @Field("serviceTime") int serviceTime);
    @POST("contracts")
    Observable<ResponseResult<ContractAddInfo>> newContract(@Body RequestBody requestBody);

    @PUT("contracts")
    Observable<ResponseResult> modifyContract(@Body RequestBody requestBody);

    @GET("contracts/{id}")
    Observable<ResponseResult<ContractListInfo>> getContractInfo(@Path("id") String id);

    @POST("contracts/_search")
    Observable<ResponseResult<List<ContractListInfo>>> searchContract(@Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("qrcode/scan")
    Observable<ResponseResult> getLoginScanResult(@Field("qrcodeId") String qrcodeId);

    @FormUrlEncoded
    @POST("qrcode/login")
    Observable<ResponseResult> scanLoginIn(@Field("qrcodeId") String qrcodeId);

    @FormUrlEncoded
    @POST("qrcode/cancel")
    Observable<ResponseResult> scanLoginCancel(@Field("qrcodeId") String qrcodeId);

    @FormUrlEncoded
    @POST(DOUBLE_CHECK)
    Observable<ResponseResult<Boolean>> doubleCheck(@Field("code") String code);

    @GET(ALARM_COUNT)
    Observable<AlarmCountRsp> getAlarmCount(@Query("beginTime") Long beginTime, @Query("endTime") Long endTime,
                                            @Query("displayStatus") String displayStatus, @Query("sn") String sn);

    @GET(MALFUNCTION_COUNT)
    Observable<MalfunctionCountRsp> getMalfunctionCount(@Query("beginTime") Long beginTime, @Query("endTime") Long endTime,
                                                        @Query("malfunctionStatus") String malfunctionStatus, @Query("sn") String sn);

    @GET(INSPECT_TASK_LIST)
    Observable<ResponseResult<InspectionTaskModel>> getInspectTaskList(@Query("search") String search, @Query("finish") Integer finish, @Query
            ("offset") Integer offset, @Query("limit") Integer limit, @Query("startTime") Long startTime, @Query("finishTime") Long finishTime);

    @PUT("inspect/device")
    Observable<ResponseResult> uploadInspectionResult(@Body RequestBody responseBody);

    @GET("inspect/task/device")
    Observable<ResponseResult<InspectionTaskDeviceDetailModel>> getInspectionDeviceList(@Query("taskId") String taskId, @Query("search") String search, @Query("sn") String sn, @Query("finish") Integer finish,
                                                                                        @Query("deviceTypes") String deviceTypes, @Query("offset") Integer offset, @Query("limit") Integer limit);

    @GET(INSPECT_TASK_EXECUTION)
    Observable<ResponseResult<InspectionTaskExecutionModel>> getInspectTaskExecution(@Query("taskId") String taskId);

    @PUT(INSPECT_TASK_CHANGE_STATE)
    Observable<ResponseResult<ChangeInspectionTaskStateInfo>> changeInspectionTaskState(@Body RequestBody requestBody);

    @POST(INSPECT_TASK_EXCEPTION_DETAIL)
    Observable<ResponseResult<InspectionTaskExceptionDeviceModel>> getInspectionDeviceDetail(@Body RequestBody requestBody);

    @GET(INSPECTION_TASK_GET_TEMPLATE)
    Observable<ResponseResult<InspectionTaskInstructionModel>> getInspectionTemplate(@Query("deviceType") String deviceType);

    @POST("devices/change/{sn}")
    Observable<ResponseResult<DeviceInfo>> doInspectionChangeDeviceDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET(GET_DEVICES_MERGE_TYPES)
//    Observable<DevicesMergeTypesRsp> getDevicesMergeTypes(@Header("x-session-id") String sessionId);
    Observable<ResponseResult<DeviceMergeTypesInfo>> getDevicesMergeTypes();

    @PUT(MONITOR_POINT_OPERATION)
    Observable<MonitorPointOperationRequestRsp> doMonitorPointOperation(@Body RequestBody requestBody);

    @PUT("devices/gps/{sn}")
    Observable<ResponseResult<DeviceInfo>> doDevicePositionCalibration(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET("devices/valid")
//    Observable<DevicesMergeTypesRsp> getDevicesMergeTypes(@Header("x-session-id") String sessionId);
    Observable<ResponseResult> getDeviceNameValid(@Query("name") String name);

    @DELETE("prov1/accounts/controlling")
    Observable<ResponseResult<UserInfo>> backMainControlling();

    /**
     * 获取固件列表
     *
     * @param sn
     * @param requestBody
     * @return
     */
    @POST("devices/version/list/{sn}")
    Observable<ResponseResult<List<DeviceUpdateFirmwareData>>> getDeviceUpdateVision(@Path("sn") String sn, @Body RequestBody requestBody);

    /**
     * 回写固件版本到iot
     *
     * @param sn
     * @param requestBody
     * @return
     */
    @POST("devices/firmwareversion/update/{sn}")
    Observable<ResponseResult> upLoadDeviceUpdateVision(@Path("sn") String sn, @Body RequestBody requestBody);

    /**
     * 下载文件
     *
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadDeviceFirmwareFile(@Url String url);

    @GET(GET_DEVICES_ALARM_POPUP_CONFIG)
//    Observable<DevicesMergeTypesRsp> getDevicesMergeTypes(@Header("x-session-id") String sessionId);
    Observable<ResponseResult<AlarmPopupDataBean>> getDevicesAlarmPopupConfig();

    /**
     * 通过sn获取摄像头详情
     *
     * @param sn
     * @return
     */
    @GET("camera")
    Observable<ResponseResult<DeviceCameraDetailInfo>> getDeviceCamera(@Query("sn") String sn);

    /**
     * 获取用户下摄像头列表
     *
     * @param pageSize
     * @param page
     * @param search
     * @return
     */
    @GET("cameras")
    Observable<ResponseResult<List<DeviceCameraInfo>>> getDeviceCameraList(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search);


    /**
     * 获取用户下全量摄像头列表
     *
     * @return
     */
    @GET("cameras/map")
    Observable<ResponseResult> getDeviceCameraMapList();

    @GET("cameras/detail")
    Observable<ResponseResult<List<AlarmCameraLiveBean>>> getAlarmCamerasDetail(@Query("sn") String sn);

    @POST("picture/faceList")
    Observable<ResponseResult<List<DeviceCameraFacePic>>> getDeviceCameraFaceList(@Body RequestBody requestBody);

    @POST("video/queryHistoryAddress")
    Observable<ResponseResult<List<DeviceCameraHistoryBean>>> getDeviceCameraPlayHistoryAddress(@Body RequestBody requestBody);

    @GET("cameras/group/bind")
    Observable<ResponseResult<List<DeviceCameraInfo>>> getDeviceGroupCameraList(@Query("_id") String _id, @Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search);

    @POST("picture/getFaceListById")
    Observable<ResponseResult<List<DeviceCameraPersonFaceBean>>> getDeviceCameraPersonFace(@Body RequestBody requestBody);

    /**
     * 100_camera - 100.026 获取安装方式和朝向选择字典
     *
     * @return
     */
    @GET("query-dict")
    Observable<ResponseResult<List<CameraFilterModel>>> getCameraFilter();

    @GET("group/stations/fields")
    Observable<ResponseResult<List<CameraFilterModel>>> getStationFilter();

    @GET("cameras")
    Observable<ResponseResult<List<DeviceCameraInfo>>> getDeviceCameraListByFilter(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search, @QueryMap Map<String, String> mapFilter);

    @GET("stations")
    Observable<ResponseResult<List<BaseStationInfo>>> getBaseStationListByFilter(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search, @QueryMap Map<String, String> mapFilter);


    @POST("qiniu/getCloudMediaByEventId")
    Observable<ResponseResult<List<AlarmCloudVideoBean>>> getCloudVideo(@Body RequestBody requestBody);

    @POST("camera/deploy")
    Observable<ResponseResult<DeployCameraUploadInfo>> doUploadDeployCamera(@Body RequestBody requestBody);

    @POST("camera/check")
    Observable<ResponseResult<DeviceCameraDetailInfo>> getDeployCameraInfo(@Body RequestBody requestBody);

    @GET("stations/{stationsn}")
    Observable<ResponseResult<BaseStationDetailModel>> getBaseStationDetail(@Path("stationsn") String stationsn);

    @GET("stations/{stationsn}/sensor/{type}")
    Observable<ResponseResult<List<BaseStationChartDetailModel>>> getBaseStationChartDetail(@Path("stationsn") String stationsn, @Path("type") String type, @Query("interval") String interval, @Query("from") long from, @Query("to") long to);


    @PUT("stations/{stationsn}")
    Observable<ResponseResult<BaseStationDetailModel>> updateStationLocation(@Path("stationsn") String stationsn, @Body RequestBody body);


    @GET("nameplates")
    Observable<ResponseResult<List<NamePlateInfo>>> getNameplateList(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search, @Query("deviceFlag") String deviceFlag);


    @DELETE("nameplate/{nameplateId}")
    Observable<ResponseResult<Integer>> deleteNameplate(@Path("nameplateId") String nameplateId);

    @GET("nameplate/{nameplateId}")
    Observable<ResponseResult<NamePlateInfo>> getNameplateDetail(@Path("nameplateId") String nameplateId, @Query("isAuthUser") Boolean isAuthUser);

    @GET("nameplate/bind/devices")
    Observable<ResponseResult<List<NamePlateInfo>>> getNameplateBindDevices(@Query("page") Integer page, @Query("count") Integer count, @Query("nameplateId") String nameplateId);

    @PUT("nameplate/unbind/device")
    Observable<ResponseResult<Integer>> unbindNameplateDevice(@Body RequestBody requestBody);

    @PUT("nameplate/{nameplateId}")
    Observable<ResponseResult<Integer>> updateNameplate(@Path("nameplateId") String nameplateId, @Body RequestBody body);

    @GET("nameplate/unbind/devices")
    Observable<ResponseResult<List<NamePlateInfo>>> getNameplateUnbindDevices(@Query("page") Integer page, @Query("count") Integer count, @Query("nameplateId") String nameplateId, @Query("search") String searchText);

    @PUT("nameplate/deploy/{nameplateId}")
    Observable<ResponseResult<DeployNameplateInfo>> doUploadDeployNameplate(@Path("nameplateId") String nameplateId, @Body RequestBody requestBody);

    @PUT("nameplate/bind/device")
    Observable<ResponseResult<Integer>> doBindDevice(@Body RequestBody requestBody);


    @PUT("camera-center/alarms/{id}/handle")
    Observable<ResponseResult<HandleAlarmData>> handleSecurityAlarm(@Path("id") String id, @Body RequestBody requestBody);

    @GET("camera-center/alarms/{id}/events")
    Observable<ResponseResult<SecurityAlarmTimelineData>> getSecurityAlarmTimeLine(@Path("id") String id);

    @GET("camera-center/alarms")
    Observable<ResponseResult<SecurityAlarmListData>> getSecurityAlarmList(@QueryMap Map<String, Object> param);

    @GET("camera-center/alarms/{id}")
    Observable<ResponseResult<SecurityAlarmDetailInfo>> getSecurityAlarmDetails(@Path("id") String id);

    @POST("token/devices_history")
    Observable<ResponseResult<SecurityAlarmDetailInfo>> getSecurityDeviceVideoHistort(@Path("id") String id, @Body RequestBody requestBody);

    @GET("camera-center/alarms/{id}/videos")
    Observable<ResponseResult<SecurityWarnRecord>> getSecurityWarnRecord(@Path("id") String id);

    @GET("users/me")
    Observable<ResponseResult<UserInfo>> getPermissionChangeInfo();
}

