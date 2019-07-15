package com.sensoro.common.widgets;

import android.content.Context;

import com.sensoro.common.utils.AppUtils;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;

/**
 * @author : bin.tian
 * date   : 2019-06-29
 */
public class SensoroTextWidthColorBar extends TextWidthColorBar {
    private Context mContext;
    public SensoroTextWidthColorBar(Context context, Indicator indicator, int color, int height) {
        super(context, indicator, color, height);
        mContext = context;
    }

    @Override
    public int getWidth(int tabWidth) {
        int width = super.getWidth(tabWidth);
        return width + AppUtils.dp2px(mContext, 24);
    }
}
