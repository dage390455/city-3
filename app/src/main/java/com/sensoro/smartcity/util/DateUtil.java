package com.sensoro.smartcity.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by bruceli on 2015/6/16.
 */
public class DateUtil {
    /*
     * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm
     */
    public static String getStrTime_ymd_hm(String cc_time) {
        String re_StrTime = "";
        if (TextUtils.isEmpty(cc_time) || "null".equals(cc_time)) {
            return re_StrTime;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;

    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getStrTime_ymd_hms(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;

    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd
     */
    public static String getStrTime_ymd(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy
     */
    public static String getStrTime_y(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：MM-dd
     */
    public static String getStrTime_md(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：HH:mm
     */
    public static String getStrTime_hm(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：HH:mm:ss
     */
    public static String getStrTime_hms(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：MM-dd HH:mm:ss
     */
    public static String getNewsDetailsDate(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    /*
     * 将字符串转为时间戳
     */
    public static String getTime() {
        String re_time = null;
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date d;
        d = new Date(currentTime);
        long l = d.getTime();
        String str = String.valueOf(l);
        re_time = str.substring(0, 10);
        return re_time;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd  星期几
     */
    public static String getSection(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  EEEE");
        // 对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
        // yyyy代表年份，如“2010”；dd代表天，如“25”
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));
        return re_StrTime;
    }

    //yyyy-MM-dd
    public static String getTodayDate(String f) {
        return new SimpleDateFormat(f, Locale.ROOT).format(new Date());
    }

    public static Date parseDateTime(String text, String... format) {
        String dateFormat = (format.length == 0 || format[0] == null) ? "yyyy-MM-dd HH:mm:ss" : format[0];
        try {
            if (text == null) return null;
            return new SimpleDateFormat(dateFormat).parse(text);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String parseDateToString(String text) {
        String dateFormat = "yyyy-MM-dd+HH:mm:ss";
        try {
            if (text == null) return " - ";
            return getFullParseDate(new SimpleDateFormat(dateFormat).parse(text).getTime());
        } catch (ParseException e) {
            return " - ";
        }
    }

    public static String parseDateToString(String text, String... format) {
        String dateFormat = (format.length == 0 || format[0] == null) ? "yyyy-MM-dd+HH:mm:ss" : format[0];
        try {
            if (text == null) return null;
            return getFullDate(new SimpleDateFormat(dateFormat).parse(text).getTime());
        } catch (ParseException e) {
            return null;
        }
    }



    public static String getDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date(time));
    }

    public static String getDateByOtherFormat(long time) {
        return new SimpleDateFormat("yyyy/MM/dd", Locale.ROOT).format(new Date(time));
    }

    public static String getFullDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getFullHourDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH", Locale.ROOT).format(new Date(time));
    }

    public static String getFullMonthDate(long time) {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getDayDate(long time) {
        return new SimpleDateFormat("dd", Locale.ROOT).format(new Date(time));
    }

    public static String getMonthDate(long time) {
        return new SimpleDateFormat("MM/dd", Locale.ROOT).format(new Date(time));
    }

    public static String getYearMonthDate(long time) {
        return new SimpleDateFormat("yyyy/MM", Locale.ROOT).format(new Date(time));
    }

    public static List<String> getBetweenDates(long startTIme, long endTime) {
        Long oneDay = 1000 * 60 * 60 * 24L;
        Long time = startTIme;
        List<String> list = new ArrayList<>();
        while (time <= endTime) {
            Date d = new Date(time);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println(df.format(d));
            list.add(df.format(d));
            time += oneDay;
        }
        return list;
    }

    public static String getFullParseDate(long time) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int apm = mCalendar.get(Calendar.AM_PM);
        String apm_text = apm == 0 ? "上午" : "下午";
        long now = System.currentTimeMillis();
        long diff = now - time;
        long day = diff / 3600000 / 24;
        String formatTime = new SimpleDateFormat("hh:mm:ss", Locale.ROOT).format(new Date(time));
        String other_date = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.ROOT).format(new Date(time));
        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return "今天 " + apm_text + formatTime;
            } else {
                return "昨天 " + apm_text + formatTime;
            }
        } else if (day > 1 && day < 2) {
            return "昨天 " + apm_text + formatTime;
        } else {

            return other_date.replace(" ", " " + apm_text);
        }
    }


    public static String getHourFormatDate(long time) {
        return new SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getMothFormatDate(long time) {
        return new SimpleDateFormat("MM/dd HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getMothDayFormatDate(long time) {
        return new SimpleDateFormat("MM/dd", Locale.ROOT).format(new Date(time));
    }

    public static int dayForWeek(String pTime) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date tmpDate = format.parse(pTime);

        Calendar now = Calendar.getInstance();
        now.setTime(tmpDate);
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
        if (isFirstSunday) {
            weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        return weekDay;
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date yearStringToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
}
