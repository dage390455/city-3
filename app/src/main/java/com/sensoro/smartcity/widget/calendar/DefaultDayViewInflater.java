package com.sensoro.smartcity.widget.calendar;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.sensoro.smartcity.R;


public final class DefaultDayViewInflater extends DayViewInflater {

    private Paint mDecorPaint;
    private int mDecorWidth; // px

    public DefaultDayViewInflater(Context context) {
        super(context);
        mDecorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDecorWidth = dip2px(mContext, 0.5f);
        int targetSDKVersion = 0;
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            targetSDKVersion = packageInfo.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {}
        if (targetSDKVersion >= 23) {
            mDecorPaint.setColor(ContextCompat.getColor(mContext, R.color.c_dddddd));
        } else {
            mDecorPaint.setColor(context.getResources().getColor(R.color.c_dddddd));
        }
        mDecorPaint.setStrokeWidth(mDecorWidth);
    }

    @Override
    public DayViewHolder inflateDayView(ViewGroup container) {
        View dayView = mLayoutInflater.inflate(R.layout.layout_dayview_default, container, false);
        return new DefaultDayViewHolder(dayView);
    }

}
