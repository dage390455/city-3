package com.sensoro.forestfire.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.TextureMapView;

/**
 * @Author: jack
 * 时  间: 2019-09-19
 * 包  名: com.sensoro.forestfire.widgets
 * 简  述: <功能简述>
 */
public class GestureMapView extends TextureMapView {


    public GestureMapView(Context context) {
        super(context);
    }

    public GestureMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GestureMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public GestureMapView(Context context, AMapOptions aMapOptions) {
        super(context, aMapOptions);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
    private int downX;
    private int downY;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

//        if(ev.getAction()==MotionEvent.ACTION_DOWN){
//        }
        return super.onInterceptTouchEvent(ev);
    }
}
