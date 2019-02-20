package com.sensoro.smartcity.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 固定纵横比的imageView，目前固定的宽高比值为16：9
 */
public class FixedAspectRationImageView extends android.support.v7.widget.AppCompatImageView {
    public FixedAspectRationImageView(Context context) {
        super(context);
    }

    public FixedAspectRationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedAspectRationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height =(int) width * 9 / 16;
        setMeasuredDimension(width,height);
    }
}
