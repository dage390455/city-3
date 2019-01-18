package com.sensoro.smartcity.calendarview.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sensoro.smartcity.calendarview.Calendar;
import com.sensoro.smartcity.calendarview.RangeWeekView;

/**
 * 范围选择周视图
 * Created by huanghaibin on 2018/9/13.
 */

public class CustomRangeWeekView extends RangeWeekView {

    private int mRadius;

    public CustomRangeWeekView(Context context) {
        super(context);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme,
                                     boolean isSelectedPre, boolean isSelectedNext) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;

        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(x, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
            } else {//最后一个，the last
                canvas.drawRect(x, cy - mRadius, cx, cy + mRadius,mSelectedPaint);
                drawRoundRect(canvas,x,cy);
//                canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            }
        } else {
            if (isSelectedNext) {
                canvas.drawRect(cx, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
            }
            drawRoundRect(canvas,x,cy);
//            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);

        }
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        drawRoundRect(canvas,x,cy);
//        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }
    private void drawRoundRect(Canvas canvas, int x, int cy) {
        RectF rectF = new RectF(x, cy - mRadius, x+mItemWidth, cy + mRadius);
        canvas.drawRoundRect(rectF,10,10,mSelectedPaint);
    }
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine;
        int cx = x + mItemWidth / 2;
        boolean isInRange = isInRange(calendar);
        boolean isEnable = !onCalendarIntercept(calendar);
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() && isInRange && isEnable? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
