package com.sensoro.smartcity.callback;

public interface OnConfigInfoObserver<T> {
    void onStart(String msg);

    void onSuccess(T t);

    void onFailed(String errorMsg);

    void onOverTime(String overTimeMsg);
}
