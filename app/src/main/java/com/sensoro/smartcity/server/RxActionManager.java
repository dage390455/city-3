package com.sensoro.smartcity.server;

import io.reactivex.disposables.Disposable;

public interface RxActionManager<T> {

    void add(T tag, Disposable disposable);

    void remove(T tag);

    void cancel(T tag);

    void cancelAll();
}
