package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.listeners.OnDayRangeSelectedListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.calendarview.CalendarView;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarPopUtils implements OnDayRangeSelectedListener, /*CalendarView.OnDaySelectObserver
        ,*/
        CalendarView.OnCalendarRangeSelectListener,Constants, PopupWindow.OnDismissListener,CalendarView.OnMonthChangeListener {

    private PopupWindow mPopupWindow = null;
    private final Activity mActivity;
    @BindView(R.id.ac_calendar_imv_arrows)
    ImageView acCalendarImvArrows;
    @BindView(R.id.ac_calendar_tv_start_month)
    TextView acCalendarTvStartMonth;
    @BindView(R.id.ac_calendar_tv_start_year)
    TextView acCalendarTvStartYear;
    @BindView(R.id.ac_calendar_tv_end_month)
    TextView acCalendarTvEndMonth;
    @BindView(R.id.ac_calendar_tv_end_year)
    TextView acCalendarTvEndYear;
    @BindView(R.id.calendar_view)
    CalendarView calendarView;
    @BindView(R.id.ac_calendar_tv_cancel)
    Button acCalendarTvCancel;
    @BindView(R.id.ac_calendar_tv_save)
    Button acCalendarTvSave;
    @BindView(R.id.calendar_btn_layout)
    LinearLayout calendarBtnLayout;
    @BindView(R.id.sensor_calendar_date_layout)
    LinearLayout sensorCalendarDateLayout;
    @BindView(R.id.ac_calendar_view_dismiss)
    View dismissiView;
    @BindView(R.id.ac_calendar_imv_arrow_left)
    ImageView acCalendarImvArrowLeft;
    @BindView(R.id.ac_calendar_tv_month_year)
    TextView acCalendarTvMonthYear;
    @BindView(R.id.ac_calendar_imv_arrow_right)
    ImageView acCalendarImvArrowRight;

    private boolean isMultiple;
    private String startDate;
    private String endDate;
    private long startTime;
    private long endTime;
    private OnCalendarPopupCallbackListener listener;
    private View view;

    public CalendarPopUtils(Activity activity) {
        mActivity = activity;
    }

    private void init() {
        mPopupWindow = new PopupWindow(mActivity);
        view = LayoutInflater.from(mActivity).inflate(R.layout.activity_calendar_test, null);
        ButterKnife.bind(this, view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_aa000000)));
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.DialogFragmentDropDownAnim);
        initView();
        mPopupWindow.setContentView(view);
    }

    private void initView() {
//        calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
//        calendarView.setSelectionType(SelectionType.RANGE);
//        calendarView.setOnDayRangeSelectedListener(this);
//        calendarView.setOnDayClickListener(this);
//        calendarView.setRange(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay(),
//                calendarView.getCurYear() + 2, 12, 31);
        calendarView.setOnCalendarRangeSelectListener(this);
        calendarView.setOnMonthChangeListener(this);
       setMonthYearText(calendarView.getCurYear(),calendarView.getCurMonth() -1);
    }


    private void setMonthYearText(int year,int month) {
        acCalendarTvMonthYear.setText(String.format(Locale.CHINA,"%s %d",
                mActivity.getString(Constants.MONTHS[month-1 ]), year));
    }

    private void setSlectTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        showHistory();
    }

    private void showHistory() {
        try {
            if (startTime == -1 || endTime == -1) {
                setStartDate("", "");
                setEndDate("", "");

                return;
            }
            endTime -= 1000 * 60 * 60 * 24;
            Day firstDay = new Day(new Date(startTime));
            Day secondDay = new Day(new Date(endTime));
            startDate = DateUtil.getDateByOtherFormat(startTime);//"/"
            endDate = DateUtil.getDateByOtherFormat(endTime);

            setStartDate(DateUtil.getMonth(startTime) + "." + DateUtil.getDayDate(startTime), DateUtil.getYearDate(startTime));
            boolean onlyOneDay = startTime == endTime;
            if (onlyOneDay) {
                setEndDate("", "");
            } else {
                setEndDate(DateUtil.getMonth(endTime) + "." + DateUtil.getDayDate(endTime), DateUtil.getYearDate(endTime));
            }


//            if (calendarView.getSelectionManager() instanceof RangeSelectionManager) {
//                RangeSelectionManager rangeSelectionManager =
//                        (RangeSelectionManager) calendarView.getSelectionManager();
//                if (onlyOneDay) {
//                    rangeSelectionManager.toggleDay(firstDay);
//                } else {
//                    rangeSelectionManager.toggleDay(firstDay);
//                    rangeSelectionManager.toggleDay(secondDay);
//                }
//                calendarView.update();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void show(final View viewLocation, long temp_startTime, long temp_endTime) {
        if (mPopupWindow == null) {
            init();
        }
        mPopupWindow.showAtLocation(viewLocation, Gravity.TOP, 0, 0);
        setSlectTime(temp_startTime, temp_endTime);

    }

    @OnClick({R.id.ac_calendar_tv_cancel, R.id.ac_calendar_tv_save,R.id.ac_calendar_view_dismiss,R.id.ac_calendar_imv_arrow_left,R.id.ac_calendar_imv_arrow_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_calendar_tv_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.ac_calendar_tv_save:
                saveDate();
                break;
            case R.id.ac_calendar_view_dismiss:
                mPopupWindow.dismiss();
                break;
            case R.id.ac_calendar_imv_arrow_left:
                calendarView.scrollToPre();
                break;
            case R.id.ac_calendar_imv_arrow_right:
                calendarView.scrollToNext();
                break;
        }
    }

    private void saveDate() {
        if (isMultiple) {
            if (startDate != null && endDate != null) {
                CalendarDateModel calendarDateModel = new CalendarDateModel();
                calendarDateModel.startDate = startDate;
                calendarDateModel.endDate = endDate;
                listener.onCalendarPopupCallback(calendarDateModel);
                mPopupWindow.dismiss();
            }
        } else {
            if (startDate != null) {
                CalendarDateModel calendarDateModel = new CalendarDateModel();
                calendarDateModel.startDate = startDate;
                calendarDateModel.endDate = startDate;
                listener.onCalendarPopupCallback(calendarDateModel);
                mPopupWindow.dismiss();
            } else {
                SensoroToast.INSTANCE.makeText(mActivity.getString(R.string.tips_date_not_null), Toast.LENGTH_SHORT).show();
            }
        }


    }

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
            setStartDate(firstMonth + "." + calendarFirst.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) + "");
            setEndDate(endMonth + "." + calendarEnd.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) + "");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setEndDate(String monthDay, String year) {
        acCalendarTvEndMonth.setText(monthDay);
        acCalendarTvEndYear.setText(year);
    }

    private void setStartDate(String monthDay, String year) {
        acCalendarTvStartMonth.setText(monthDay);
        acCalendarTvStartYear.setText(year);

    }

//    @Override
//    public void onClick(List<Day> dayList) {
//        isMultiple = false;
//        Day day = dayList.get(0);
//        try {
//            Calendar calendarFirst = day.getCalendar();
//            int firstMonth = calendarFirst.get(Calendar.MONTH) + 1;
//            startDate = calendarFirst.get(Calendar.YEAR) + "/" + firstMonth + "/" + calendarFirst.get
//                    (Calendar.DAY_OF_MONTH);
//            String temp_startDate = calendarFirst.get(Calendar.YEAR) + "-" + firstMonth + "-" + calendarFirst
//                    .get(Calendar.DAY_OF_MONTH);
//            setStartDate(firstMonth + "." + calendarFirst.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) + "");
//            setEndDate("", "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public void onDismiss() {
        if (calendarView != null) {
//            calendarView.clearSelections();
        }

    }

    public void setOnCalendarPopupCallbackListener(OnCalendarPopupCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCalendarSelectOutOfRange(com.sensoro.smartcity.calendarview.Calendar calendar) {

    }

    @Override
    public void onSelectOutOfRange(com.sensoro.smartcity.calendarview.Calendar calendar, boolean isOutOfMinRange) {

    }

    @Override
    public void onCalendarRangeSelect(com.sensoro.smartcity.calendarview.Calendar calendar, boolean isEnd) {

    }

    @Override
    public void onMonthChange(int year, int month) {
        setMonthYearText(year,month);
    }


    public interface OnCalendarPopupCallbackListener {
        void onCalendarPopupCallback(CalendarDateModel calendarDateModel);
    }
}
