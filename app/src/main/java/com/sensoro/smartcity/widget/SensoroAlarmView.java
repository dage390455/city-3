package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/8/8.
 */

public class SensoroAlarmView extends ImageView {
    private Paint paint;
    private int radius = 13;
    private boolean isStarting = false;
    private int offset = 0;
    private int alpha_offset = 0;
    private Paint srcPaint;
    private Paint textPaint;
    private String text = "";
    private List<Integer> alphaList = new ArrayList<Integer>();
    private List<Integer> startWidthList = new ArrayList<Integer>();

    public SensoroAlarmView(Context context) {
        super(context);
        init();
    }

    public SensoroAlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SensoroAlarmView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        srcPaint = new Paint();
        paint.setAntiAlias(true);
        srcPaint.setAntiAlias(true);
        srcPaint.setColor(Color.RED);
        paint.setColor(Color.RED);// 此处颜色可以改为自己喜欢的
        alphaList.add(90);// 圆心的不透明度
        alphaList.add(80);
        startWidthList.add(0);//085213
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        start();

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);// 颜色：完全透明
        if (isStarting) {
            if (offset >= 60) {
                offset = 0;
                alpha_offset = 0;
                isStarting = false;
                paint.setAlpha(0);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isStarting = true;
                        postInvalidate();
                    }
                }, 500);

            } else {
                for (int i = 0; i < 2; i++) {
                    int radius_offset =  (i + 1) * offset ;
                    int alpha = (int)(alphaList.get(i) - alpha_offset * 2.2) ;
                    if (alpha >= 0) {
                        paint.setAlpha(alpha) ;
                    } else {
                        paint.setAlpha(0) ;
                    }
                    canvas.drawCircle(getWidth()/ 2, getHeight()/2,  (int)(radius + radius_offset * 0.6 ), paint);
                }
                if (offset > 10) {
                    offset += 1.8;
                } else {
                    offset += 1;
                }
                alpha_offset += 1.8;
            }
            postInvalidate();
        }

        canvas.drawCircle(getWidth()/ 2, getHeight()/2, radius, srcPaint);


    }

    public void setStatus(int status) {
        if (status == Constants.SENSOR_STATUS_ALARM) {
            srcPaint.setColor(getResources().getColor(R.color.sensoro_alarm));
            paint.setColor(getResources().getColor(R.color.sensoro_alarm));
            text = "";
        } else {
            text = "";
            srcPaint.setColor(getResources().getColor(R.color.sensoro_normal));
            paint.setColor(getResources().getColor(R.color.sensoro_normal));
        }
    }

    public void start() {
        isStarting = true;
    }

    // 地震波暂停
    public void stop() {
        isStarting = false;
    }

    public boolean isStarting() {
        return isStarting;
    }


}