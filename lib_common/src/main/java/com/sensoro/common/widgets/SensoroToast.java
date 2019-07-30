package com.sensoro.common.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sensoro.common.R;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.utils.Repause;


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

    private void showToast(@Nullable Context context, @NonNull CharSequence content, int duration) {
        mToast = new Toast(context);
        final View v = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        textView = (TextView) v.findViewById(R.id.textView1);
        mToast.setView(v);//设置自定义的view
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

    public SensoroToast makeText(@NonNull Context context, @NonNull CharSequence text, int duration) {
        showToast(context, text, duration);
        return this;
    }

    public SensoroToast makeText(@NonNull CharSequence text, int duration) {
        //处理application的只在前台时提示
        if (Repause.isApplicationResumed()) {
            showToast(ContextUtils.getContext(), text, duration);
        }
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
