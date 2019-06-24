package com.sensoro.common.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class MaxHeightListView extends ListView {
    private int mMaxHeight;

    /**
     * 设置最大高度
     *
     * @param maxHeight 最大高度 px
     */
    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
        // 重绘 RecyclerView
        requestLayout();
    }

    public MaxHeightListView(Context context) {
        super(context);
    }

    public MaxHeightListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (mMaxHeight > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

}