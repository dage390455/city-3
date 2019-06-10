package com.sensoro.smartcity.widget.calendar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;

public class CalendarDayViewInflater extends DayViewInflater{

    public CalendarDayViewInflater(Context context) {
        super(context);
    }

    @Override
    public DayViewHolder inflateDayView(ViewGroup container) {
        View dayView = mLayoutInflater.inflate(R.layout.layout_dayview_default, container, false);
        return new CustomDayViewHolder(dayView);
    }

    @Override
    public Decor inflateHorizontalDecor(ViewGroup container, int row, int totalRow) {
        return new Decor(mLayoutInflater.inflate(R.layout.view_horizontal_decor, container, false));
    }

    @Override
    public boolean isShowHorizontalDecor(int row, int realRowCount) {
        if(row == 0 || row == realRowCount) return false;
        return super.isShowHorizontalDecor(row, realRowCount);
    }

    public static class CustomDayViewHolder extends DayViewHolder{

        protected TextView tvDay;
        private int mPrevMonthDayTextColor;
        private int mNextMonthDayTextColor;

        public CustomDayViewHolder(View dayView) {
            super(dayView);
            tvDay = (TextView) dayView.findViewById(R.id.tvDay);
            mPrevMonthDayTextColor = ContextCompat.getColor(mContext, R.color.c_999999);
            mNextMonthDayTextColor = ContextCompat.getColor(mContext, R.color.c_dddddd);
        }

        @Override
        public void setCurrentMonthDayText(FullDay day, boolean isSelected) {
            if(day.getWeekOf() == FullDay.WEEK_1 || day.getWeekOf() == FullDay.WEEK_7)
                tvDay.setTextColor(mPrevMonthDayTextColor);
            boolean oldSelected = tvDay.isSelected();
            tvDay.setText(String.valueOf(day.getDay()));
            tvDay.setSelected(isSelected);
            if(!oldSelected && isSelected){
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(ObjectAnimator.ofFloat(tvDay, "rotationX", 0.0f, 360f))
                .with(ObjectAnimator.ofFloat(tvDay, "rotationY", 0.0f, 360f));
                animatorSet.setDuration(500)
                        .start();
            }
        }

        @Override
        public void setPrevMonthDayText(FullDay day) {
            tvDay.setTextColor(mPrevMonthDayTextColor);
            tvDay.setText(String.valueOf(day.getDay()));
        }

        @Override
        public void setNextMonthDayText(FullDay day) {
            tvDay.setTextColor(mNextMonthDayTextColor);
            tvDay.setText(String.valueOf(day.getDay()));
        }

    }
}
