package com.sensoro.smartcity.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sensoro.smartcity.R;

/**
 * Created by sensoro on 17/7/11.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int color;
    private boolean isDrawTop = true;
    private boolean isDrawFirstLeft = true;
    private boolean isDrawLine = false;
    private boolean isDrawRight = true;


    public SpacesItemDecoration(boolean isDrawLine, int space) {
        this.space = space;
        this.isDrawTop = true;
        this.isDrawLine = isDrawLine;
        this.color = R.color.c_e7e7e7;
    }

    public SpacesItemDecoration(boolean isDrawLine, int space,boolean isDrawRight) {
        this.space = space;
        this.isDrawTop = true;
        this.isDrawLine = isDrawLine;
        this.isDrawRight = isDrawRight;
        this.color = R.color.c_e7e7e7;
    }

    public SpacesItemDecoration(boolean isDrawLine, int space,boolean isDrawRight,boolean isDrawTop) {
        this.space = space;
        this.isDrawTop = isDrawTop;
        this.isDrawLine = isDrawLine;
        this.isDrawRight = isDrawRight;
        this.color = R.color.c_e7e7e7;
    }

    public SpacesItemDecoration(int space) {
        this.space = space;
        this.isDrawTop = true;
        this.isDrawLine = false;
        this.color = R.color.c_e7e7e7;
    }

    public SpacesItemDecoration(int space, int color) {
        this.space = space;
        this.isDrawTop = true;
        this.color = color;
        this.isDrawLine = true;
    }

    public SpacesItemDecoration(int space, boolean isDrawTop, boolean isDrawFirstLeft) {
        this.space = space;
        this.isDrawTop = isDrawTop;
        this.isDrawFirstLeft = isDrawFirstLeft;
        this.color = R.color.item_sensor_line;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //先初始化一个Paint来简单指定一下Canvas的颜色，就黑的吧！  这个地方我们可以设置 paint 的 颜色大小
        if (!isDrawLine) {
            return ;
        }
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(parent.getContext().getResources().getColor(color));

        // 获得RecyclerView中总条目数量
        int childCount = parent.getChildCount();
        //遍历一下
        for (int i = 0; i < childCount; i++) {

            // 获得子View，也就是一个条目的View，准备给他画上边框
            View childView = parent.getChildAt(i);
            //先获得子View的长宽，以及在屏幕上的位置，方便我们得到边框的具体坐标
            float x = childView.getX();
            float y = childView.getY();
            int width = childView.getWidth();
            int height = childView.getHeight();
            //根据这些点画条目的四周的线
            if (isDrawTop) {
                c.drawLine(x, y, x + width, y, paint);
            }
            c.drawLine(x + width, y, x + width, y + height, paint);
            c.drawLine(x, y, x, y + height, paint);
            c.drawLine(x, y + height, x + width, y + height, paint);
            //当然了，这里大家肯定是要根据自己不同的设计稿进行画线，或者画一些其他的东西，都可以在这里搞，非常方便
        }
        super.onDraw(c, parent, state);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0 ) {
            if (isDrawFirstLeft) {
                outRect.left = space;
            }else{
                outRect.left = 0;
            }
        }else{
            outRect.left = space;;
        }

        if(isDrawRight){
            outRect.right = space;
        }
        outRect.bottom = space;

        if (isDrawTop) {
            outRect.top = space;
        }else{
            outRect.top = 0;
        }
    }

    public int getSpace(){
        return space;
    }
}
