package com.sensoro.common.widgets.slideverify;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.sensoro.common.R;

/**
 * Created by luozhanming on 2018/1/17.
 */

class TextSeekbar extends AppCompatSeekBar {


    private Paint textPaint;

    public TextSeekbar(Context context) {
        super(context);
    }

    public TextSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.SlideSeekbarSytle);
    }

    public TextSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        int textSize = Utils.dp2px(context, 12);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#252525"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (getHeight() / 2 - top / 2 - bottom / 2);//基线中间点的y轴计算公式
//        canvas.drawText("请拖动上方滑块完成拼图", getWidth() / 2, baseLineY, textPaint);
    }


}
