package com.sensoro.common.manger;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sensoro.common.utils.AppUtils;

public class MaxHeightLinearLayoutManager extends LinearLayoutManager {

    private  Context mContext;

    public MaxHeightLinearLayoutManager(Context context) {
        super(context);
        mContext = context;
    }

    public MaxHeightLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mContext = context;
    }

    @Override
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        super.setMeasuredDimension(childrenBounds, wSpec, View.MeasureSpec.makeMeasureSpec(AppUtils.dp2px(mContext,400), View.MeasureSpec.AT_MOST));
    }
}
