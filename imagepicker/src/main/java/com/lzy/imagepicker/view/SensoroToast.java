package com.lzy.imagepicker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.R;


/**
 * Created by sensoro on 17/12/6.
 */

public class SensoroToast {

    private Toast mToast;

    private SensoroToast(Context context, CharSequence text, int duration) {
        final View v = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView textView = (TextView) v.findViewById(R.id.textView1);
        textView.setText(text);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);
    }

    public static SensoroToast makeText(Context context, CharSequence text, int duration) {
        return new SensoroToast(context, text, duration);
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
