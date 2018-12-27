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

public enum SensoroSuccessToast {
    INSTANCE;
    private Toast mToast;
    private TextView textView;

    public void showToast(Context context, int duration) {
       showToast(context,duration,context.getResources().getString(R.string.request_success));
    }

    public void showToast(Context context, int duration,String msg) {
        if (mToast == null) {
            mToast = new Toast(context);
            final View v = LayoutInflater.from(context).inflate(R.layout.item_toast_monitor_point_operation_success, null);
            textView = (TextView) v.findViewById(R.id.toast_message);
            mToast.setView(v);//设置自定义的view
        }
        if (textView!=null) {
            textView.setText(msg);
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
