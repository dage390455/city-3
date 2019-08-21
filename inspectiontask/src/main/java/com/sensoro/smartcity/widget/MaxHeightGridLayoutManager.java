package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.sensoro.common.utils.AppUtils;

public class MaxHeightGridLayoutManager extends GridLayoutManager {
    private  Context mContext;

    public MaxHeightGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public MaxHeightGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        mContext = context;
    }

    public MaxHeightGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        mContext = context;
    }


    @Override
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        super.setMeasuredDimension(childrenBounds, wSpec, View.MeasureSpec.makeMeasureSpec(AppUtils.dp2px(mContext,500), View.MeasureSpec.AT_MOST));
    }
}
