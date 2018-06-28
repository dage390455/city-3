package com.sensoro.smartcity.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class SupSubSpan extends ReplacementSpan {
    private String tv;
    private String sub;

    public SupSubSpan(String text, String sub) {
        this.tv = text;
        this.sub = sub;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) Math.max(paint.measureText(tv), paint.measureText(sub));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint
            paint) {
        canvas.drawText(tv, x, y + paint.ascent(), paint);
        canvas.drawText(sub, x, y - paint.ascent() / 2, paint);
    }
}
