package com.sensoro.smartcity.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by sensoro on 17/11/14.
 */

public class SensoroShadowView extends ImageView {


    public SensoroShadowView(Context context) {
        super(context);
    }

    public SensoroShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SensoroShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return true;
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
