package com.sensoro.smartcity.server.response;

import android.util.Log;

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
    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        Log.e("CityObserver", "onError: " + message);
        //在这里做全局的错误处理
        if (e instanceof UnknownHostException || e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof TimeoutException) {
            //网络错误
            onErrorMsg("网络连接错误,请检查网络连接");
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            try {
                String errorBody = httpException.response().errorBody().string();
                //TODO: parse To JSON Obj
                try {
                    JSONObject jsonObject = new JSONObject(errorBody);
                    onErrorMsg(jsonObject.getString("errmsg"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    byte[] bytes = httpException.response().errorBody().bytes();
                    onErrorMsg("服务器json数据不标准：" + new String(bytes));
                }
            } catch (IOException er) {
                e.printStackTrace();
                onErrorMsg("数据格式不准确： " + er.getMessage());
            }
        } else {
            onErrorMsg("未知网络错误: " + message);
        }


    }

    public abstract void onErrorMsg(String errorMsg);
}
