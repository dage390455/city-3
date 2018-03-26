package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by sensoro on 17/12/11.
 */

public class SensoroXGridLayoutManager extends GridLayoutManager {
    public SensoroXGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SensoroXGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public SensoroXGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("SensoroXGridLayoutManager.exception----->");
        }
    }
}
