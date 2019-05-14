package com.sensoro.common.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

public class HandlerDeployCheck extends Handler {

    private volatile int count;
    private int mInterval;
    private int mMaxCount;
    private final SparseArray<OnMessageDeal> listenerMap = new SparseArray<>();

    public HandlerDeployCheck(Looper mainLooper) {
        super(mainLooper);
    }

    @Override
    public void handleMessage(Message msg) {
        if (count < mMaxCount) {
            Message m = Message.obtain();
            m.what = msg.what;
            count++;
            sendMessageDelayed(m, mInterval);
            OnMessageDeal onMessageDeal = listenerMap.get(msg.what);
            if (onMessageDeal != null) {
                onMessageDeal.onNext();
            }
        } else {
            OnMessageDeal onMessageDeal = listenerMap.get(msg.what);
            if (onMessageDeal != null) {
                onMessageDeal.onFinish();
            }
            removeMessages(msg.what);
            listenerMap.remove(msg.what);
        }
    }

    public void init(int interval, int maxCount) {
        count = 0;
        mInterval = interval;
        mMaxCount = maxCount;
    }

    public void dealMessage(int what, OnMessageDeal onMessageDeal) {
        Message message = Message.obtain();
        message.what = what;
        count++;
        sendMessageDelayed(message, mInterval);
        listenerMap.put(what, onMessageDeal);
    }

    public void removeMessage(int what) {
        removeMessages(what);
        listenerMap.remove(what);
    }

    public void removeAllMessage() {
        removeCallbacksAndMessages(null);
        listenerMap.clear();
    }

    public interface OnMessageDeal {
        void onNext();

        void onFinish();

//        void onCancel();
    }
}
