package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.RelativeLayout;

import com.sensoro.smartcity.R;

import cn.szx.simplescanner.base.IViewFinder;

/**
 * 覆盖在相机预览上的view，包含扫码框、扫描线、扫码框周围的阴影遮罩等
 */
public class ViewFinderView extends RelativeLayout implements IViewFinder {
    private String tipText = getContext().getResources().getString(R.string.device_deployment_tip);
    private Rect framingRect;//扫码框所占区域
    private float widthRatio = 0.6f;//扫码框宽度占view总宽度的比例
    private float heightWidthRatio = 0.35f;//扫码框的高宽比
    private int leftOffset = -1;//扫码框相对于左边的偏移量，若为负值，则扫码框会水平居中
    private int topOffset = (int) (158 * getContext().getResources().getDisplayMetrics().density);//扫码框相对于顶部的偏移量，若为负值，则扫码框会竖直居中

    private boolean isLaserEnabled = true;//是否显示扫描线
    //    private static final int[] laserAlpha = {0, 64, 128, 192, 255, 192, 128, 64};
//    private int laserAlphaIndex;
//    private static final long animationDelay = 80l;
    private final int laserColor = Color.parseColor("#1DBB99");

    private final int maskColor = Color.parseColor("#7D000000");
    private final int borderColor = Color.parseColor("#ffffffff");
    private int tipColor = Color.parseColor("#ffffffff");
    private final int borderStrokeWidth = (int) (4 * getContext().getResources().getDisplayMetrics().density);
    protected int borderLineLength = (int) (14 * getContext().getResources().getDisplayMetrics().density);
    protected int tipTextMarginTop = (int) (28 * getContext().getResources().getDisplayMetrics().density);

    protected Paint laserPaint;
    protected Paint maskPaint;
    protected Paint borderPaint;
    private TextPaint tipPaint;
    private int laserOffset = 0;
    private int laserHeight = (int) (3 * getContext().getResources().getDisplayMetrics().density);
    private int count = 0;
    private float tipTextSize = 12 * getContext().getResources().getDisplayMetrics().scaledDensity;//12sp
    private float canvasTranslateY;
    private float canvasTranslateX;
    private int marginBorder = (int) (20 * getContext().getResources().getDisplayMetrics().density);//距离屏幕的边距20dp


    public ViewFinderView(Context context) {
        super(context);
        initDraw();
//        initLayout();
    }

    private void initDraw() {
        setWillNotDraw(false);//需要进行绘制

        //扫描线画笔
        laserPaint = new Paint();
        laserPaint.setColor(laserColor);
        laserPaint.setStyle(Paint.Style.FILL);

        //阴影遮罩画笔
        maskPaint = new Paint();
        maskPaint.setColor(maskColor);

        //边框画笔
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderStrokeWidth);
        borderPaint.setAntiAlias(true);

        //tip 文字画笔
        tipPaint = new TextPaint();
        tipPaint.setColor(tipColor);
        tipPaint.setStyle(Paint.Style.FILL);
        tipPaint.setTextSize(tipTextSize);
    }

//    private void initLayout() {
//        LayoutInflater.from(getContext()).inflate(R.layout.view_view_finder, this, true);
//    }

    @Override
    public void onDraw(Canvas canvas) {
        if (getFramingRect() == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);

        if (isLaserEnabled) {
            drawLaser(canvas);
        }

        drawTipText(canvas);
    }

    /**
     * 绘制提示文本
     *
     * @param canvas
     */
    private void drawTipText(Canvas canvas) {
        StaticLayout staticLayout = new StaticLayout(tipText, tipPaint, canvas.getWidth() - marginBorder * 2, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false);
        canvas.save();
        canvas.translate(canvasTranslateX, canvasTranslateY);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制扫码框四周的阴影遮罩
     */
    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();

        canvas.drawRect(0, 0, width, framingRect.top, maskPaint);//扫码框顶部阴影
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom, maskPaint);//扫码框左边阴影
        canvas.drawRect(framingRect.right, framingRect.top, width, framingRect.bottom, maskPaint);//扫码框右边阴影
        canvas.drawRect(0, framingRect.bottom, width, height, maskPaint);//扫码框底部阴影
    }

    /**
     * 绘制扫码框的边框
     */
    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();

        // Top-left corner
        Path path = new Path();
        path.moveTo(framingRect.left, framingRect.top + borderLineLength);
        path.lineTo(framingRect.left, framingRect.top);
        path.lineTo(framingRect.left + borderLineLength, framingRect.top);
        canvas.drawPath(path, borderPaint);

        // Top-right corner
        path.moveTo(framingRect.right, framingRect.top + borderLineLength);
        path.lineTo(framingRect.right, framingRect.top);
        path.lineTo(framingRect.right - borderLineLength, framingRect.top);
        canvas.drawPath(path, borderPaint);

        // Bottom-right corner
        path.moveTo(framingRect.right, framingRect.bottom - borderLineLength);
        path.lineTo(framingRect.right, framingRect.bottom);
        path.lineTo(framingRect.right - borderLineLength, framingRect.bottom);
        canvas.drawPath(path, borderPaint);

        // Bottom-left corner
        path.moveTo(framingRect.left, framingRect.bottom - borderLineLength);
        path.lineTo(framingRect.left, framingRect.bottom);
        path.lineTo(framingRect.left + borderLineLength, framingRect.bottom);
        canvas.drawPath(path, borderPaint);
    }

    /**
     * 绘制扫描线
     */
    public void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();

//        laserPaint.setAlpha(laserAlpha[laserAlphaIndex]);
//        laserAlphaIndex = (laserAlphaIndex + 1) % laserAlpha.length;
//        int middle = framingRect.height() / 2 + framingRect.top;

        int top = framingRect.top + laserOffset * count;
        int bottom = top + laserHeight;
        if (bottom > framingRect.bottom) {
            count = 0;
        } else {
            count++;
        }
        canvas.drawRect(framingRect.left, top, framingRect.right, bottom, laserPaint);
        postInvalidate(framingRect.left,
                framingRect.top,
                framingRect.right,
                framingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    /**
     * 设置framingRect的值（扫码框所占的区域）
     */
    public synchronized void updateFramingRect() {
        Point viewSize = new Point(getWidth(), getHeight());
        int width, height;
        width = (int) (getWidth() * widthRatio);
        height = width;
        laserOffset = width / 100;

        int left, top;
        if (leftOffset < 0) {
            left = (viewSize.x - width) / 2;//水平居中
        } else {
            left = leftOffset;
        }
        if (topOffset < 0) {
            top = (viewSize.y - height) / 2;//竖直居中

        } else {
            top = topOffset;
        }
        framingRect = new Rect(left, top, left + width, top + height);
        Rect rect = new Rect();
        tipPaint.getTextBounds(tipText, 0, tipText.length(), rect);
        canvasTranslateX = marginBorder;
        canvasTranslateY = framingRect.bottom + tipTextMarginTop - rect.height();


    }

    public void setTipText(String text) {
        this.tipText = text;
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return framingRect;
    }
}