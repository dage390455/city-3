package com.sensoro.smartcity.calendarview.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sensoro.smartcity.calendarview.Calendar;
import com.sensoro.smartcity.calendarview.RangeMonthView;

/**
 * 范围选择月视图,当天会有一个小圆点
 * Created by huanghaibin on 2018/9/13.
 */

public class CustomCircleRangeMonthView extends RangeMonthView {

    private final Context mContext;
    private int mRadius;
    private float mPointRadius;
    private Paint mPointPaint = new Paint();

    public CustomCircleRangeMonthView(Context context) {
        super(context);
        mContext = context;
        mPointRadius = dp2Px(2);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.RED);
    }


    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme,
                                     boolean isSelectedPre, boolean isSelectedNext) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;



        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(x, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
            } else {//最后一个，the last
//                canvas.drawRect(x, cy - mRadius, cx, cy + mRadius, mSelectedPaint);
                drawRoundRect(canvas, x, cy);
                canvas.drawRect(x, cy - mRadius, cx, cy + mRadius, mSelectedLastPaint);
//                canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            }
        } else {
            if (isSelectedNext) {
                canvas.drawRect(cx, cy - mRadius, x + mItemWidth, cy + mRadius, mSelectedPaint);
            }
            drawRoundRect(canvas, x, cy);
            if(isSelectedNext){
                canvas.drawRect(cx, cy - mRadius, x+mItemWidth, cy + mRadius, mSelectedLastPaint);
            }

//            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
            //
        }

        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;

        drawRoundRect(canvas, x, cy);
//        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
//        canvas.drawCircle(cx, y +cy , mPointRadius, mPointPaint);
    }

    private void drawRoundRect(Canvas canvas, int x, int cy) {
        RectF rectF = new RectF(x, cy - mRadius, x + mItemWidth, cy + mRadius);
        canvas.drawRoundRect(rectF, dp2Px(8), dp2Px(8), mSelectedLastPaint);
    }

    private int dp2Px(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp*density+0.5f);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected, boolean isSelectedPre, boolean isSelectedNext) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        boolean isInRange = isInRange(calendar);
        boolean isEnable = !onCalendarIntercept(calendar);

//        if (isSelectedPre) {
//            if (isSelectedNext) {
//                canvas.drawText(String.valueOf(calendar.getDay()),
//                        cx,
//                        baselineY,
//                        mSelectTextPaint);
//            } else {
//                canvas.drawText(String.valueOf(calendar.getDay()),
//                        cx,
//                        baselineY,
//                        mSelectedTextLastColor);
//            }
//        } else {
//            if (isSelectedNext) {
//                canvas.drawText(String.valueOf(calendar.getDay()),
//                        cx,
//                        baselineY,
//                        mSelectTextPaint);
//            }
//            canvas.drawText(String.valueOf(calendar.getDay()),
//                    cx,
//                    baselineY,
//                    mSelectedTextLastColor);
//
//        }
        if (isSelected) {
            if(!isSelectedPre){
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSelectedTextLastColor);
            }else if(!isSelectedNext){
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSelectedTextLastColor);
            }else{
                canvas.drawText(String.valueOf(calendar.getDay()),
                        cx,
                        baselineY,
                        mSelectTextPaint);
            }

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

        if(calendar.isCurrentDay()) {
            mPointPaint.setColor(isSelected ? Color.WHITE : Color.parseColor("#1DBB99"));
            canvas.drawCircle(cx,baselineY+dp2Px(6),mPointRadius,mPointPaint);
        }

    }
}
