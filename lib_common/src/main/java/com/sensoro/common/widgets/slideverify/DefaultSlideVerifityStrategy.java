package com.sensoro.common.widgets.slideverify;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.Random;

/**
 * 默认CaptchaStrategy
 * Created by luozhanming on 2018/1/19.
 */

public class DefaultSlideVerifityStrategy extends SlideVerifityStrategy {

    public DefaultSlideVerifityStrategy(Context ctx) {
        super(ctx);
    }

    @Override
    public Path getBlockShape(int blockSize) {
        int gap = (int) (blockSize / 8f);
        Path path = new Path();
        path.moveTo(0, gap);
        path.rLineTo(2*gap,0);
        path.rLineTo(gap,gap);
        path.rLineTo(gap,0);
        path.rLineTo(gap,-gap);
        path.rLineTo(2*gap,0);
        path.rLineTo(0,2*gap);
        path.rLineTo(gap,gap);
        path.rLineTo(0,gap);
        path.rLineTo(-gap,gap);
        path.rLineTo(0,2*gap);
        path.rLineTo(-7*gap,0);
        path.rLineTo(0,-8*gap);
        path.close();

        return path;
    }

    @Override
    public @NonNull
    PositionInfo getBlockPostionInfo(int width, int height, int blockSize) {
        Random random = new Random();

        int left = width/2+random.nextInt(width/2 - blockSize + 1);
        //Avoid robot frequently and quickly click the start point to access the captcha.
        if (left < blockSize) {
            left = blockSize-10;
        }
        int top = random.nextInt(height - blockSize -blockSize);
        if (top < 0) {
            top = 0;
        }
        return new PositionInfo(left, top);
    }

    @Override
    public @NonNull
    PositionInfo getPositionInfoForSwipeBlock(int width, int height, int blockSize) {
        Random random = new Random();
        int left = random.nextInt(width - blockSize + 1);
        int top = random.nextInt(height - blockSize + 1);
        if (top < 0) {
            top = 0;
        }
        return new PositionInfo(left, top);
    }

    @Override
    public Paint getBlockShadowPaint() {
        Paint shadowPaint = new Paint();
        shadowPaint.setColor(Color.parseColor("#000000"));
        shadowPaint.setAlpha(80);
        shadowPaint.setShadowLayer(10,2,2,Color.parseColor("#000000"));
        return shadowPaint;
    }

    @Override
    public Paint getBlockBitmapPaint() {
        Paint paint = new Paint();
        return paint;
    }


    @Override
    public void decoreateSwipeBlockBitmap(Canvas canvas, Path shape, View view) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
//        paint.setAlpha(100);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5);

//        if (Build.VERSION.SDK_INT >= 11) {
//            setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
//        }

//        Shader mShader = new RadialGradient(100,100,122
//                , new int[]{Color.argb(40,0x00,0x00,0x00),Color.argb(0,0x00,0x00,0x00)}
//                , new float[]{1.0f,1.2f}, Shader.TileMode.MIRROR);
//
//        paint.setShader(mShader);

        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));


        Path path = new Path(shape);
        canvas.drawPath(path, paint);

//        paint.setAlpha(200);
//        paint.setShadowLayer(10,5,5,Color.parseColor("#000000"));
//        canvas.drawPath(path, paint);
    }
}
