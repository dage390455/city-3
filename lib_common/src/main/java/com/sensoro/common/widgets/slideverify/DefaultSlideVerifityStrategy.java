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
        if (left < blockSize-10) {
            left = blockSize-10;
        }
        int top = random.nextInt(height - blockSize -blockSize);
        if (top < 0) {
            top = 0;
        }
        return new PositionInfo(left, top);
    }

  

}
