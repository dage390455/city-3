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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.EXTRA_ALARM_END_DATE;
import static com.sensoro.smartcity.constant.Constants.EXTRA_ALARM_START_DATE;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_END_TIME;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_START_TIME;
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
    private Day firstDay;
    private Day secondDay;
    private CalendarView calendarView;

    private List<BaseCriteria> threeMonthsCriteriaList;
    private WeekDayCriteria fridayCriteria;
    private volatile boolean isMultiple = true;
    private volatile boolean isMultipleOr = true;

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
                    startDayTextView.setText("" + calendarFirst.get(Calendar.DAY_OF_MONTH));
                    startDateTextView.setText(calendarFirst.get(Calendar.YEAR) + "/" + firstMonth);
                    startWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);
                    endDayTextView.setText("" + calendarEnd.get(Calendar.DAY_OF_MONTH));
                    endDateTextView.setText(calendarEnd.get(Calendar.YEAR) + "/" + endMonth);
                    endWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_endDate) - 1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        calendarView.setOnDayClickListener(new CalendarView.OnDaySelectObserver() {
            @Override
            public void onClick(List<Day> dayList) {
                isMultiple = false;
                for (Day day : dayList) {
                    String s = day.toString();
                    Calendar calendar = day.getCalendar();
                    Date time = calendar.getTime();
                    int dayNumber = day.getDayNumber();
                    int dayOfWeek = day.getDayOfWeek();
                }
                Day day = dayList.get(0);
                setSelect(day);
            }
        });
//        calendarView
//        calendarView.setlist
        createCriterias();
        showHistory(isMultipleOr, firstDay, secondDay);
//        List<Day> days = new ArrayList<>();
//        days.add(firstDay);
//        calendarView.displyDays(isMultipleOr, days, firstDay, secondDay);
    }

    private void showHistory(boolean isMultiple, Day firstDay, Day secondDay) {
        if (firstDay != null) {
            if (calendarView.getSelectionManager() instanceof RangeSelectionManager) {
                RangeSelectionManager rangeSelectionManager =
                        (RangeSelectionManager) calendarView.getSelectionManager();
                if (isMultiple) {
                    rangeSelectionManager.toggleDay(firstDay);
                    rangeSelectionManager.toggleDay(secondDay);
                } else {
                    rangeSelectionManager.toggleDay(firstDay);
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

    private void setSelect(Day day) {
        try {
            Calendar calendarFirst = day.getCalendar();
            int firstMonth = calendarFirst.get(Calendar.MONTH) + 1;
            startDate = calendarFirst.get(Calendar.YEAR) + "/" + firstMonth + "/" + calendarFirst.get
                    (Calendar.DAY_OF_MONTH);
            String temp_startDate = calendarFirst.get(Calendar.YEAR) + "-" + firstMonth + "-" + calendarFirst
                    .get(Calendar.DAY_OF_MONTH);
            startDayTextView.setText("" + calendarFirst.get(Calendar.DAY_OF_MONTH));
            startDateTextView.setText(calendarFirst.get(Calendar.YEAR) + "/" + firstMonth);

            startWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);

            endDayTextView.setText("-");
            endDateTextView.setText("");
            endWeekTextView.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void init() {
        try {
//            long endTime = Calendar.getInstance().getTime().getTime();
//            long startTime = endTime - 3 * 1000 * 60 * 60 * 24;
            long startTime = getIntent().getLongExtra(PREFERENCE_KEY_START_TIME, -1);
            long endTime = getIntent().getLongExtra(PREFERENCE_KEY_END_TIME, -1);
//            if (start_time != -1) {
//                startTime = start_time;
//            }
//            if (end_time != -1) {
//                endTime = end_time - 1000 * 60 * 60 * 24;
//            }
            if (startTime == -1 || endTime == -1) {
                isMultipleOr = false;
                startDayTextView.setText("-");
                startDateTextView.setText("");
                startWeekTextView.setText("");
                //
                endDayTextView.setText("-");
                endDateTextView.setText("");
                endWeekTextView.setText("");
                return;
            }
            endTime -= 1000 * 60 * 60 * 24;
            String temp_startDate = DateUtil.getDate(startTime);
            String temp_endDate = DateUtil.getDate(endTime);//"-"
            firstDay = new Day(new Date(startTime));
            secondDay = new Day(new Date(endTime));
            startDate = DateUtil.getDateByOtherFormat(startTime);//"/"
            endDate = DateUtil.getDateByOtherFormat(endTime);

            startDayTextView.setText(DateUtil.getDayDate(startTime));
            startDateTextView.setText(DateUtil.getYearMonthDate(startTime));
            startWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_startDate) - 1]);

            if (startTime == endTime) {
                isMultipleOr = false;
                endDayTextView.setText("-");
                endDateTextView.setText("");
                endWeekTextView.setText("");
            } else {
                isMultipleOr = true;
                endDayTextView.setText(DateUtil.getDayDate(endTime));
                endDateTextView.setText(DateUtil.getYearMonthDate(endTime));
                endWeekTextView.setText("" + WEEK_TITLE_ARRAY[DateUtil.dayForWeek(temp_endDate) - 1]);

            }
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
        if (isMultiple) {
            if (startDate != null && endDate != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ALARM_START_DATE, startDate);
                intent.putExtra(EXTRA_ALARM_END_DATE, endDate);
                setResult(Constants.RESULT_CODE_CALENDAR, intent);
                this.finish();
            }
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
