package com.sensoro.smartcity.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.AppUtils;

import java.util.Locale;

/**
 * 拍摄button的自定义view
 * Created by zhaoshuang on 17/2/8.
 */

public class RecordedButton extends View {

    private int measuredWidth = -1;
    private Paint paint;
    private int colorGray;
    private float radius1;
    private float innerRadius;
    private float zoom = 0.7f;//初始化缩放比例
    private int dp5;
    private Paint paintProgress;
    private int colorBlue;
    private float girth;
    private RectF oval;
    private int max;
    private OnGestureListener onGestureListener;
    private int animTime = 200;
    private float downX;
    private float downY;
    private boolean closeMode = true;
    private RectF recordRectF;
    private Context mContext;
    private Paint ringPaint;
    private int centerWidth;
    private float ringRadius;
    private String timeText;
    private Paint textPaint;
    private boolean isShowRoundRectF = false;
    private boolean isRecording = false;
    private int innerTransparentColor;
    private int innerRedColor;

    public RecordedButton(Context context) {
        super(context);
        init(context);
    }

    public RecordedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        dp5 = (int) getResources().getDimension(R.dimen.dp5);
        colorGray = getResources().getColor(R.color.gray);
        colorBlue = getResources().getColor(R.color.white);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        innerTransparentColor = mContext.getResources().getColor(R.color.c_03ffffff);
        innerRedColor = mContext.getResources().getColor(R.color.c_f34a4a);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
//        textPaint.setStrokeWidth(AppUtils.sp2px(mContext,16));
        textPaint.setTextSize(AppUtils.dp2px(mContext,16));
        textPaint.setTextAlign(Paint.Align.CENTER);

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setColor(mContext.getResources().getColor(R.color.c_4dffffff));
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(AppUtils.dp2px(mContext,7));

        paintProgress = new Paint();
        paintProgress.setAntiAlias(true);
        paintProgress.setColor(colorBlue);
        paintProgress.setStrokeWidth(AppUtils.dp2px(mContext,7));
        paintProgress.setStyle(Paint.Style.STROKE);

        oval = new RectF();
        recordRectF = new RectF();
    }

    public void retake() {
        isRecording = false;
        girth = 0;
        invalidate();
    }

    public interface OnGestureListener {
        void onLongClick();

        void onClick();

        void onLift();

        void onOver();
    }

    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.onGestureListener = onGestureListener;
    }

    private final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (onGestureListener != null) {
                startAnim(0, 1 - zoom);
                onGestureListener.onLongClick();
                closeMode = true;
            }
        }
    };

    public void setIsRecording(boolean isRecording){
        this.isRecording = isRecording;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                myHandler.sendEmptyMessageDelayed(0, animTime);
//                downX = event.getX();
//                downY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                float upX = event.getX();
//                float upY = event.getY();
//                if (myHandler.hasMessages(0)) {
//                    myHandler.removeMessages(0);
//                    if (Math.abs(upX - downX) < dp5 && Math.abs(upY - downY) < dp5) {
//                        if (onGestureListener != null) onGestureListener.onClick();
//                    }
//                } else if (onGestureListener != null && closeMode) {
//                    onGestureListener.onLift();
//                    closeButton();
//                }
//                break;
//        }
//        return true;
//    }

    public void closeButton() {

//        if (closeMode) {
//            closeMode = false;
////            startAnim(1 - zoom, 0);
            girth = 0;
            invalidate();
//        }
    }

    private void startAnim(float start, float end) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(animTime);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                radius1 = measuredWidth * (zoom + value) / 2;
//                innerRadius = measuredWidth * (zoom - value) / 2.5f;
                invalidate();
            }
        });
        va.start();
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setProgress(float progress) {

        float ratio = progress / max;
        girth = 370 * ratio;

        int time = (int) ((6000 - progress)/1000);
        if (time > 0) {
            timeText = String.format(Locale.ROOT,"%ds",time);
        }else{
            timeText = null;
        }
        invalidate();

        if (ratio >= 1) {
            if (onGestureListener != null) onGestureListener.onOver();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (measuredWidth == -1) {
            measuredWidth = getMeasuredWidth();

//            radius1 = measuredWidth * zoom / 2;
            centerWidth = measuredWidth / 2;
//            innerRadius = measuredWidth * zoom / 2.5f;
            innerRadius = (measuredWidth - AppUtils.dp2px(mContext,14))/2;

            int ringWidth = AppUtils.dp2px(mContext, 7) / 2;
            ringRadius = centerWidth - ringWidth;


            //设置绘制大小
            oval.left = ringWidth;
            oval.top = ringWidth;
            oval.right = measuredWidth - ringWidth;
            oval.bottom = measuredWidth - ringWidth;

            int dp12 = AppUtils.dp2px(mContext, 12);
            recordRectF.left  = centerWidth - dp12;
            recordRectF.top  = centerWidth - dp12;
            recordRectF.right  = centerWidth + dp12;
            recordRectF.bottom  = centerWidth + dp12;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制外圈圆 radius1代表绘制半径
//        paint.setColor(colorGray);
//        canvas.drawCircle(measuredWidth / 2, measuredWidth / 2, radius1, paint);

        //绘制内圈圆 radius2代表绘制半径
//        paint.setColor(Color.WHITE);

        if (isRecording) {
            paint.setColor(innerTransparentColor);
            canvas.drawCircle(centerWidth, centerWidth, innerRadius, paint);
            if (timeText == null) {
                int corner = AppUtils.dp2px(mContext, 4);
                paint.setColor(innerRedColor);
                canvas.drawRoundRect(recordRectF,corner,corner,paint);
            }else{
                canvas.drawText(timeText,centerWidth ,centerWidth + AppUtils.dp2px(mContext,3),textPaint);
            }
        }else{
            paint.setColor(innerRedColor);
            canvas.drawCircle(centerWidth, centerWidth, innerRadius, paint);
        }



        canvas.drawCircle(centerWidth,centerWidth,ringRadius,ringPaint);

        //绘制进度 270表示以圆的270度为起点, 绘制girth长度的弧线
        canvas.drawArc(oval, 270, girth, false, paintProgress);

    }
    public void onDestroy(){
        myHandler.removeCallbacksAndMessages(null);
    }
}
