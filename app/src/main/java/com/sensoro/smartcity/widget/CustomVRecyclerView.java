package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by yingzi on 2017/6/27.
 */

public class CustomVRecyclerView extends RecyclerView {
    private float lastX;
    private float lastY;

    public CustomVRecyclerView(Context context) {
        super(context);
    }

    public CustomVRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float x = ev.getRawX();
        float y = ev.getRawY();
        float dealtX = 0;
        float dealtY = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                dealtX = 0;
                dealtY = 0;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                dealtX += Math.abs(x - lastX);
                dealtY += Math.abs(y - lastY);
                if (dealtX >= dealtY) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                lastX = x;
                lastY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
//        ViewParent parent =this;
//        while(!((parent = parent.getParent()) instanceof SmartRefreshLayout));// 循环查找viewPager
//        parent.requestDisallowInterceptTouchEvent(true);
        /*---解决ViewPager嵌套垂直RecyclerView嵌套水平RecyclerView横向滑动到底后不滑动ViewPager start ---*/
        return super.dispatchTouchEvent(ev);
    }


}
