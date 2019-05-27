package com.sensoro.common.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sensoro.common.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
/**
 * Created by bruceli on 2015/6/16.
 */
public class DateUtil {
    /*
     * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm
     */
    public static String getStrTime_ymd_hm(long cc_time) {
        String re_StrTime = "";
//        if (TextUtils.isEmpty(cc_time) || "null".equals(cc_time)) {
//            return re_StrTime;
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
//        // 例如：cc_time=1291778220
//        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(cc_time));
        return re_StrTime;

    }

    public static String getStrTime_ymd_hm_ss(long cc_time) {
        String re_StrTime = "";
//        if (TextUtils.isEmpty(cc_time) || "null".equals(cc_time)) {
//            return re_StrTime;
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//        // 例如：cc_time=1291778220
//        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(cc_time));
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
    public static String getStrTime_ymd(long cc_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        // 例如：cc_time=1291778220
        return sdf.format(new Date(cc_time));
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

    public static String getStrTime_hms(long cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // 例如：cc_time=1291778220
        re_StrTime = sdf.format(new Date(cc_time));
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

//    public static String parseDateToString(String text) {
//        String dateFormat = "yyyy-MM-dd HH:mm:ss";
//        try {
//            if (TextUtils.isEmpty(text)) {
//                return " - ";
//            }
//            text = text.replace("+", " ");
//            return getFullParseDate(new SimpleDateFormat(dateFormat).parse(text).getTime());
//        } catch (ParseException e) {
//            return " - ";
//        }
//    }

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

    public static String getDateByOtherFormatPoint(long time) {
        return new SimpleDateFormat("yyyy.MM.dd", Locale.ROOT).format(new Date(time));
    }

    public static String getFullDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getFullDatePoint(long time) {
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

    public static String getYearDate(long time) {
        return new SimpleDateFormat("yyyy", Locale.ROOT).format(new Date(time));
    }

    public static String getMonth(long time) {
        return new SimpleDateFormat("MM", Locale.ROOT).format(new Date(time));
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

//    public static String getFullParseDate(Context context,long time) {
//        final Calendar mCalendar = Calendar.getInstance();
//        mCalendar.setTimeInMillis(time);
//        int apm = mCalendar.get(Calendar.AM_PM);
//        String apm_text = apm == 0 ? "上午" : "下午";
//        long now = System.currentTimeMillis();
//        long diff = now - time;
//        float day = diff / 3600000 / 24;
//        String formatTime = new SimpleDateFormat("hh:mm:ss", Locale.ROOT).format(new Date(time));
//        String other_date = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.ROOT).format(new Date(time));
//        if (day < 1) {
//            String nowString = DateUtil.getDayDate(now);
//            String dataString = DateUtil.getDayDate(time);
//            if (dataString.equalsIgnoreCase(nowString)) {
//                return "今天 " + apm_text + formatTime;
//            } else {
//                return "昨天 " + apm_text + formatTime;
//            }
//        } else if (day < 2) {
//            return "昨天 " + apm_text + formatTime;
//        } else {
//
//            return other_date.replace(" ", " " + apm_text);
//        }
//    }

    public static String getFullParseDatePoint(Context context, long time) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int apm = mCalendar.get(Calendar.AM_PM);
        String apm_text = apm == 0 ? context.getString(R.string.am) : context.getString(R.string.pm);
        long now = System.currentTimeMillis();
        long diff = now - time;
        float day = diff / 3600000 / 24;
        String formatTime = new SimpleDateFormat("hh:mm:ss", Locale.ROOT).format(new Date(time));
        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return context.getString(R.string.today) + apm_text + formatTime;
            } else {
                return context.getString(R.string.yesterday) + apm_text + formatTime;
            }
//        } else if (day < 2) {
//            return context.getString(R.string.yesterday) + apm_text + formatTime;
//        } else {
//            String other_date = new SimpleDateFormat("MM.dd hh:mm:ss", Locale.ROOT).format(new Date(time));
//            return other_date.replace(" ", " " + apm_text);
//        }
        } else {
            String other_date = new SimpleDateFormat("MM.dd hh:mm:ss", Locale.ROOT).format(new Date(time));
            return other_date.replace(" ", " " + apm_text);
        }
    }

    /**
     * status 0 表示含有年月日， 1表示含有月日
     *
     * @param time
     * @param status
     * @return
     */

    public static String getStrTimeToday(Context context, long time, int status) {
        //TODO 暂时去掉
//        final Calendar mCalendar = Calendar.getInstance();
//        mCalendar.setTimeInMillis(time);
        long now = System.currentTimeMillis();
        long diff = now - time;
        float day = diff / 3600000 / 24;
        String pattern = "yyyy.MM.dd HH:mm:ss";
        String formatPattern = "HH:mm:ss";
        switch (status) {
            case 0:
                pattern = "yyyy.MM.dd HH:mm:ss";
                break;
            case 1:
                pattern = "MM.dd  HH:mm:ss";
                break;

        }
        String formatTime = new SimpleDateFormat(formatPattern, Locale.ROOT).format(new Date(time));
        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return context.getString(R.string.today) + formatTime;
            } else {
                return context.getString(R.string.yesterday) + formatTime;
            }
//        } else if (day < 2) {
//            return context.getString(R.string.yesterday) + formatTime;
//        } else {
//            return new SimpleDateFormat(pattern, Locale.ROOT).format(new Date(time));
//        }
        } else {
            return new SimpleDateFormat(pattern, Locale.ROOT).format(new Date(time));
        }
    }

    public static String getStrTimeTodayByDevice(Context context, long time) {
//        final Calendar mCalendar = Calendar.getInstance();
//        mCalendar.setTimeInMillis(time);
        long now = System.currentTimeMillis();
        long diff = now - time;
        float day = diff / 3600000 / 24;
        String pattern = "MM.dd HH:mm:ss";
        String formatPattern = "HH:mm:ss";
        String formatTime = new SimpleDateFormat(formatPattern, Locale.ROOT).format(new Date(time));
        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return formatTime;
            } else {
                return context.getString(R.string.yesterday) + formatTime;
            }
//        } else if (day < 2) {
//            return context.getString(R.string.yesterday) + formatTime;
//        } else {
//            return new SimpleDateFormat(pattern, Locale.ROOT).format(new Date(time));
//        }
        } else {
            return new SimpleDateFormat(pattern, Locale.ROOT).format(new Date(time));
        }
    }


    public static String getHourFormatDate(long time) {
        return new SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getHourMmFormatDate(long time) {
        return new SimpleDateFormat("HH:mm", Locale.ROOT).format(new Date(time));
    }

    public static String getMothFormatDate(long time) {
        return new SimpleDateFormat("MM/dd HH:mm:ss", Locale.ROOT).format(new Date(time));
    }

    public static String getMothDayHourMinuteFormatDate(long time) {
        return new SimpleDateFormat("MM/dd HH:mm", Locale.ROOT).format(new Date(time));
    }

    public static String getCalendarYearMothDayFormatDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date(time));
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

    public static String secToTimeBefore(Context context, int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second >= 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp >= 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        //
        if (h >= 24) {
            int day = h / 24;
            h = day % 24;
            return day + context.getString(R.string.day) + getStrTime(context, h, d, s);
        } else {
            return getStrTime(context, h, d, s);
        }
    }

    @NonNull
    private static String getStrTime(Context context, int hours, int min, int sec) {
        String second = context.getString(R.string.second);
        String minute = context.getString(R.string.minute);
        String hour = context.getString(R.string.hour);
        if (hours == 0) {
            if (min == 0) {
                if (sec == 0) {
                    return "";
                } else {
                    return sec + second;
                }
            } else {
                if (sec == 0) {
                    return min + minute;
                } else {
                    return min + minute + sec + second;
                }
            }
        } else {
            if (min == 0) {
                if (sec == 0) {
                    return hours + hour;
                } else {
                    return hours + hour + sec + second;
                }

            } else {
                if (sec == 0) {
                    return hours + hour + min + minute;
                } else {
                    return hours + hour + min + minute + sec + second;
                }
            }
        }
    }


    public static String getChineseCalendar(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return String.format(Locale.ROOT, "%d年%02d月%02d日 %02d:%02d", instance.get(Calendar.YEAR), instance.get(Calendar.MONTH) + 1,
                instance.get(Calendar.DAY_OF_MONTH), instance.get(Calendar.HOUR_OF_DAY), instance.get(Calendar.MINUTE));
    }

    public static String getStrTime_yymmdd(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ROOT);
        return simpleDateFormat.format(new Date(time));
    }

    public static String getStrTime_yy(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.ROOT);
        return simpleDateFormat.format(new Date(time));
    }

    public static String getStrTime_MM_dd_hms(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd HH:mm:ss", Locale.ROOT);
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 解析格林尼治时间戳
     * @param time 格式为：2019-05-15T09:09:55.000Z
     */
    public static String parseUTC(String time) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z",Locale.ROOT);
        Date z = dateFormat.parse(time.replace("Z", " UTC"));
        dateFormat.applyPattern("yyyy.MM.dd HH:mm:ss");
        return dateFormat.format(z);


    }


    //获取当天的开始时间
    public static java.util.Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    //获取当天的结束时间
    public static java.util.Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    //获取昨天的开始时间
    public static Date getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    //获取昨天的结束时间
    public static Date getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    //获取明天的开始时间
    public static Date getBeginDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    //获取明天的结束时间
    public static Date getEndDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    //获取本周的开始时间
    @SuppressWarnings("unused")
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    //获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    //获取上周的开始时间
    @SuppressWarnings("unused")
    public static Date getBeginDayOfLastWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek - 7);
        return getDayStartTime(cal.getTime());
    }

    //获取上周的结束时间
    public static Date getEndDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfLastWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    //获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    //获取上月的开始时间
    public static Date getBeginDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取上月的结束时间
    public static Date getEndDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 2, day);
        return getDayEndTime(calendar.getTime());
    }

    //获取本年的开始时间
    public static java.util.Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        // cal.set
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);

        return getDayStartTime(cal.getTime());
    }

    //获取本年的结束时间
    public static java.util.Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    //获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    //获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    //两个日期相减得到的天数
    public static int getDiffDays(Date beginDate, Date endDate) {

        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }

        long diff = (endDate.getTime() - beginDate.getTime())
                / (1000 * 60 * 60 * 24);

        int days = new Long(diff).intValue();

        return days;
    }

    //两个日期相减得到的毫秒数
    public static long dateDiff(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return date2ms - date1ms;
    }

    //获取两个日期中的最大日期
    public static Date max(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return beginDate;
        }
        return endDate;
    }

    //获取两个日期中的最小日期
    public static Date min(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return endDate;
        }
        return beginDate;
    }

    //返回某月该季度的第一个月
    public static Date getFirstSeasonDate(Date date) {
        final int[] SEASON = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int sean = SEASON[cal.get(Calendar.MONTH)];
        cal.set(Calendar.MONTH, sean * 3 - 3);
        return cal.getTime();
    }

    //返回某个日期下几天的日期
    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
        return cal.getTime();
    }

    //返回某个日期前几天的日期
    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        return cal.getTime();
    }

    //获取某年某月到某年某月按天的切片日期集合（间隔天数的集合）
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List getTimeList(int beginYear, int beginMonth, int endYear,
                                   int endMonth, int k) {
        List list = new ArrayList();
        if (beginYear == endYear) {
            for (int j = beginMonth; j <= endMonth; j++) {
                list.add(getTimeList(beginYear, j, k));

            }
        } else {
            {
                for (int j = beginMonth; j < 12; j++) {
                    list.add(getTimeList(beginYear, j, k));
                }

                for (int i = beginYear + 1; i < endYear; i++) {
                    for (int j = 0; j < 12; j++) {
                        list.add(getTimeList(i, j, k));
                    }
                }
                for (int j = 0; j <= endMonth; j++) {
                    list.add(getTimeList(endYear, j, k));
                }
            }
        }
        return list;
    }

    //获取某年某月按天切片日期集合（某个月间隔多少天的日期集合）
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List getTimeList(int beginYear, int beginMonth, int k) {
        List list = new ArrayList();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth, 1);
        int max = begincal.getActualMaximum(Calendar.DATE);
        for (int i = 1; i < max; i = i + k) {
            list.add(begincal.getTime());
            begincal.add(Calendar.DATE, k);
        }
        begincal = new GregorianCalendar(beginYear, beginMonth, max);
        list.add(begincal.getTime());
        return list;
    }
}
