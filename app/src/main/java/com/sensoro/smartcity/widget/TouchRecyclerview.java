package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TouchRecyclerview extends RecyclerView {
    private boolean isIntercept;
    private boolean isMove = false;

    public TouchRecyclerview(Context context) {
        super(context);
    }

    public TouchRecyclerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchRecyclerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    public void setIntercept(boolean isIntercept){
        this.isIntercept = isIntercept;
    }
}
