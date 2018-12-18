package com.sensoro.smartcity.server;


import android.widget.Toast;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

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
    public static int ERR_CODE_NET_CONNECT_EX = -0x1001;
    public static int ERR_CODE_UNKNOWN_EX = -0x1002;
    private final WeakReference<BasePresenter> presenterWeakReference;
    private boolean needPresenter;

    /**
     * 如果不需要绑定Activity的生命周期，传入null即可
     *
     * @param basePresenter
     */
    public CityObserver(BasePresenter basePresenter) {
        if (basePresenter == null) {
            presenterWeakReference = null;
            needPresenter = false;
        } else {
            presenterWeakReference = new WeakReference<>(basePresenter);
            needPresenter = true;
        }
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
            if (AppUtils.isChineseLanguage()) {
                onErrorMsg(ERR_CODE_NET_CONNECT_EX, "似乎已断开与互联网的连接。");
            } else {
                onErrorMsg(ERR_CODE_NET_CONNECT_EX, "It seems to have disconnected from the internet.");
            }

        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.response().code();
            //sessionID过期统一处理
            String responseMsg = httpException.response().toString();
            try {
                String errorBody = httpException.response().errorBody().string();
                JSONObject jsonObject = null;
                if (AppUtils.isChineseLanguage()) {
                    try {
                        jsonObject = new JSONObject(errorBody);
                        String log = jsonObject.toString();
                        LogUtils.loge(this, "onError = " + log);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 4000002) {
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_SESSION_ID_OVERTIME;
                            EventBus.getDefault().post(eventData);
                            String errinfo = jsonObject.getString("errinfo");
                            SensoroToast.INSTANCE.makeText(errinfo, Toast.LENGTH_SHORT).show();
                            return;
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
                            if (AppUtils.isChineseLanguage()) {
                                onErrorMsg(code, "服务器json数据不标准：" + errorBody);
                            } else {
                                onErrorMsg(code, "Server json data is not standard:" + errorBody);
                            }

                        }
                    }
                } else {
                    try {
                        jsonObject = new JSONObject(errorBody);
                        String log = jsonObject.toString();
                        LogUtils.loge(this, "onError = " + log);
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 4000002) {
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_SESSION_ID_OVERTIME;
                            EventBus.getDefault().post(eventData);
                            String errmsg = jsonObject.getString("errmsg");
                            SensoroToast.INSTANCE.makeText(errmsg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String errmsg = jsonObject.getString("errmsg");
                        onErrorMsg(errcode, errmsg);
                    } catch (JSONException e1) {
                        try {
                            jsonObject = new JSONObject(errorBody);
                            String log = jsonObject.toString();
                            int errcode = jsonObject.getInt("errcode");
                            String errinfo = jsonObject.getString("errinfo");
                            LogUtils.loge(this, "onError = " + log + ",errcode = " + errcode);
                            onErrorMsg(code, errinfo);
                        } catch (JSONException e2) {
                            if (AppUtils.isChineseLanguage()) {
                                onErrorMsg(code, "服务器json数据不标准：" + errorBody);
                            } else {
                                onErrorMsg(code, "Server json data is not standard:" + errorBody);
                            }
                        }
                    }
                }

            } catch (IOException er) {
                if (AppUtils.isChineseLanguage()) {
                    onErrorMsg(code, "数据格式不准确： " + responseMsg);
                } else {
                    onErrorMsg(code, "Inaccurate data format： " + responseMsg);
                }

            }
        } else {
            if (AppUtils.isChineseLanguage()) {
                onErrorMsg(ERR_CODE_UNKNOWN_EX, "未知网络错误: " + message);
            } else {
                onErrorMsg(ERR_CODE_UNKNOWN_EX, "Unknown network error: " + message);
            }

        }


    }

    @Override
    public void onCompleted() {
        if (presenterWeakReference != null) {
            presenterWeakReference.clear();
        }
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
