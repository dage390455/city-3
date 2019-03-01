package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by sensoro on 17/7/18.
 */

public class SensoroEditText extends AppCompatEditText {

    public SensoroEditText(Context context) {
        super(context);
    }

    public SensoroEditText(Context context, AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);
    }

    public SensoroEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isTranslateLeft;

    public void isTranslateLeft(boolean isTranslateLeft) {
        this.isTranslateLeft = isTranslateLeft;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //在布局文件中设置TextView的四周图片，用getCompoundDrawables方法可以获取这4个位置的图片
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable left = drawables[0];
            if (left != null) {
                float textWidth = getPaint().measureText(getHint().toString());
                int padding = getCompoundDrawablePadding();
                int width = 0;
                width = left.getIntrinsicHeight();
                float bodyWidth = textWidth + width + padding ;
                if (isTranslateLeft) {
                    canvas.translate( 30, 0);
                } else {
                    canvas.translate((getWidth() - bodyWidth) / 2, 0);
                }

            }

        }

        super.onDraw(canvas);
    }
}
