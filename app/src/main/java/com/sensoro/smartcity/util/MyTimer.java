package com.sensoro.smartcity.util;

import android.os.Handler;
import android.os.Looper;

public class MyTimer {
    private final int interval;
    private final int duration;
    private final OnMyTimer listener;
    private Handler handler;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            listener.onNext();
            handler.postDelayed(task, interval);
        }
    };
    private Runnable timeOverTime = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(task);
            handler.removeCallbacks(timeOverTime);
            listener.onFinish();
        }
    };

    public MyTimer(int interval, int duration, OnMyTimer onMyTimer) {
        this.interval = interval;
        this.duration = duration;
        this.listener = onMyTimer;
        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        handler.removeCallbacks(task);
        handler.removeCallbacks(timeOverTime);
        handler.postDelayed(task, interval);
        handler.postDelayed(timeOverTime, duration);
    }

    public void cancel() {
        handler.removeCallbacks(task);
        handler.removeCallbacks(timeOverTime);
        listener.onCancel();
    }

    public interface OnMyTimer {
        void onNext();

        void onFinish();

        void onCancel();
    }
}
