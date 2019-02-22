package com.sensoro.smartcity.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sensoro.smartcity.R;

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

    public static String getFullParseDatePoint(Context context,long time) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int apm = mCalendar.get(Calendar.AM_PM);
        String apm_text = apm == 0 ? context.getString(R.string.am) : context.getString(R.string.pm);
        long now = System.currentTimeMillis();
        long diff = now - time;
        float day = diff / 3600000 / 24;
        String formatTime = new SimpleDateFormat("hh:mm:ss", Locale.ROOT).format(new Date(time));
        String other_date = new SimpleDateFormat("MM.dd hh:mm:ss", Locale.ROOT).format(new Date(time));
        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return context.getString(R.string.today) + apm_text + formatTime;
            } else {
                return context.getString(R.string.yesterday)+ apm_text + formatTime;
            }
        } else if (day < 2) {
            return context.getString(R.string.yesterday)+ apm_text + formatTime;
        } else {

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
    public static String getStrTimeToday(Context context,long time, int status) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
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
        String other_date = new SimpleDateFormat(pattern, Locale.ROOT).format(new Date(time));

        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return context.getString(R.string.today) + formatTime;
            } else {
                return context.getString(R.string.yesterday) + formatTime;
            }
        } else if (day < 2) {
            return context.getString(R.string.yesterday) + formatTime;
        } else {
            return other_date;
        }
    }

    public static String getStrTimeTodayByDevice(Context context,long time) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        long now = System.currentTimeMillis();
        long diff = now - time;
        float day = diff / 3600000 / 24;
        String pattern = "MM.dd HH:mm:ss";
        String formatPattern = "HH:mm:ss";
        String formatTime = new SimpleDateFormat(formatPattern, Locale.ROOT).format(new Date(time));
        String other_date = new SimpleDateFormat(pattern, Locale.ROOT).format(new Date(time));

        if (day < 1) {
            String nowString = DateUtil.getDayDate(now);
            String dataString = DateUtil.getDayDate(time);
            if (dataString.equalsIgnoreCase(nowString)) {
                return formatTime;
            } else {
                return context.getString(R.string.yesterday) + formatTime;
            }
        } else if (day < 2) {
            return context.getString(R.string.yesterday) + formatTime;
        } else {
            return other_date;
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

    public static String getMothDayFormatDate(long time) {
        return new SimpleDateFormat("MM/dd", Locale.ROOT).format(new Date(time));
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

    public static String secToTimeBefore(Context context,int second) {
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
            return day + context.getString(R.string.day) + getStrTime(context,h, d, s);
        } else {
            return getStrTime(context,h, d, s);
        }
    }

    @NonNull
    private static String getStrTime(Context context,int hours, int min, int sec) {
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
        return String.format(Locale.ROOT,"%d年%02d月%02d日 %02d:%02d",instance.get(Calendar.YEAR),instance.get(Calendar.MONTH)+1,
                instance.get(Calendar.DAY_OF_MONTH),instance.get(Calendar.HOUR_OF_DAY),instance.get(Calendar.MINUTE));
    }

    public static String getStrTime_yymmdd(long time) {
       SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyyMMdd",Locale.ROOT);
       return simpleDateFormat.format(new Date(time));
    }

    public static String getStrTime_yy(long time) {
        SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy",Locale.ROOT);
        return simpleDateFormat.format(new Date(time));
    }
}
