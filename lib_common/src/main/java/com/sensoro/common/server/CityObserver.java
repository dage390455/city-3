package com.sensoro.common.server;


import android.widget.Toast;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.manger.RxApiManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.SensoroToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

import static com.sensoro.common.constant.Constants.EVENT_DATA_SESSION_ID_OVERTIME;

public abstract class CityObserver<T> implements Observer<T> {
    public static int ERR_CODE_NET_CONNECT_EX = -0x1001;
    public static int ERR_CODE_UNKNOWN_EX = -0x1002;
    private final WeakReference<BasePresenter> presenterWeakReference;

    /**
     * 如果不需要绑定Activity的生命周期，传入null即可
     *
     * @param basePresenter
     */
    public CityObserver(BasePresenter basePresenter) {
        if (basePresenter == null) {
            presenterWeakReference = null;
        } else {
            presenterWeakReference = new WeakReference<>(basePresenter);
        }
    }

    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        try {
            LogUtils.loge(this, "onError e : " + message);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //在这里做全局的错误处理
        if (e instanceof UnknownHostException || e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof TimeoutException) {
            //网络错误
            if (AppUtils.isChineseLanguage()) {
                if (viewAttachedAlive()) {
                    onErrorMsg(ERR_CODE_NET_CONNECT_EX, "似乎已断开与互联网的连接。");
                }
            } else {
                if (viewAttachedAlive()) {
                    onErrorMsg(ERR_CODE_NET_CONNECT_EX, "It seems to have disconnected from the internet.");
                }
            }

        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.response().code();
            //sessionID过期统一处理
            String responseMsg = httpException.response().toString();
            try {
                String errorBody = httpException.response().errorBody().string();
                JSONObject jsonObject;
                if (AppUtils.isChineseLanguage()) {
                    try {
                        jsonObject = new JSONObject(errorBody);
                        String log = jsonObject.toString();
                        try {
                            LogUtils.loge(this, "onError = " + log);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 4000002) {
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_SESSION_ID_OVERTIME;
                            EventBus.getDefault().post(eventData);
                            String errinfo = jsonObject.getString("errinfo");
                            SensoroToast.getInstance().makeText(errinfo, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String errinfo = jsonObject.getString("errinfo");
                        if (viewAttachedAlive()) {
                            onErrorMsg(errcode, errinfo);
                        }
                    } catch (JSONException e1) {
                        try {
                            jsonObject = new JSONObject(errorBody);
                            String log = jsonObject.toString();
                            int errcode = jsonObject.getInt("errcode");
                            String errmsg = jsonObject.getString("errmsg");
                            try {
                                LogUtils.loge(this, "onError = " + log + ",errcode = " + errcode);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            if (viewAttachedAlive()) {
                                onErrorMsg(code, errmsg);
                            }
                        } catch (JSONException e2) {
                            if (AppUtils.isChineseLanguage()) {
                                if (viewAttachedAlive()) {
                                    onErrorMsg(code, "服务器json数据不标准：" + errorBody);
                                }
                            } else {
                                if (viewAttachedAlive()) {
                                    onErrorMsg(code, "Server json data is not standard:" + errorBody);
                                }
                            }

                        }
                    }
                } else {
                    try {
                        jsonObject = new JSONObject(errorBody);
                        String log = jsonObject.toString();
                        try {
                            LogUtils.loge(this, "onError = " + log);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        int errcode = jsonObject.getInt("errcode");
                        if (errcode == 4000002) {
                            EventData eventData = new EventData();
                            eventData.code = EVENT_DATA_SESSION_ID_OVERTIME;
                            EventBus.getDefault().post(eventData);
                            String errmsg = jsonObject.getString("errmsg");
                            SensoroToast.getInstance().makeText(errmsg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String errmsg = jsonObject.getString("errmsg");
                        if (viewAttachedAlive()) {
                            onErrorMsg(errcode, errmsg);
                        }
                    } catch (JSONException e1) {
                        try {
                            jsonObject = new JSONObject(errorBody);
                            String log = jsonObject.toString();
                            int errcode = jsonObject.getInt("errcode");
                            String errinfo = jsonObject.getString("errinfo");
                            try {
                                LogUtils.loge(this, "onError = " + log + ",errcode = " + errcode);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            if (viewAttachedAlive()) {
                                onErrorMsg(code, errinfo);
                            }
                        } catch (JSONException e2) {
                            if (AppUtils.isChineseLanguage()) {
                                if (viewAttachedAlive()) {
                                    onErrorMsg(code, "服务器json数据不标准：" + errorBody);
                                }
                            } else {
                                if (viewAttachedAlive()) {
                                    onErrorMsg(code, "Server json data is not standard:" + errorBody);
                                }
                            }
                        }
                    }
                }

            } catch (IOException er) {
                if (AppUtils.isChineseLanguage()) {
                    if (viewAttachedAlive()) {
                        onErrorMsg(code, "数据格式不准确： " + responseMsg);
                    }
                } else {
                    if (viewAttachedAlive()) {
                        onErrorMsg(code, "Inaccurate data format： " + responseMsg);
                    }
                }

            }
        } else {
            if (AppUtils.isChineseLanguage()) {
                if (viewAttachedAlive()) {
                    onErrorMsg(ERR_CODE_UNKNOWN_EX, "未知网络错误: " + message);
                }
            } else {
                if (viewAttachedAlive()) {
                    onErrorMsg(ERR_CODE_UNKNOWN_EX, "Unknown network error: " + message);
                }
            }

        }
        RxApiManager.getInstance().remove(this);
    }

    @Override
    public void onComplete() {
        if (presenterWeakReference != null) {
            presenterWeakReference.clear();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        RxApiManager.getInstance().add(this, d);
    }

    @Override
    public void onNext(T t) {
        if (viewAttachedAlive()) {
            onCompleted(t);
            RxApiManager.getInstance().remove(this);
        } else {
            RxApiManager.getInstance().cancel(this);
        }
    }

    /**
     * 判断是否还有view存在
     *
     * @return
     */
    private boolean viewAttachedAlive() {
        if (presenterWeakReference == null) {
            return true;
        } else {
            if (presenterWeakReference.get() != null && presenterWeakReference.get().isAttachedView()) {
                return true;
            } else {
                try {
                    LogUtils.loge(this, "------->>界面在未完成任务前销毁！！！");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        return false;
    }


    public abstract void onCompleted(T t);

    public abstract void onErrorMsg(int errorCode, String errorMsg);
}
