package com.sensoro.smartcity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class CustomVRelativeLayout extends RelativeLayout {
    private float lastX;
    private float lastY;

    public CustomVRelativeLayout(Context context) {
        super(context);
    }

    public CustomVRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                lastX = x;
                lastY = y;
                if (dealtX >= dealtY) {
                    //TODO 屏蔽水平滑动
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

//                break;
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
