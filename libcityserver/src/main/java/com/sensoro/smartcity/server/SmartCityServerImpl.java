package com.sensoro.smartcity.server;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
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
import com.sensoro.volleymanager.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SmartCityServerImpl implements ISmartCityServer {
    public static final String SCOPE_MOCHA = "https://demo-city-api.sensoro.com";
    public static final String SCOPE_MASTER = "https://city-api.sensoro.com";
    public static String SCOPE = SCOPE_MASTER;//http://mocha-iot-api.mocha.server.sensoro.com-----http://iot-api.sensoro.com
    public static final String LOGIN = "/sessions";
    public static final String LOGOUT = "/sessions/current";
    public static final String USER_ACCOUNT_LIST = "/users";
    public static final String DEVICE_INFO_LIST = "/prov1/devices/details/app";
    public static final String DEVICE_HISTORY_LIST = "/prov1/logs/list/ltime/app";
    public static final String DEVICE_ALARM_TIME = "/details/alarm/ltime";
    public static final String DEVICE_ALARM_HISTORY = "/prov1/alarms/list/app";
    public static final String DEVICE_ALARM_LOG = "/alarmplay";
    public static final String DEVICE_BRIEF_LIST = "/stats/device/brief";
    public static final String DEVICE_TYPE_COUNT = "/prov1/devices/status/count";
    public static final String APP_UPDATE = "http://api.fir.im/apps/latest/599519bbca87a829360005f8?api_token=72af8ff1c6587c51e8e9a28209f71713";

    public static final String HEADER_SESSION_ID = "x-session-id";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String TAG = "Lora";

    Context context;

    private String sessionId = null;
    private static SmartCityServerImpl singleton;
    Gson gson;

    VolleyManager volleyManager;

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    private SmartCityServerImpl(Context context) {
        this.context = context;
        gson = new Gson();
        volleyManager = VolleyManager.getInstance(context);
    }

    public static SmartCityServerImpl getInstance(Context context) {
        if (context == null) {
            return null;
        }
        if (singleton == null) {
            synchronized (SmartCityServerImpl.class) {
                if (singleton == null) {
                    singleton = new SmartCityServerImpl(context);
                }
            }
        }

        return singleton;
    }

    @Override
    public void stopAllRequest() {
        if (volleyManager != null) {
            volleyManager.cancel(TAG);
        }
    }

    @Override
    public boolean login(String phone, String pwd, String phoneId, final Response.Listener<LoginRsp> listener, Response.ErrorListener errorListener) {

        if (phone == null || pwd == null) {
            return false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("password", pwd);
            jsonObject.put("phoneId", phoneId);
            jsonObject.put("phoneType", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Response.Listener<LoginRsp> interceptListener = new Response.Listener<LoginRsp>() {
            @Override
            public void onResponse(LoginRsp response) {
                // set sessionid;
                sessionId = response.getData().getSessionID();
                System.out.println("sessionId1===>" + sessionId);
                listener.onResponse(response);
            }
        };
        volleyManager.gsonRequest(TAG, Request.Method.POST, jsonObject.toString(), SCOPE + LOGIN, LoginRsp.class, interceptListener, errorListener);

        return true;
    }


    @Override
    public void getDeviceHistoryList(String sn, int count, Response.Listener<DeviceHistoryListRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

        String url = SCOPE + DEVICE_HISTORY_LIST + "?sn=" + sn + "&count=" + count ;
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, DeviceHistoryListRsp.class, listener, errorListener);
    }

    @Override
    public void getDeviceHistoryList(String sn, long startTime, long endTime, Response.Listener<DeviceRecentRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

        String url = SCOPE + "/details/"+ sn+ "/statistics/es?beginTime=" + startTime + "&endTime=" + endTime + "&type=hours";
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, DeviceRecentRsp.class, listener, errorListener);
    }

    @Override
    public void getDeviceDetailInfoList(String sns, String search, int all, Response.Listener<DeviceInfoListRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

        if (search == null) {
            search = "";
        }

        String url = SCOPE + DEVICE_INFO_LIST + "?sns=" + sns + "&search=" + search + "&all=" + all;
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, DeviceInfoListRsp.class, listener, errorListener);

    }

    @Override
    public void getDeviceBriefInfoList(int page, String sensorTypes, Integer status, String search, Response.Listener<DeviceInfoListRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }
        String url = SCOPE + DEVICE_BRIEF_LIST + "?page=" + page + "&count=20&all=1";
        if (sensorTypes != null) {
            url +=  "&sensorTypes=" + sensorTypes;
        }
        if (status != null) {
            url +=  "&status=" + status;
        }
        if (search != null){
            url +=  "&search=" + search;
        }
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, DeviceInfoListRsp.class, listener, errorListener);

    }

    @Override
    public void getDeviceTypeCount(Response.Listener<DeviceTypeCountRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }
        String url = SCOPE + DEVICE_TYPE_COUNT;
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, DeviceTypeCountRsp.class, listener, errorListener);
    }

    @Override
    public void getDeviceAlarmTime(String sn, Response.Listener<DeviceAlarmTimeRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

        String url = SCOPE + DEVICE_ALARM_TIME + "?sn=" + sn;
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, DeviceAlarmTimeRsp.class, listener, errorListener);

    }

    @Override
    public void getDeviceAlarmLogList(Long beginTime, Long endTime, String sn, String unionTypes, int page, final Response.Listener<DeviceAlarmLogRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }


        StringBuffer url = new StringBuffer();
        url.append(SCOPE + DEVICE_ALARM_LOG + "?count=10&page=" + page + "&");
        if (sn != null) {
            url.append("sn=" + sn + "&");
        }
        if (beginTime != null || endTime != null){
            url.append("beginTime=" + beginTime + "&endTime=" + endTime + "&");
        }
        if (unionTypes != null){
            url.append("unionTypes=" +unionTypes);
        }
        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url.toString(), DeviceAlarmLogRsp.class, listener, errorListener);

    }

    @Override
    public void getUserAccountList(String search, String order, String sort, String offset, String limit, Response.Listener<UserAccountRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

        String url = SCOPE + USER_ACCOUNT_LIST ;
        if (search == null) {
            url += "?limit=" + limit;
        } else {
            url += "?search=" + search + "&limit=" + limit;
        }

        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, UserAccountRsp.class, listener, errorListener);

    }

    @Override
    public void getUpdateInfo( Response.Listener<UpdateRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        String url = APP_UPDATE;

        volleyManager.gsonRequest(TAG, Request.Method.GET, headers, (String) null,  url, UpdateRsp.class, listener, errorListener);

    }

    @Override
    public void doAlarmConfirm(String id, int status, String remark, Response.Listener<DeviceAlarmItemRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("displayStatus", status);
            jsonObject.put("remark", remark);
            jsonObject.put("source", "app");
            jsonObject.put("type", "confirm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SCOPE + "/alarmplay/" + id ;
        volleyManager.gsonRequest(TAG, Request.Method.PUT, headers, jsonObject.toString(), url, DeviceAlarmItemRsp.class, listener, errorListener);

    }

    @Override
    public void doAccountControl(String uid, String phoneId, final Response.Listener<UserAccountControlRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

//        Response.Listener<UserAccountControlRsp> interceptListener = new Response.Listener<UserAccountControlRsp>() {
//            @Override
//            public void onResponse(UserAccountControlRsp response) {
//                // set sessionid;
//                sessionId = response.getData().getSessionID();
//                System.out.println("sessionId2===>" + sessionId);
//                listener.onResponse(response);
//            }
//        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneId", phoneId);
            jsonObject.put("phoneType", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SCOPE + "/users/" + uid + "/controlling";
        volleyManager.gsonRequest(TAG, Request.Method.POST, headers, jsonObject.toString(), url, UserAccountControlRsp.class, listener, errorListener);

    }

    @Override
    public void doDevicePointDeploy(String sn, double lon, double lat, String tags, String name, String contact, String content, Response.Listener<DeviceDeployRsp> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            if (tags != null) {
                jsonObject.put("tags", tags);
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
        String url = SCOPE + "/devices/app/" + sn;
        volleyManager.gsonRequest(TAG, Request.Method.POST, headers, jsonObject.toString(), url, DeviceDeployRsp.class, listener, errorListener);

    }


    @Override
    public void logout(String phoneId, String uid, final Response.Listener<ResponseBase> listener, Response.ErrorListener errorListener) {
        Map<String, String> headers = new HashMap<>();

        if (sessionId != null) {
            // add sessionId for authorization.
            headers.put(HEADER_SESSION_ID, sessionId);
            headers.put("phoneId", phoneId);
            headers.put("uid", uid);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phoneId", phoneId);
            jsonObject.put("uid", uid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SCOPE + LOGOUT + "?phoneId=" + phoneId + "&uid=" + uid;
        volleyManager.gsonRequest(TAG, Request.Method.DELETE, headers, jsonObject.toString(), url, ResponseBase.class, listener, errorListener);

    }
}
