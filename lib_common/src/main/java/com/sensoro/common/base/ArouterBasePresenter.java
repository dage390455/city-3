package com.sensoro.common.base;

import android.content.Context;
import android.content.Intent;

public abstract class ArouterBasePresenter<V> extends BasePresenter<V> {
    @Override
    public void initData(Context context) {
        this.initData(context, null);
    }

    @Override
    public void onDestroy() {

    }

    public abstract void initData(Context context, Intent intent);
}
