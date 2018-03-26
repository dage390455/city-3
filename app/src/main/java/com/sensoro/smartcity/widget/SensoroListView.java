package com.sensoro.smartcity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by sensoro on 17/10/31.
 */

public class SensoroListView extends ListView {

    private ScrollView mParentScrollView;
    public SensoroListView(Context context) {
        super(context);
    }

    public SensoroListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SensoroListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParentScrollView(ScrollView scrollView) {
        this.mParentScrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setParentScrollAble(false);//当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview 停住不能滚动
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setParentScrollAble(true);//当手指松开时，让父ScrollView重新拿到onTouch权限
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否把滚动事件交给父scrollview
     *
     * @param flag
     */
    public void setParentScrollAble(boolean flag) {
        if (mParentScrollView != null) {
            mParentScrollView.requestDisallowInterceptTouchEvent(!flag);//这里的parentScrollView就是listview外面的那个scrollview
        }
    }
}
