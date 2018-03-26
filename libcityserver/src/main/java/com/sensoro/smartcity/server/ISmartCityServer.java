package com.sensoro.smartcity.server;

import com.android.volley.Response;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmTimeRsp;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.DeviceHistoryListRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.server.response.DeviceTypeCountRsp;
import com.sensoro.smartcity.server.response.LoginRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UpdateRsp;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;

/**
 * Created by tangrisheng on 2016/5/5.
 * used for lora setting app server interface
 */
public interface ISmartCityServer {


    void setSessionId(String sessionId);

    void stopAllRequest();

    boolean login(String phone, String pwd,  String phoneId, final Response.Listener<LoginRsp> listener, Response.ErrorListener errorListener) ;

    void logout(String phoneId, String uid, final Response.Listener<ResponseBase> listener,  Response.ErrorListener errorListener);

    void getDeviceHistoryList(String sn, int count, final Response.Listener<DeviceHistoryListRsp> listener, Response.ErrorListener errorListener);

    void getDeviceHistoryList(String sn, long startTime, long endTime, final Response.Listener<DeviceRecentRsp> listener, Response.ErrorListener errorListener);

    void getDeviceDetailInfoList(String sns, String search, int all, final Response.Listener<DeviceInfoListRsp> listener, Response.ErrorListener errorListener);

    void getDeviceBriefInfoList(int page, String sensorTypes, Integer status, String search, final Response.Listener<DeviceInfoListRsp> listener, Response.ErrorListener errorListener);

    void getDeviceTypeCount(final Response.Listener<DeviceTypeCountRsp> listener, Response.ErrorListener errorListener);

    void getDeviceAlarmTime(String sn, final Response.Listener<DeviceAlarmTimeRsp> listener, Response.ErrorListener errorListener);

    void getDeviceAlarmLogList(Long beginTime, Long endTime, String sn, String unionTypes, int page, final Response.Listener<DeviceAlarmLogRsp> listener, Response.ErrorListener errorListener);

    void getUserAccountList(String search, String order, String sort, String offset, String limit, final Response.Listener<UserAccountRsp> listener, Response.ErrorListener errorListener);

    void getUpdateInfo(final Response.Listener<UpdateRsp> listener, Response.ErrorListener errorListener);

    void doAlarmConfirm(String id, int status, String remark, final Response.Listener<DeviceAlarmItemRsp> listener, Response.ErrorListener errorListener);

    void doAccountControl(String uid, String phoneId, final Response.Listener<UserAccountControlRsp> listener, Response.ErrorListener errorListener);

    void doDevicePointDeploy(String sn, double lon, double lat, String tags, String name, String contact, String content, final Response.Listener<DeviceDeployRsp> listener, Response.ErrorListener errorListener);

}
