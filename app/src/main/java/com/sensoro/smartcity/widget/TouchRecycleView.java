package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TouchRecycleView extends RecyclerView {
    private boolean isIntercept;
    private boolean isMove = false;

    public TouchRecycleView(Context context) {
        super(context);
    }

    public TouchRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isIntercept) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }


    public void setIntercept(boolean isIntercept) {
        this.isIntercept = isIntercept;
    }
}
