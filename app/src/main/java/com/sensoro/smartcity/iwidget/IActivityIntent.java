package com.sensoro.smartcity.iwidget;

import android.content.Intent;

public interface IActivityIntent {
    void startAC(Intent intent);
    void finishAc();
    void startACForResult(Intent intent,int requestCode);
}
