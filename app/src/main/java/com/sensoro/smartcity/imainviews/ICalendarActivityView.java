package com.sensoro.smartcity.imainviews;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

public interface ICalendarActivityView extends IToast,IActivityIntent{
    void setStartDate(String day,String date,String week);
    void setEndDate(String day,String date,String week);
    void showHistory(boolean onlyOnDay, Day firstDay, Day secondDay);
}
