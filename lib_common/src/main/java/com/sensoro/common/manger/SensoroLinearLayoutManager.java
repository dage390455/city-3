package com.sensoro.common.manger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sensoro on 17/5/2.
 */

public class SensoroLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollVertical = true;

    public SensoroLinearLayoutManager(Context context) {
        super(context);
    }

    public SensoroLinearLayoutManager(Context context, boolean isScrollVertical) {
        this(context);
        this.isScrollVertical = isScrollVertical;
    }

    public SensoroLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SensoroLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollVertically() {
        if (isScrollVertical) {
            return super.canScrollVertically();
        } else {
            return false;
        }


    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (recycler != null) {
            detachAndScrapAttachedViews(recycler);
            int sumWidth = getWidth();

            int curLineWidth = 0, curLineTop = 0;
            int lastLineMaxHeight = 0;
            int rowIndex = 0;
            removeAllViews();
            for (int i = 0; i < getItemCount(); i++) {
                View view = recycler.getViewForPosition(i);
                measureChildWithMargins(view, 0, 0);
                int width = getDecoratedMeasuredWidth(view);
                int height = getDecoratedMeasuredHeight(view);

                curLineWidth += width;
                if (curLineWidth <= sumWidth) {
                    //不需要换行
                    if (rowIndex == 0 && i > 0) {
                        layoutDecorated(view, curLineWidth - width, curLineTop, curLineWidth, curLineTop + height);
                        //比较当前行多有item的最大高度
                        lastLineMaxHeight = Math.max(lastLineMaxHeight, height);
                    } else {
                        layoutDecorated(view, curLineWidth - width, curLineTop, curLineWidth, curLineTop + height);
                        //比较当前行多有item的最大高度
                        lastLineMaxHeight = Math.max(lastLineMaxHeight, height);
                    }


                } else {//换行
                    rowIndex++;
                    curLineWidth = width;
                    if (lastLineMaxHeight == 0) {
                        lastLineMaxHeight = height;
                    }
                    //记录当前行top
                    curLineTop += lastLineMaxHeight;

                    layoutDecorated(view, 0, curLineTop, width, curLineTop + height);
                    lastLineMaxHeight = height;
                }
                addView(view);
            }
        }
    }

}
