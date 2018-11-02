package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SensoroNestedScrollview extends NestedScrollView {
    public SensoroNestedScrollview(@NonNull Context context) {
        super(context);
    }

    public SensoroNestedScrollview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SensoroNestedScrollview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return false;
            default:
                return true;
        }
    }
}
