package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.criteria.BaseCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.WeekDayCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.CurrentMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.NextMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.PreviousMonthCriteria;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICalendarActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.sensoro.smartcity.constant.Constants.EXTRA_ALARM_END_DATE;
import static com.sensoro.smartcity.constant.Constants.EXTRA_ALARM_START_DATE;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_END_TIME;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_START_TIME;
import static com.sensoro.smartcity.constant.Constants.WEEK_TITLE_ARRAY;

public class CalendarActivityPresenter extends BasePresenter<ICalendarActivityView> implements IOnStart {
    private String startDate = null;
    private String endDate = null;
    private Activity mContext;
    private volatile boolean isMultiple = true;
    private long startTime;
    private long endTime;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        startTime = mContext.getIntent().getLongExtra(PREFERENCE_KEY_START_TIME, -1);
        endTime = mContext.getIntent().getLongExtra(PREFERENCE_KEY_END_TIME, -1);
//        createCriterias();
    }

    @Override
    public void onStart() {
        try {
            if (startTime == -1 || endTime == -1) {
                getView().setStartDate("-", "", "");
                getView().setEndDate("-", "", "");
                return;
            }
            endTime -= 1000 * 60 * 60 * 24;
            String temp_startDate = DateUtil.getDate(startTime);
            String temp_endDate = DateUtil.getDate(endTime);//"-"
            Day firstDay = new Day(new Date(startTime));
            Day secondDay = new Day(new Date(endTime));
            startDate = DateUtil.getDateByOtherFormat(startTime);//"/"
            endDate = DateUtil.getDateByOtherFormat(endTime);
            getView().setStartDate(DateUtil.getDayDate(startTime), DateUtil.getYearMonthDate(startTime), "" +
                    WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);
            boolean onlyOneDay = startTime == endTime;
            if (onlyOneDay) {
                getView().setEndDate("-", "", "");
            } else {
                getView().setEndDate(DateUtil.getDayDate(endTime), DateUtil.getYearMonthDate(endTime), "" +
                        WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_endDate) - 1]);
            }
            getView().showHistory(onlyOneDay, firstDay, secondDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCriterias() {
        WeekDayCriteria fridayCriteria = new WeekDayCriteria(Calendar.FRIDAY);
        List<BaseCriteria> threeMonthsCriteriaList = new ArrayList<>();
        threeMonthsCriteriaList.add(new CurrentMonthCriteria());
        threeMonthsCriteriaList.add(new NextMonthCriteria());
        threeMonthsCriteriaList.add(new PreviousMonthCriteria());
    }

    public void clickDayRangeSelected(Pair<Day, Day> days) {
        try {
            isMultiple = true;
            Calendar calendarFirst = days.first.getCalendar();
            Calendar calendarEnd = days.second.getCalendar();
            int firstMonth = calendarFirst.get(Calendar.MONTH) + 1;
            int endMonth = calendarEnd.get(Calendar.MONTH) + 1;
            startDate = calendarFirst.get(Calendar.YEAR) + "/" + firstMonth + "/" + calendarFirst.get
                    (Calendar.DAY_OF_MONTH);
            endDate = calendarEnd.get(Calendar.YEAR) + "/" + endMonth + "/" + calendarEnd.get(Calendar
                    .DAY_OF_MONTH);
            String temp_startDate = calendarFirst.get(Calendar.YEAR) + "-" + firstMonth + "-" + calendarFirst
                    .get(Calendar.DAY_OF_MONTH);
            String temp_endDate = calendarEnd.get(Calendar.YEAR) + "-" + endMonth + "-" + calendarEnd.get
                    (Calendar.DAY_OF_MONTH);
            getView().setStartDate("" + calendarFirst.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) +
                    "/" + firstMonth, "" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);
            getView().setEndDate("" + calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.YEAR) + "/" +
                    endMonth, "" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_endDate) - 1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickDay(List<Day> dayList) {
        isMultiple = false;
        Day day = dayList.get(0);
        try {
            Calendar calendarFirst = day.getCalendar();
            int firstMonth = calendarFirst.get(Calendar.MONTH) + 1;
            startDate = calendarFirst.get(Calendar.YEAR) + "/" + firstMonth + "/" + calendarFirst.get
                    (Calendar.DAY_OF_MONTH);
            String temp_startDate = calendarFirst.get(Calendar.YEAR) + "-" + firstMonth + "-" + calendarFirst
                    .get(Calendar.DAY_OF_MONTH);
            getView().setStartDate("" + calendarFirst.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) +
                    "/" + firstMonth, "" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);
            getView().setEndDate("-", "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDates() {
        if (isMultiple) {
            if (startDate != null && endDate != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
                intent.putExtra(EXTRA_ALARM_END_DATE, endDate);
                getView().setIntentResult(Constants.RESULT_CODE_CALENDAR, intent);
                getView().finishAc();
            }
        } else {
            if (startDate != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
                intent.putExtra(EXTRA_ALARM_END_DATE, startDate);
                getView().setIntentResult(Constants.RESULT_CODE_CALENDAR, intent);
                getView().finishAc();
            } else {
                getView().toastShort(mContext.getResources().getString(R.string.tips_date_not_null));
            }
        }
//        if (startDate != null && endDate != null) {
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
//            intent.putExtra(EXTRA_ALARM_END_DATE, endDate);
//            setResult(Constants.RESULT_CODE_CALENDAR, intent);
//            this.finish();
//        } else {
//            if (startDate != null) {
//                Intent intent = new Intent();
//                intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
//                intent.putExtra(EXTRA_ALARM_END_DATE, startDate);
//                setResult(Constants.RESULT_CODE_CALENDAR, intent);
//                this.finish();
//            } else {
//                Toast.makeText(this, R.string.tips_date_not_null, Toast.LENGTH_SHORT).show();
//            }
//
//        }
    }


}
