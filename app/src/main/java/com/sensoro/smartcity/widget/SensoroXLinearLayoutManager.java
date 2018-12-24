package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by sensoro on 17/5/2.
 */

public class SensoroXLinearLayoutManager extends LinearLayoutManager {
    public SensoroXLinearLayoutManager(Context context) {
        super(context);
    }

    public SensoroXLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SensoroXLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SensoroXLinearLayoutManager.exception----->");
        }
    }

}
