package com.sensoro.common.widgets.slideverify;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Captcha的拼图区域策咯
 * Created by luozhanming on 2018/1/19.
 */

public abstract class SlideVerifityStrategy {

    protected Context mContext;

    public SlideVerifityStrategy(Context ctx) {
        this.mContext = ctx;
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * 定义缺块的形状
     *
     * @param blockSize 单位dp，注意转化为px
     * @return path of the shape
     */
    public abstract Path getBlockShape(int blockSize);

    /**
     * 定义缺块的位置信息
     *
     * @param width     picture width unit:px
     * @param height    picture height unit:px
     * @param blockSize
     * @return position info of the block
     */
    public abstract PositionInfo getBlockPostionInfo(int width, int height, int blockSize);


}
