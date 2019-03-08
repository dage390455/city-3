package com.sensoro.smartcity.util;

import android.os.Handler;
import android.os.Looper;

public class MyTimer {
    private final int p;
    private final int ll;
    private final OnMyTimer listener;
    private Handler handler;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            listener.onNext();
            handler.postDelayed(this,p);
        }
    };

    public MyTimer(int p, int ll, OnMyTimer onMyTimer) {
        this.p = p;
        this.ll = ll;
        this.listener = onMyTimer;
        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        handler.postDelayed(task, p);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(task);
                handler.removeCallbacks(this);
                listener.onFinish();
            }
        }, ll);

    }

    public void cancle() {
        handler.removeCallbacksAndMessages(null);
        listener.onCancel();
    }

    public interface OnMyTimer {
        void onNext();

        void onFinish();

        void onCancel();
    }
}
