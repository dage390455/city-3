package com.sensoro.smartcity.util;

import android.content.res.Resources;
import androidx.annotation.NonNull;

import com.sensoro.common.base.ContextUtils;
import com.sensoro.smartcity.R;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 动态时间格式化
 * Created by SCWANG on 2017/6/17.
 */

public class DynamicTimeFormat extends SimpleDateFormat {

    private static Locale locale = Locale.CHINA;
    private static String weeks[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private static String moments[] = {"中午", "上午", "上午", "下午", "下午"};

    private String mFormat = "%s";

    public DynamicTimeFormat() {
        this("%s", "yyyy/", "M/d/", "HH:mm");
    }

    public DynamicTimeFormat(String format) {
        this();
        this.mFormat = format;
    }

    public DynamicTimeFormat(String yearFormat, String dateFormat, String timeFormat) {
        super(String.format(locale, "%s %s %s", yearFormat, dateFormat, timeFormat), locale);
        Resources resources = ContextUtils.getContext().getResources();
        weeks = new String[]{resources.getString(R.string.sunday), resources.getString(R.string.monday), resources.getString(R.string.tuesday), resources.getString(R.string.wednesday), resources.getString(R.string.thursday), resources.getString(R.string.friday), resources.getString(R.string.saturday)};
        moments = new String[]{resources.getString(R.string.noon), resources.getString(R.string.early_morning), resources.getString(R.string.morning), resources.getString(R.string.pm), resources.getString(R.string.night)};
    }

    public DynamicTimeFormat(String format, String yearFormat, String dateFormat, String timeFormat) {
        this(yearFormat, dateFormat, timeFormat);
        this.mFormat = format;
    }

    @Override
    public StringBuffer format(@NonNull Date date, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
        toAppendTo = super.format(date, toAppendTo, pos);

        Calendar otherCalendar = calendar;
        Calendar todayCalendar = Calendar.getInstance();

        int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);

        String[] times = toAppendTo.toString().split(" ");
        String moment = hour == 12 ? moments[0] : moments[hour / 6 + 1];
        String timeFormat = moment + " " + times[2];
        String dateFormat = times[1] + " " + timeFormat;
        String yearFormat = times[0] + dateFormat;
        toAppendTo.delete(0, toAppendTo.length());

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        toAppendTo.append(timeFormat);
                        break;
                    case 1:
                        toAppendTo.append(ContextUtils.getContext().getResources().getString(R.string.yesterday));
                        toAppendTo.append(timeFormat);
                        break;
                    case 2:
                        toAppendTo.append(ContextUtils.getContext().getResources().getString(R.string.before_yesterday));
                        toAppendTo.append(timeFormat);
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if (dayOfMonth == todayOfMonth) {//表示是同一周
                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek != 1) {//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                toAppendTo.append(weeks[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1]);
                                toAppendTo.append(' ');
                                toAppendTo.append(timeFormat);
                            } else {
                                toAppendTo.append(dateFormat);
                            }
                        } else {
                            toAppendTo.append(dateFormat);
                        }
                        break;
                    default:
                        toAppendTo.append(dateFormat);
                        break;
                }
            } else {
                toAppendTo.append(dateFormat);
            }
        } else {
            toAppendTo.append(yearFormat);
        }

        int length = toAppendTo.length();
        toAppendTo.append(String.format(locale, mFormat, toAppendTo.toString()));
        toAppendTo.delete(0, length);
        return toAppendTo;
    }

}
