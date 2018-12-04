package com.sensoro.smartcity.widget.divider;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 仅仅recycleView item间的距离
 */
public class TopSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public TopSpaceItemDecoration(int topSpace) {
        this.space = topSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        outRect.top = space;

    }
}
