package com.sensoro.smartcity.server;


import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;
import rx.Observer;

import static com.sensoro.smartcity.constant.Constants.EVENT_DATA_SESSION_ID_OVERTIME;

public abstract class CityObserver<T> implements Observer<T> {
    public static int ERR_CODE_NET_CONNECT_EX = -0x01;
    public static int ERR_CODE_UNKNOWN_EX = -0x02;
    private final WeakReference<BasePresenter> presenterWeakReference;
    private boolean needPresenter = false;

    public CityObserver(BasePresenter basePresenter) {
        presenterWeakReference = new WeakReference<>(basePresenter);
    }

    public CityObserver() {
        presenterWeakReference = null;
        needPresenter = false;
    }


    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        LogUtils.loge(this, "onError e : " + message);
        //在这里做全局的错误处理
        if (e instanceof UnknownHostException || e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof TimeoutException) {
            //网络错误
            onErrorMsg(ERR_CODE_NET_CONNECT_EX, "似乎已断开与互联网的连接。");
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.response().code();
            //sessionID过期统一处理
            String responseMsg = httpException.response().toString();
            try {
                String errorBody = httpException.response().errorBody().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(errorBody);
                    String log = jsonObject.toString();
                    LogUtils.loge(this, "onError = " + log);
                    int errcode = jsonObject.getInt("errcode");
                    if (errcode == 4000002) {
                        EventData eventData = new EventData();
                        eventData.code = EVENT_DATA_SESSION_ID_OVERTIME;
                        EventBus.getDefault().post(eventData);
                    }
                    String errinfo = jsonObject.getString("errinfo");
                    onErrorMsg(errcode, errinfo);
                } catch (JSONException e1) {
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

    @Override
    public void onCompleted() {

    }

    @Override
    public void onNext(T t) {
        if (needPresenter) {
            if (presenterWeakReference != null) {
                if (presenterWeakReference.get() != null && presenterWeakReference.get().isAttachedView()) {
                    onCompleted(t);
                } else {
                    LogUtils.loge(this, "------->>界面在未完成任务前销毁！！！");
                }
            }
        } else {
            onCompleted(t);
        }

    }

    public abstract void onCompleted(T t);

    public abstract void onErrorMsg(int errorCode, String errorMsg);
}
