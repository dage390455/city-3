package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baidu.ocr.sdk.utils.LogUtil;

/**
 * Created by yingzi on 2017/6/27.
 */

public class CustomVRecyclerView extends RecyclerView {
    private float lastX;
    private float lastY;
    private boolean isTopToBottom;
    private boolean isBottomToTop;
    private int lastVisibleItemPosition;
    private int firstVisibleItemPosition;
    private float mLastY = 0;// 记录上次Y位置

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

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastY = event.getY();
//                //不允许父View拦截事件
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float nowY = event.getY();
//                isIntercept(nowY);
//                if (isBottomToTop || isTopToBottom) {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                    return false;
//                } else {
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                }
//                mLastY = nowY;
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//        return super.onTouchEvent(ev);
//    }

    private void isIntercept(float nowY) {

        isTopToBottom = false;
        isBottomToTop = false;

        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //得到当前界面，最后一个子视图对应的position
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            //得到当前界面，第一个子视图的position
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();
        }
        //得到当前界面可见数据的大小
        int visibleItemCount = layoutManager.getChildCount();
        //得到RecyclerView对应所有数据的大小
        int totalItemCount = layoutManager.getItemCount();
        LogUtil.d("nestScrolling", "onScrollStateChanged");
        if (visibleItemCount > 0) {
            if (lastVisibleItemPosition == totalItemCount - 1) {
                //最后视图对应的position等于总数-1时，说明上一次滑动结束时，触底了
                LogUtil.d("nestScrolling", "触底了");
                if (CustomVRecyclerView.this.canScrollVertically(-1) && nowY < mLastY) {
                    // 不能向上滑动
                    LogUtil.d("nestScrolling", "不能向上滑动");
                    isBottomToTop = true;
                } else {
                    LogUtil.d("nestScrolling", "向下滑动");
                }
            } else if (firstVisibleItemPosition == 0) {
                //第一个视图的position等于0，说明上一次滑动结束时，触顶了
                LogUtil.d("nestScrolling", "触顶了");
                if (CustomVRecyclerView.this.canScrollVertically(1) && nowY > mLastY) {
                    // 不能向下滑动
                    LogUtil.d("nestScrolling", "不能向下滑动");
                    isTopToBottom = true;
                } else {
                    LogUtil.d("nestScrolling", "向上滑动");
                }
            }
        }
    }


}
