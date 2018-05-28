package com.sensoro.smartcity.iwidget;

public interface IToast {
    void toastShort(String msg);

    void toastShort(int resId);

    void toastLong(String msg);

    void toastLong(int resId);
}
