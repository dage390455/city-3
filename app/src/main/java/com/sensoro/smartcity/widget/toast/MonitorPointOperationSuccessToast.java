package com.sensoro.smartcity.widget.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;

import javax.xml.datatype.Duration;

/**
 * Created by sensoro on 17/12/6.
 */

public enum MonitorPointOperationSuccessToast {
    INSTANCE;
    private Toast mToast;

    public void showToast(Context context, int duration) {
        if (mToast == null) {
            mToast = new Toast(context);
            final View v = LayoutInflater.from(context).inflate(R.layout.item_toast_monitor_point_operation_success, null);
            mToast.setView(v);//设置自定义的view
        }
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(duration);
        mToast.show();
    }


    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

}
