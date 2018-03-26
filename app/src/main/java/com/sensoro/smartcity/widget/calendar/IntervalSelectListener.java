package com.sensoro.smartcity.widget.calendar;

import java.util.List;


public abstract class IntervalSelectListener {
    public abstract void onIntervalSelect(List<FullDay> selectedDays);
    public boolean onInterceptSelect(List<FullDay> selectedDays, FullDay selectingDay){
        return false;
    }
}
