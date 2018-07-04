package com.sensoro.smartcity.server;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;
import rx.Observer;

public abstract class CityObserver<T> implements Observer<T> {
    public static int ERR_CODE_NET_CONNECT_EX = -0x01;
    public static int ERR_CODE_UNKNOWN_EX = -0x02;

    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        LogUtils.loge(this, "onError e : " + message);
        //在这里做全局的错误处理
        if (e instanceof UnknownHostException || e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof TimeoutException) {
            //网络错误
            onErrorMsg(ERR_CODE_NET_CONNECT_EX, "网络连接错误,请检查网络连接");
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.response().code();
            String responseMsg = httpException.response().toString();
            try {
                String errorBody = httpException.response().errorBody().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(errorBody);
                    String log = jsonObject.toString();
                    LogUtils.loge(this, "onError = " + log);
                    int errcode = jsonObject.getInt("errcode");
                    String errinfo = jsonObject.getString("errinfo");
                    onErrorMsg(errcode, errinfo);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    try {
                        jsonObject = new JSONObject(errorBody);
                        String log = jsonObject.toString();
                        int errcode = jsonObject.getInt("errcode");
                        String errmsg = jsonObject.getString("errmsg");
                        LogUtils.loge(this, "onError = " + log + ",errcode = " + errcode);
                        onErrorMsg(code, errmsg);
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        onErrorMsg(code, "服务器json数据不标准：" + errorBody);
                    }
                }
            } catch (IOException er) {
                e.printStackTrace();
                onErrorMsg(code, "数据格式不准确： " + responseMsg);
            }
        } else {
            onErrorMsg(ERR_CODE_UNKNOWN_EX, "未知网络错误: " + message);
        }


    }

    public abstract void onErrorMsg(int errorCode, String errorMsg);
}
