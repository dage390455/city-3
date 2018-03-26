package com.sensoro.smartcity.widget.calendar;


public abstract class SegmentSelectListener {
    public abstract void onSegmentSelect(FullDay startDay, FullDay endDay);
    public abstract void onSegmentClick(FullDay day) ;
    public boolean onInterceptSelect(FullDay startDay, FullDay endDay){
        return false;
    }
    public boolean onInterceptSelect(FullDay selectingDay){
        return false;
    }
    public void selectedSameDay(FullDay sameDay){

    }
}
