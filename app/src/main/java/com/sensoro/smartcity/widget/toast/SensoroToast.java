package com.sensoro.smartcity.widget.toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;

/**
 * Created by sensoro on 17/12/6.
 */

public class SensoroToast {

    public static SensoroToast getInstance() {
        return SensoroToastHolder.instance;
    }

    private static class SensoroToastHolder {
        private static final SensoroToast instance = new SensoroToast();
    }

    private SensoroToast() {
    }

    private volatile Toast mToast;
    private volatile TextView textView;

    private void showToast(Context context, CharSequence content, int duration) {
        context = context.getApplicationContext();
        if (mToast == null) {
            mToast = new Toast(context.getApplicationContext());
            final View v = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
            textView = (TextView) v.findViewById(R.id.textView1);
            mToast.setView(v);//设置自定义的view
        }
        mToast.setDuration(duration);
        textView.setText(content);//设置文本
    }


    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
            if (textView != null) {
                textView.destroyDrawingCache();
                textView = null;
            }
        }
    }

    public SensoroToast makeText(Context context, CharSequence text, int duration) {
        context = context.getApplicationContext();
        showToast(context, text, duration);
        return this;
    }

    public SensoroToast makeText(CharSequence text, int duration) {
        showToast(SensoroCityApplication.getInstance().getApplicationContext(), text, duration);
        return this;
    }

    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }

    public SensoroToast setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
        return this;
    }
}
