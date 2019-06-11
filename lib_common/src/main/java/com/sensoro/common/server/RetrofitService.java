package com.sensoro.common.server;


import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.AlarmCameraLiveRsp;
import com.sensoro.common.server.response.AlarmCloudVideoRsp;
import com.sensoro.common.server.response.AlarmCountRsp;
import com.sensoro.common.server.response.AuthRsp;
import com.sensoro.common.server.response.BaseStationChartDetailRsp;
import com.sensoro.common.server.response.BaseStationDetailRsp;
import com.sensoro.common.server.response.BaseStationListRsp;
import com.sensoro.common.server.response.CameraFilterRsp;
import com.sensoro.common.server.response.ChangeInspectionTaskStateRsp;
import com.sensoro.common.server.response.ContractAddRsp;
import com.sensoro.common.server.response.ContractInfoRsp;
import com.sensoro.common.server.response.ContractsListRsp;
import com.sensoro.common.server.response.ContractsTemplateRsp;
import com.sensoro.common.server.response.DeleteNamePlateRsp;
import com.sensoro.common.server.response.DeployCameraUploadRsp;
import com.sensoro.common.server.response.DeployDeviceDetailRsp;
import com.sensoro.common.server.response.DeployNameplateRsp;
import com.sensoro.common.server.response.DeployRecordRsp;
import com.sensoro.common.server.response.DeployStationInfoRsp;
import com.sensoro.common.server.response.DeviceAlarmItemRsp;
import com.sensoro.common.server.response.DeviceAlarmLogRsp;
import com.sensoro.common.server.response.DeviceAlarmTimeRsp;
import com.sensoro.common.server.response.DeviceCameraDetailRsp;
import com.sensoro.common.server.response.DeviceCameraFacePicListRsp;
import com.sensoro.common.server.response.DeviceCameraHistoryRsp;
import com.sensoro.common.server.response.DeviceCameraListRsp;
import com.sensoro.common.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.common.server.response.DeviceDeployRsp;
import com.sensoro.common.server.response.DeviceHistoryListRsp;
import com.sensoro.common.server.response.DeviceInfoListRsp;
import com.sensoro.common.server.response.DeviceRecentRsp;
import com.sensoro.common.server.response.DeviceTypeCountRsp;
import com.sensoro.common.server.response.DeviceUpdateFirmwareDataRsp;
import com.sensoro.common.server.response.DevicesAlarmPopupConfigRsp;
import com.sensoro.common.server.response.DevicesMergeTypesRsp;
import com.sensoro.common.server.response.InspectionTaskDeviceDetailRsp;
import com.sensoro.common.server.response.InspectionTaskExceptionDeviceRsp;
import com.sensoro.common.server.response.InspectionTaskExecutionRsp;
import com.sensoro.common.server.response.InspectionTaskInstructionRsp;
import com.sensoro.common.server.response.InspectionTaskModelRsp;
import com.sensoro.common.server.response.LoginRsp;
import com.sensoro.common.server.response.MalfunctionCountRsp;
import com.sensoro.common.server.response.MalfunctionListRsp;
import com.sensoro.common.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.common.server.response.NamePlateListRsp;
import com.sensoro.common.server.response.NameplateBindDeviceRsp;
import com.sensoro.common.server.response.QiNiuToken;
import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.server.response.UpdateRsp;
import com.sensoro.common.server.response.UserAccountControlRsp;
import com.sensoro.common.server.response.UserAccountRsp;

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
    Observable<LoginRsp> login(@Field("phone") String phone, @Field("password") String pwd, @Field("phoneId") String
            phoneId, @Field("phoneType") String phoneType, @Field("remember") Boolean needRemember);

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
            int count, @Query("all") int all, @Query("showIndoorDevice") int showIndoorDevice,
                                                         @Query("sensorTypes") String sensorTypes, @Query("mergeTypes") String mergeTypes, @Query("status") Integer status, @Query("search") String search);

    @GET(DEVICE_TYPE_COUNT)
    Observable<DeviceTypeCountRsp> getDeviceTypeCount();

    @GET(DEVICE_ALARM_TIME)
    Observable<DeviceAlarmTimeRsp> getDeviceAlarmTime(@Query("sn") String sn);

    @GET(DEVICE_ALARM_LOG)
    Observable<DeviceAlarmLogRsp> getDeviceAlarmLogList(@Query("count") int count, @Query("page") int page, @Query
            ("sn") String sn, @Query("deviceName") String deviceName, @Query("phone")
                                                                String phone, @Query("search") String search, @Query
                                                                ("beginTime") Long beginTime,
                                                        @Query("endTime") Long endTime
            , @Query("unionTypes") String unionTypes);

    @GET(DEVICE_MALFUNCTION_LOG)
    Observable<MalfunctionListRsp> getDeviceMalfunctionLogList(@Query("count") int count, @Query("page") int page, @Query
            ("sn") String sn, @Query("deviceName") String deviceName, @Query("search") String search, @Query
                                                                       ("beginTime") Long beginTime,
                                                               @Query("endTime") Long endTime);

    @GET(USER_ACCOUNT_LIST)
    Observable<UserAccountRsp> getUserAccountList(@Query("search") String search, @Query("page") Integer page, @Query
            ("count") Integer count, @Query("offset") Integer offset, @Query("limit") Integer limit);

    @GET(APP_UPDATE)
    Observable<UpdateRsp> getUpdateInfo();

    @PUT("alarmplay/{id}")
    Observable<DeviceAlarmItemRsp> doAlarmConfirm(@Path("id") String id, @Body RequestBody requestBody);

    @POST("users/{uid}/controlling")
    Observable<UserAccountControlRsp> doAccountControl(@Path("uid") String uid, @Body RequestBody requestBody);

    @POST("prov2/devices/app/{sn}")
    Observable<DeviceDeployRsp> doDevicePointDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET("devices/realStatus/{sn}")
    Observable<DeviceDeployRsp> getRealStatus(@Path("sn") String sn);


    @GET(DEPLOY_DEVICE_DETAIL)
    Observable<DeployDeviceDetailRsp> getDeployDeviceDetail(@Query("sn") String sn, @Query("longitude") Double longitude, @Query("latitude") Double latitude);

    //    @HTTP(method = "DELETE", path = LOGOUT, hasBody = true)
//    Observable<ResponseBase> logout(@Header("phoneId") String phoneId, @Header("uid")
//            String uid, @Query("phoneId") String phoneId_q, @Query("uid") String uid_q, @Body RequestBody
// requestBody);
    @HTTP(method = "DELETE", path = LOGOUT, hasBody = true)
    Observable<ResponseBase> logout(@Query("phoneId") String phoneId_q, @Query("uid") String uid_q, @Body RequestBody
            requestBody);

    @GET(STATION_INFO + "{sn}")
    Observable<DeployStationInfoRsp> getStationDetail(@Path("sn") String sn);

    //
    @POST(STATION_DEPLOY + "{sn}")
    Observable<DeployStationInfoRsp> doStationDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET(GET_DEPOLY_RECORD_LIST)
    Observable<DeployRecordRsp> getDeployRecordList(@Query("sn") String sn, @Query("search") String searchText, @Query("beginTime") Long beginTime,
                                                    @Query("endTime") Long endTime, @Query("owners") String owners, @Query("signalQuality") String signalQuality, @Query("limit") Integer limit, @Query("offset") Integer offset, @Query("group") Boolean group);

    @PUT("alarmplay/{id}")
    Observable<DeviceAlarmItemRsp> doUpdatePhotosUrl(@Path("id") String id, @Body RequestBody requestBody);

    @GET("tools/qiniu/token")
    Observable<QiNiuToken> getQiNiuToken();

    @GET("contractsTemplate")
    Observable<ContractsTemplateRsp> getContractsTemplate();

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
    Observable<ContractAddRsp> newContract(@Body RequestBody requestBody);

    @PUT("contracts")
    Observable<ResponseBase> modifyContract(@Body RequestBody requestBody);

    @GET("contracts/{id}")
    Observable<ContractInfoRsp> getContractInfo(@Path("id") String id);

    @POST("contracts/_search")
    Observable<ContractsListRsp> searchContract(@Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("qrcode/scan")
    Observable<ResponseBase> getLoginScanResult(@Field("qrcodeId") String qrcodeId);

    @FormUrlEncoded
    @POST("qrcode/login")
    Observable<ResponseBase> scanLoginIn(@Field("qrcodeId") String qrcodeId);

    @FormUrlEncoded
    @POST("qrcode/cancel")
    Observable<ResponseBase> scanLoginCancel(@Field("qrcodeId") String qrcodeId);

    @FormUrlEncoded
    @POST(DOUBLE_CHECK)
    Observable<AuthRsp> doubleCheck(@Field("code") String code);

    @GET(ALARM_COUNT)
    Observable<AlarmCountRsp> getAlarmCount(@Query("beginTime") Long beginTime, @Query("endTime") Long endTime,
                                            @Query("displayStatus") String displayStatus, @Query("sn") String sn);

    @GET(MALFUNCTION_COUNT)
    Observable<MalfunctionCountRsp> getMalfunctionCount(@Query("beginTime") Long beginTime, @Query("endTime") Long endTime,
                                                        @Query("malfunctionStatus") String malfunctionStatus, @Query("sn") String sn);

    @GET(INSPECT_TASK_LIST)
    Observable<InspectionTaskModelRsp> getInspectTaskList(@Query("search") String search, @Query("finish") Integer finish, @Query
            ("offset") Integer offset, @Query("limit") Integer limit, @Query("startTime") Long startTime, @Query("finishTime") Long finishTime);

    @PUT("inspect/device")
    Observable<ResponseBase> uploadInspectionResult(@Body RequestBody responseBody);

    @GET("inspect/task/device")
    Observable<InspectionTaskDeviceDetailRsp> getInspectionDeviceList(@Query("taskId") String taskId, @Query("search") String search, @Query("sn") String sn, @Query("finish") Integer finish,
                                                                      @Query("deviceTypes") String deviceTypes, @Query("offset") Integer offset, @Query("limit") Integer limit);

    @GET(INSPECT_TASK_EXECUTION)
    Observable<InspectionTaskExecutionRsp> getInspectTaskExecution(@Query("taskId") String taskId);

    @PUT(INSPECT_TASK_CHANGE_STATE)
    Observable<ChangeInspectionTaskStateRsp> changeInspectionTaskState(@Body RequestBody requestBody);

    @POST(INSPECT_TASK_EXCEPTION_DETAIL)
    Observable<InspectionTaskExceptionDeviceRsp> getInspectionDeviceDetail(@Body RequestBody requestBody);

    @GET(INSPECTION_TASK_GET_TEMPLATE)
    Observable<InspectionTaskInstructionRsp> getInspectionTemplate(@Query("deviceType") String deviceType);

    @POST("devices/change/{sn}")
    Observable<DeviceDeployRsp> doInspectionChangeDeviceDeploy(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET(GET_DEVICES_MERGE_TYPES)
//    Observable<DevicesMergeTypesRsp> getDevicesMergeTypes(@Header("x-session-id") String sessionId);
    Observable<DevicesMergeTypesRsp> getDevicesMergeTypes();

    @PUT(MONITOR_POINT_OPERATION)
    Observable<MonitorPointOperationRequestRsp> doMonitorPointOperation(@Body RequestBody requestBody);

    @PUT("devices/gps/{sn}")
    Observable<DeviceDeployRsp> doDevicePositionCalibration(@Path("sn") String sn, @Body RequestBody requestBody);

    @GET("devices/valid")
//    Observable<DevicesMergeTypesRsp> getDevicesMergeTypes(@Header("x-session-id") String sessionId);
    Observable<ResponseBase> getDeviceNameValid(@Query("name") String name);

    @DELETE("prov1/accounts/controlling")
    Observable<LoginRsp> backMainControlling();

    /**
     * 获取固件列表
     *
     * @param sn
     * @param requestBody
     * @return
     */
    @POST("devices/version/list/{sn}")
    Observable<DeviceUpdateFirmwareDataRsp> getDeviceUpdateVision(@Path("sn") String sn, @Body RequestBody requestBody);

    /**
     * 回写固件版本到iot
     *
     * @param sn
     * @param requestBody
     * @return
     */
    @POST("devices/firmwareversion/update/{sn}")
    Observable<ResponseBase> upLoadDeviceUpdateVision(@Path("sn") String sn, @Body RequestBody requestBody);

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
    Observable<DevicesAlarmPopupConfigRsp> getDevicesAlarmPopupConfig();

    /**
     * 通过sn获取摄像头详情
     *
     * @param sn
     * @return
     */
    @GET("camera")
    Observable<DeviceCameraDetailRsp> getDeviceCamera(@Query("sn") String sn);

    /**
     * 获取用户下摄像头列表
     *
     * @param pageSize
     * @param page
     * @param search
     * @return
     */
    @GET("cameras")
    Observable<DeviceCameraListRsp> getDeviceCameraList(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search);


    /**
     * 获取用户下全量摄像头列表
     *
     * @return
     */
    @GET("cameras/map")
    Observable<ResponseBase> getDeviceCameraMapList();

    @GET("cameras/detail")
    Observable<AlarmCameraLiveRsp> getAlarmCamerasDetail(@Query("sn") String sn);

    @POST("picture/faceList")
    Observable<DeviceCameraFacePicListRsp> getDeviceCameraFaceList(@Body RequestBody requestBody);

    @POST("video/queryHistoryAddress")
    Observable<DeviceCameraHistoryRsp> getDeviceCameraPlayHistoryAddress(@Body RequestBody requestBody);

    @GET("cameras/group/bind")
    Observable<DeviceCameraListRsp> getDeviceGroupCameraList(@Query("_id") String _id, @Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search);

    @POST("picture/getFaceListById")
    Observable<DeviceCameraPersonFaceRsp> getDeviceCameraPersonFace(@Body RequestBody requestBody);

    /**
     * 100_camera - 100.026 获取安装方式和朝向选择字典
     *
     * @return
     */
    @GET("query-dict")
    Observable<CameraFilterRsp> getCameraFilter();

    @GET("group/stations/fields")
    Observable<CameraFilterRsp> getStationFilter();

    @GET("cameras")
    Observable<DeviceCameraListRsp> getDeviceCameraListByFilter(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search, @QueryMap Map<String, String> mapFilter);

    @GET("stations")
    Observable<BaseStationListRsp> getBaseStationListByFilter(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search, @QueryMap Map<String, String> mapFilter);


    @POST("qiniu/getCloudMediaByEventId")
    Observable<AlarmCloudVideoRsp> getCloudVideo(@Body RequestBody requestBody);

    @POST("camera/deploy")
    Observable<DeployCameraUploadRsp> doUploadDeployCamera(@Body RequestBody requestBody);

    @POST("camera/check")
    Observable<DeviceCameraDetailRsp> getDeployCameraInfo(@Body RequestBody requestBody);

    @GET("stations/{stationsn}")
    Observable<BaseStationDetailRsp> getBaseStationDetail(@Path("stationsn") String stationsn);

    @GET("stations/{stationsn}/sensor/{type}")
    Observable<BaseStationChartDetailRsp> getBaseStationChartDetail(@Path("stationsn") String stationsn, @Path("type") String type, @Query("interval") String interval, @Query("from") long from, @Query("to") long to);


    @PUT("stations/{stationsn}")
    Observable<BaseStationDetailRsp> updateStationLocation(@Path("stationsn") String stationsn, @Body RequestBody body);


    @GET("nameplates")
    Observable<NamePlateListRsp> getNameplateList(@Query("pageSize") Integer pageSize, @Query("page") Integer page, @Query("search") String search, @Query("deviceFlag") String deviceFlag);


    @DELETE("nameplate/{nameplateId}")
    Observable<DeleteNamePlateRsp> deleteNameplate(@Path("nameplateId") String nameplateId);

    @GET("nameplate/{nameplateId}")
    Observable<ResponseResult<NamePlateInfo>> getNameplateDetail(@Path("nameplateId") String nameplateId, @Query("isAuthUser") Boolean isAuthUser);

    @GET("nameplate/bind/devices")
    Observable<NameplateBindDeviceRsp> getNameplateBindDevices(@Query("page") Integer page, @Query("count") Integer count, @Query("nameplateId") String nameplateId);

    @PUT("nameplate/unbind/device")
    Observable<ResponseResult<Integer>> unbindNameplateDevice(@Body RequestBody requestBody);

    @PUT("nameplate/{nameplateId}")
    Observable<ResponseResult<Integer>> updateNameplate(@Path("nameplateId") String nameplateId, @Body RequestBody body);

    @GET("nameplate/unbind/devices")
    Observable<NameplateBindDeviceRsp> getNameplateUnbindDevices(@Query("page") Integer page, @Query("count") Integer count, @Query("nameplateId") String nameplateId, @Query("search") String searchText);

    @PUT("nameplate/deploy/{nameplateId}")
    Observable<DeployNameplateRsp> doUploadDeployNameplate(@Path("nameplateId") String nameplateId, @Body RequestBody requestBody);

    @PUT("nameplate/bind/device")
    Observable<ResponseResult<Integer>> doBindDevice(@Body RequestBody requestBody);
}

