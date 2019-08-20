package com.sensoro.common.widgets;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.sensoro.common.R;
import com.sensoro.common.R2;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.CalendarDateModel;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.calendarview.CalendarView;
import com.sensoro.smartcity.calendarview.customview.CustomCircleRangeMonthView;
import com.sensoro.smartcity.calendarview.customview.CustomRangeMonthView;


import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CalendarPopUtils implements
        CalendarView.OnCalendarRangeSelectListener, Constants, PopupWindow.OnDismissListener, CalendarView.OnMonthChangeListener {

    @BindView(R2.id.ac_calendar_ll_start_month)
    LinearLayout acCalendarLlStartMonth;
    @BindView(R2.id.ac_calendar_ll_end_month)
    LinearLayout acCalendarLlEndMonth;
    @BindView(R2.id.calendar_view_ll)
    LinearLayout calendarViewLl;
    private PopupWindow mPopupWindow = null;
    private final Activity mActivity;
    @BindView(R2.id.ac_calendar_imv_arrows)
    ImageView acCalendarImvArrows;
    @BindView(R2.id.ac_calendar_tv_start_month)
    TextView acCalendarTvStartMonth;
    @BindView(R2.id.ac_calendar_tv_start_year)
    TextView acCalendarTvStartYear;
    @BindView(R2.id.ac_calendar_tv_end_month)
    TextView acCalendarTvEndMonth;
    @BindView(R2.id.ac_calendar_tv_end_year)
    TextView acCalendarTvEndYear;
    @BindView(R2.id.calendar_view)
    CalendarView calendarView;
    @BindView(R2.id.ac_calendar_tv_cancel)
    TextView acCalendarTvCancel;
    @BindView(R2.id.ac_calendar_tv_save)
    TextView acCalendarTvSave;
    @BindView(R2.id.calendar_btn_layout)
    LinearLayout calendarBtnLayout;
    @BindView(R2.id.sensor_calendar_date_layout)
    RelativeLayout sensorCalendarDateLayout;
    @BindView(R2.id.ac_calendar_view_dismiss)
    View dismissiView;
    @BindView(R2.id.ac_calendar_imv_arrow_left)
    ImageView acCalendarImvArrowLeft;
    @BindView(R2.id.ac_calendar_tv_month_year)
    TextView acCalendarTvMonthYear;
    @BindView(R2.id.ac_calendar_imv_arrow_right)
    ImageView acCalendarImvArrowRight;

    private boolean isMultiple;
    private String startDate;
    private String endDate;
    private long startTime;
    private long endTime;
    private OnCalendarPopupCallbackListener listener;
    private View view;
    private TranslateAnimation showTranslateAnimation;
    private TranslateAnimation dismissTranslateAnimation;
    private AlphaAnimation showAlphaAnimation;
    private AlphaAnimation dismissAlphaAnimation;
    private int monthStatus;
    private boolean isDefaultSelectedCurDay = true;
    private int rangeStatus;

    public CalendarPopUtils(Activity activity) {
        mActivity = activity;
    }

    /**
     * 指定日期当天有个小绿点
     * @param monthStatus
     * @return
     */
    public CalendarPopUtils setMonthStatus(int monthStatus) {
       this.monthStatus = monthStatus;
       return this;
    }
    public CalendarPopUtils setRangeStatus(int rangeStatus) {
        this.rangeStatus = rangeStatus;
        return this;
    }

    public CalendarPopUtils isDefaultSelectedCurDay(boolean isDefaultSelectedCurDay) {
        this.isDefaultSelectedCurDay = isDefaultSelectedCurDay;
        return this;
    }
    private void init() {
        mPopupWindow = new PopupWindow(mActivity);
        view = LayoutInflater.from(mActivity).inflate(R2.layout.activity_calendar_test, null);
        ButterKnife.bind(this, view);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_aa000000)));
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
//        mPopupWindow.setAnimationStyle(R2.style.DialogFragmentDropDownAnim);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(R.color.c_B3000000)));
        mPopupWindow.setAnimationStyle(R2.style.DialogFragmentDropDownAnim);
        initView();
        mPopupWindow.setContentView(view);


    }

    private void initView() {
        switch (monthStatus){
            case 1:
                calendarView.setMonthView(CustomCircleRangeMonthView.class);
                break;
            default:
                calendarView.setMonthView(CustomRangeMonthView.class);
                break;

        }

       switch (rangeStatus){
           case 1:
               calendarView.setRange(2004,1,1,calendarView.getCurYear(),calendarView.getCurMonth(),calendarView.getCurDay());
               break;
            default:
                break;

        }
        calendarView.setOnCalendarRangeSelectListener(this);
        calendarView.setOnMonthChangeListener(this);

        setMonthYearText(calendarView.getCurMonth());
        initAnimation();
    }

    /**
     * 隐藏日历
     */
    public  void  dismissNoAnimation(){
        if(mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }
    private void initAnimation() {
        showTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0);
        dismissTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1);
        showAlphaAnimation = new AlphaAnimation(0f, 0.33f);
        dismissAlphaAnimation = new AlphaAnimation(0.33f, 0f);

        showTranslateAnimation.setDuration(300);
        dismissTranslateAnimation.setDuration(300);
        showAlphaAnimation.setDuration(300);
        dismissAlphaAnimation.setDuration(300);
        dismissTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPopupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void setMonthYearText(int month) {
        acCalendarTvMonthYear.setText(mActivity.getString(MONTHS[month - 1]));
    }

    /**
     * 根据年月显示一下月Arrawbtn状态
     * @param year
     * @param month
     */
    private void setNextBtnState(int year,int  month){
        if(year==calendarView.getCurYear()&& month==calendarView.getCurMonth()){
            acCalendarImvArrowRight.setEnabled(false);
            acCalendarImvArrowRight.setImageResource(R.drawable.calendar_arrow_right_disabled);

        }else{
            acCalendarImvArrowRight.setEnabled(true);
            acCalendarImvArrowRight.setImageResource(R.drawable.calendar_arrow_right);
        }
    }

    private void setSelectTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        showHistory();
    }

    private void showHistory() {
        try {
            if (startTime == -1 || endTime == -1) {
                if(isDefaultSelectedCurDay){
                    calendarView.setSelectedCalendar(calendarView.getCurYear(),calendarView.getCurMonth(),calendarView.getCurDay(),true);
                    if (AppUtils.isChineseLanguage()) {
                        setStartDate(calendarView.getCurMonth() + "月" + calendarView.getCurDay()+"日", String.valueOf(calendarView.getCurYear()));
                    }else{
                        setStartDate(calendarView.getCurMonth() + "." + calendarView.getCurDay(), String.valueOf(calendarView.getCurYear()));
                    }

                }else{
                    calendarView.clearSelectRange();
                    setStartDate("-","");
                }
                return;
            }

            endTime -= 1000 * 60 * 60 * 24;
//            Day firstDay = new Day(new Date(startTime));
//            Day secondDay = new Day(new Date(endTime));
            startDate = DateUtil.getDateByOtherFormat(startTime);//"/"
            endDate = DateUtil.getDateByOtherFormat(endTime);


//            new Handler(LoopeR2.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Calendar instance = Calendar.getInstance();
//                    instance.setTimeInMillis(startTime);
//                    calendarView.setSelectedCalendar(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH)+1,instance.get(Calendar.DAY_OF_MONTH),true);
//                    if (startTime != endTime) {
//                        instance.setTimeInMillis(endTime);
//                        calendarView.setSelectedCalendar(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH)+1,instance.get(Calendar.DAY_OF_MONTH),false);
//                    }
//                }
//            },300);
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(startTime);
            calendarView.setSelectedCalendar(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH)+1,instance.get(Calendar.DAY_OF_MONTH),true);
            if (startTime != endTime) {
                instance.setTimeInMillis(endTime);
                calendarView.setSelectedCalendar(instance.get(Calendar.YEAR),instance.get(Calendar.MONTH)+1,instance.get(Calendar.DAY_OF_MONTH),false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChineseStartDate(String monthDay, String year) {
        acCalendarLlEndMonth.setVisibility(View.GONE);
        acCalendarImvArrows.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) acCalendarLlStartMonth.getLayoutParams();
        layoutParams.setMarginEnd(AppUtils.dp2px(mActivity,0));
        acCalendarLlStartMonth.setLayoutParams(layoutParams);
        acCalendarTvStartMonth.setText(monthDay);
        acCalendarTvStartYear.setText(year);
    }


    public void show(final View viewLocation, long temp_startTime, long temp_endTime) {
        if (mPopupWindow == null) {
            init();
        }
        mPopupWindow.showAtLocation(viewLocation, Gravity.TOP, 0, 0);
        setSelectTime(temp_startTime, temp_endTime);
        calendarViewLl.startAnimation(showTranslateAnimation);
    }

    public void showFalseClip(final View viewLocation, long temp_startTime, long temp_endTime) {
        if (mPopupWindow == null) {
            init();
        }
        //多了这一行，不能超出屏幕边界，都是白色，所以看不到状态栏的变化
        mPopupWindow.setClippingEnabled(false);
        mPopupWindow.showAtLocation(viewLocation, Gravity.TOP, 0, 0);
        setSelectTime(temp_startTime, temp_endTime);
        calendarViewLl.startAnimation(showTranslateAnimation);
    }


    @OnClick({R2.id.ac_calendar_tv_cancel, R2.id.ac_calendar_tv_save, R2.id.ac_calendar_view_dismiss, R2.id.ac_calendar_imv_arrow_left, R2.id.ac_calendar_imv_arrow_right})
    public void onViewClicked(View view) {
        int id=view.getId();
        if(id==R.id.ac_calendar_tv_cancel){
            calendarViewLl.startAnimation(dismissTranslateAnimation);
        }else  if(id==R.id.ac_calendar_tv_save){
            saveDate();
        }else if(id==R.id.ac_calendar_view_dismiss){
            calendarViewLl.startAnimation(dismissTranslateAnimation);
        }else if(id==R.id.ac_calendar_imv_arrow_left){
            calendarView.scrollToPre();
        }else if(id==R.id.ac_calendar_imv_arrow_right){
            calendarView.scrollToNext();
        }

    }

    private void saveDate() {
        if (startDate != null && endDate != null) {
            CalendarDateModel calendarDateModel = new CalendarDateModel();
            calendarDateModel.startDate = startDate;
            calendarDateModel.endDate = endDate;
            listener.onCalendarPopupCallback(calendarDateModel);
//            mPopupWindow.dismiss();
            calendarViewLl.startAnimation(dismissTranslateAnimation);
        } else {
            SensoroToast.getInstance().makeText(mActivity.getString(R.string.tips_date_not_null), Toast.LENGTH_SHORT).show();
        }


    }

//    @Override
//    public void onDayRangeSelected(Pair<Day, Day> days) {
//        try {
//            isMultiple = true;
//            Calendar calendarFirst = days.first.getCalendar();
//            Calendar calendarEnd = days.second.getCalendar();
//            int firstMonth = calendarFirst.get(Calendar.MONTH) + 1;
//            int endMonth = calendarEnd.get(Calendar.MONTH) + 1;
//            startDate = calendarFirst.get(Calendar.YEAR) + "/" + firstMonth + "/" + calendarFirst.get
//                    (Calendar.DAY_OF_MONTH);
//            endDate = calendarEnd.get(Calendar.YEAR) + "/" + endMonth + "/" + calendarEnd.get(Calendar
//                    .DAY_OF_MONTH);
//            String temp_startDate = calendarFirst.get(Calendar.YEAR) + "-" + firstMonth + "-" + calendarFirst
//                    .get(Calendar.DAY_OF_MONTH);
//            String temp_endDate = calendarEnd.get(Calendar.YEAR) + "-" + endMonth + "-" + calendarEnd.get
//                    (Calendar.DAY_OF_MONTH);
//            setStartDate(firstMonth + "." + calendarFirst.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) + "");
//            setEndDate(endMonth + "." + calendarEnd.get(Calendar.DAY_OF_MONTH), calendarFirst.get(Calendar.YEAR) + "");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void setEndDate(String monthDay, String year) {
        acCalendarLlEndMonth.setVisibility(View.VISIBLE);
        acCalendarImvArrows.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) acCalendarLlStartMonth.getLayoutParams();
        layoutParams.setMarginEnd(AppUtils.dp2px(mActivity,18));
        acCalendarLlStartMonth.setLayoutParams(layoutParams);
        acCalendarTvEndMonth.setText(monthDay);
        acCalendarTvEndYear.setText(year);
    }

    private void setStartDate(String monthDay, String year) {
        acCalendarLlEndMonth.setVisibility(View.GONE);
        acCalendarImvArrows.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) acCalendarLlStartMonth.getLayoutParams();
        layoutParams.setMarginEnd(AppUtils.dp2px(mActivity,0));
        acCalendarLlStartMonth.setLayoutParams(layoutParams);
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

        if (isEnd) {
            endDate = calendar.getYear() + "/" + calendar.getMonth() + "/" + calendar.getDay();
            if (AppUtils.isChineseLanguage()) {
                setEndDate(calendar.getMonth() + "月" + calendar.getDay()+"日", String.valueOf(calendar.getYear()));
            }else{
                setEndDate(calendar.getMonth() + "." + calendar.getDay(), String.valueOf(calendar.getYear()));
            }
        } else {
            startDate = calendar.getYear() + "/" + calendar.getMonth() + "/" + calendar.getDay();
            endDate = startDate;
            if (AppUtils.isChineseLanguage()) {
                setStartDate(calendar.getMonth() + "月" + calendar.getDay()+"日", String.valueOf(calendar.getYear()));
            }else{
                setStartDate(calendar.getMonth() + "." + calendar.getDay(), String.valueOf(calendar.getYear()));
            }

        }
    }

    @Override
    public void onMonthChange(int year, int month) {
        setMonthYearText(month);
       setNextBtnState(year,month);
    }


    public interface OnCalendarPopupCallbackListener {
        void onCalendarPopupCallback(CalendarDateModel calendarDateModel);
    }
}
