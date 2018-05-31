package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.OrientationHelper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.listeners.OnDayRangeSelectedListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ICalendarActivityView;
import com.sensoro.smartcity.presenter.CalendarActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/8/3.
 */

public class CalendarActivity extends BaseActivity<ICalendarActivityView, CalendarActivityPresenter> implements
        ICalendarActivityView, OnDayRangeSelectedListener, CalendarView.OnDaySelectObserver {

    @BindView(R.id.calendar_close)
    ImageView closeImageView;
    @BindView(R.id.calendar_start_day)
    TextView startDayTextView;
    @BindView(R.id.calendar_start_date)
    TextView startDateTextView;
    @BindView(R.id.calendar_start_week)
    TextView startWeekTextView;
    @BindView(R.id.calendar_end_day)
    TextView endDayTextView;
    @BindView(R.id.calendar_end_date)
    TextView endDateTextView;
    @BindView(R.id.calendar_end_week)
    TextView endWeekTextView;
    @BindView(R.id.calendar_view)
    CalendarView calendarView;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(mActivity);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        calendarView.setSelectionType(SelectionType.RANGE);
        calendarView.setOnDayRangeSelectedListener(this);
        calendarView.setOnDayClickListener(this);
        mPrestener.initData(mActivity);
//        init();
//
//        createCriterias();
//        showHistory(isMultipleOr, firstDay, secondDay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPrestener.freshDate();
    }

    @Override
    public void showHistory(boolean onlyOnDay, Day firstDay, Day secondDay) {
        if (firstDay != null) {
            if (calendarView.getSelectionManager() instanceof RangeSelectionManager) {
                RangeSelectionManager rangeSelectionManager =
                        (RangeSelectionManager) calendarView.getSelectionManager();
                if (onlyOnDay) {
                    rangeSelectionManager.toggleDay(firstDay);
                } else {
                    rangeSelectionManager.toggleDay(firstDay);
                    rangeSelectionManager.toggleDay(secondDay);
                }
                calendarView.update();
            }
        }
    }
//    if (calendarView.getSelectionManager() instanceof RangeSelectionManager) {
//        RangeSelectionManager rangeSelectionManager =
//                (RangeSelectionManager) calendarView.getSelectionManager();
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 3);
//        rangeSelectionManager.toggleDay(new Day(Calendar.getInstance()));
//        rangeSelectionManager.toggleDay(new Day(calendar));
//        calendarView.update();
//    }


    @Override
    protected CalendarActivityPresenter createPresenter() {
        return new CalendarActivityPresenter();
    }


    @OnClick(R.id.calendar_close)
    public void close() {
        finishAc();
    }


    @OnClick(R.id.calendar_save)
    public void save() {
        mPrestener.saveDates();
    }


    @Override
    public void onDayRangeSelected(Pair<Day, Day> days) {
        mPrestener.clickDayRangeSelected(days);
    }

    @Override
    public void onClick(List<Day> dayList) {
        mPrestener.clickDay(dayList);
    }

    @Override
    public void setStartDate(String day, String date, String week) {
        startDayTextView.setText(day);
        startDateTextView.setText(date);
        startWeekTextView.setText(week);
    }

    @Override
    public void setEndDate(String day, String date, String week) {
        endDayTextView.setText(day);
        endDateTextView.setText(date);
        endWeekTextView.setText(week);
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int requestCode) {
    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }

    @Override
    public void toastShort(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastShort(int resId) {
        Toast.makeText(mActivity, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void toastLong(int resId) {

    }
}
