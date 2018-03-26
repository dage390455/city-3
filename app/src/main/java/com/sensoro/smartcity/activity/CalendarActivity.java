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
import com.applikeysolutions.cosmocalendar.selection.criteria.BaseCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.WeekDayCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.CurrentMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.NextMonthCriteria;
import com.applikeysolutions.cosmocalendar.selection.criteria.month.PreviousMonthCriteria;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.EXTRA_ALARM_END_DATE;
import static com.sensoro.smartcity.constant.Constants.EXTRA_ALARM_START_DATE;
import static com.sensoro.smartcity.constant.Constants.WEEK_TITLE_ARRAY;

/**
 * Created by sensoro on 17/8/3.
 */

public class CalendarActivity extends BaseActivity {

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
    private String startDate = null;
    private String endDate = null;

    private CalendarView calendarView;

    private List<BaseCriteria> threeMonthsCriteriaList;
    private WeekDayCriteria fridayCriteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        StatusBarCompat.setStatusBarColor(this);
        init();
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        calendarView.setSelectionType(SelectionType.RANGE);
        calendarView.setOnDayRangeSelectedListener(new OnDayRangeSelectedListener() {
            @Override
            public void onDayRangeSelected(Pair<Day, Day> days) {
                try {
                    Calendar calendarFirst = days.first.getCalendar();
                    Calendar calendarEnd = days.second.getCalendar();
                    int firstMonth = calendarFirst.get(Calendar.MONTH) + 1;
                    int endMonth = calendarEnd.get(Calendar.MONTH) + 1;
                    startDate = calendarFirst.get(Calendar.YEAR) + "/" + firstMonth + "/" + calendarFirst.get(Calendar.DAY_OF_MONTH);
                    endDate = calendarEnd.get(Calendar.YEAR) + "/" + endMonth + "/" + calendarEnd.get(Calendar.DAY_OF_MONTH);
                    String temp_startDate = calendarFirst.get(Calendar.YEAR) + "-" + firstMonth + "-" + calendarFirst.get(Calendar.DAY_OF_MONTH);
                    String temp_endDate = calendarEnd.get(Calendar.YEAR) + "-" + endMonth + "-" + calendarEnd.get(Calendar.DAY_OF_MONTH);
                    startDayTextView.setText("" + calendarFirst.get(Calendar.DAY_OF_MONTH));
                    startDateTextView.setText(calendarFirst.get(Calendar.YEAR) + "/" +firstMonth);
                    startWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);
                    endDayTextView.setText("" + calendarEnd.get(Calendar.DAY_OF_MONTH));
                    endDateTextView.setText(calendarEnd.get(Calendar.YEAR) + "/" + endMonth);
                    endWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_endDate) - 1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        createCriterias();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void createCriterias() {
        fridayCriteria = new WeekDayCriteria(Calendar.FRIDAY);
        threeMonthsCriteriaList = new ArrayList<>();
        threeMonthsCriteriaList.add(new CurrentMonthCriteria());
        threeMonthsCriteriaList.add(new NextMonthCriteria());
        threeMonthsCriteriaList.add(new PreviousMonthCriteria());
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void init(){
        try {
            long endTime = Calendar.getInstance().getTime().getTime();
            long startTime = endTime - 3 * 1000 * 60 * 60 * 24;
            String temp_startDate = DateUtil.getDate(startTime);
            String temp_endDate = DateUtil.getDate(endTime);//"-"

            startDate = DateUtil.getDateByOtherFormat(startTime);//"/"
            endDate = DateUtil.getDateByOtherFormat(endTime);
            startDayTextView.setText(DateUtil.getDayDate(startTime));
            startDateTextView.setText(DateUtil.getYearMonthDate(startTime));
            startWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);
            endDayTextView.setText(DateUtil.getDayDate(endTime));
            endDateTextView.setText(DateUtil.getYearMonthDate(endTime));
            endWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_endDate) - 1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick(R.id.calendar_close)
    public void close() {
        this.finish();
    }


    @OnClick(R.id.calendar_save)
    public void save() {
        if (startDate != null && endDate != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
            intent.putExtra(EXTRA_ALARM_END_DATE, endDate);
            setResult(Constants.RESULT_CODE_CALENDAR, intent);
            this.finish();
        } else {
            if (startDate != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
                intent.putExtra(EXTRA_ALARM_END_DATE, startDate);
                setResult(Constants.RESULT_CODE_CALENDAR, intent);
                this.finish();
            } else {
                Toast.makeText(this, R.string.tips_date_not_null, Toast.LENGTH_SHORT).show();
            }

        }
    }


}
