package com.eric.util.date;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:日期工具类
 * author: Eric
 * Date: 18/8/6
 */
public class DateUtils {

    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_SHORT_STRING = "yyyy-MM-dd";
    private static final String DATE_FORMAT_MIN_STRING = "yyyyMMdd";
    private static final String DATE_FORMAT_MONTH_STRING = "yyyy-MM";
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
    private static ThreadLocal<DateFormat> threadLocalShort = new ThreadLocal<>();
    private static ThreadLocal<DateFormat> threadLocalMin = new ThreadLocal<>();
    private static ThreadLocal<DateFormat> threadLocalMonth = new ThreadLocal<>();

    private static DateFormat getDateFormat() {
        DateFormat df = threadLocal.get();
        if (df == null) {
            //清除原数据 防止内存泄露
            threadLocal.remove();
            df = new SimpleDateFormat(DATE_FORMAT_STRING);
            threadLocal.set(df);
        }
        return df;
    }

    private static DateFormat getShortDateFormat() {
        DateFormat df = threadLocalShort.get();
        if (df == null) {
            threadLocalShort.remove();
            df = new SimpleDateFormat(DATE_FORMAT_SHORT_STRING);
            threadLocalShort.set(df);
        }
        return df;
    }

    private static DateFormat getMinDateFormat() {
        DateFormat df = threadLocalMin.get();
        if (df == null) {
            threadLocalMin.remove();
            df = new SimpleDateFormat(DATE_FORMAT_MIN_STRING);
            threadLocalMin.set(df);
        }
        return df;
    }

    private static DateFormat getMonthDateFormat() {
        DateFormat df = threadLocalMonth.get();
        if (df == null) {
            threadLocalMonth.remove();
            df = new SimpleDateFormat(DATE_FORMAT_MONTH_STRING);
            threadLocalMonth.set(df);
        }
        return df;
    }

    /**
     * Date转String
     * 日期样式
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return getDateFormat().format(date);
    }

    /**
     * String转Date
     * 字符串样式
     * yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date parse(String strDate) {
        try {
            return getDateFormat().parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Date转String
     * 日期样式
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String shortFormat(Date date) {
        return getShortDateFormat().format(date);
    }

    /**
     * String转Date
     * 字符串样式
     * yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date shortParse(String strDate) {
        try {
            return getShortDateFormat().parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Date转String
     * 日期样式
     * yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String minFormat(Date date) {
        return getMinDateFormat().format(date);
    }


    /**
     * Date转month格式
     * 日期样式
     * yyyy-MM
     *
     * @param date
     * @return
     */
    public static String monthFormat(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_MONTH_STRING);
        return df.format(date);
    }


    /**
     * String转Date
     * 字符串样式
     * yyyyMMdd
     *
     * @param strDate
     * @return
     */
    public static Date minParse(String strDate) {
        try {
            return getMinDateFormat().parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取两个日期之间的所有日期
     *
     * @param startTime 开始日期    yyyy-MM-dd
     * @param endTime   结束日期    yyyy-MM-dd
     * @return
     */
    public static List<String> getDays(String startTime, String endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<>();

        DateFormat dateFormat = getShortDateFormat();
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            // 日期加1(包含结束)
            tempEnd.add(Calendar.DATE, +1);
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }


    /**
     * 获取两个日期之间的所有日期
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return
     */
    public static List<String> getDays(Date startTime, Date endTime) {

        // 返回的日期集合
        List<String> days = new ArrayList<>();

        DateFormat dateFormat = getShortDateFormat();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startTime);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(endTime);
        // 日期加1(包含结束)
        tempEnd.add(Calendar.DATE, +1);
        while (tempStart.before(tempEnd)) {
            days.add(dateFormat.format(tempStart.getTime()));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }

        return days;
    }

    /**
     * 获取两个日期之间的所有周末日期，包含日期边界所在周
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return
     */
    public static List<String> getWeekDays(Date startTime, Date endTime) {
        List<String> dayList = new ArrayList<>();
        // 返回的日期集合
        Set<String> daySet = new HashSet<>();

        DateFormat dateFormat = getShortDateFormat();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startTime);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(endTime);
        // 日期加1(包含结束)
        tempEnd.add(Calendar.DATE, +1);
        while (tempStart.before(tempEnd)) {
            // 周末日期
            if (tempStart.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                daySet.add(dateFormat.format(tempStart.getTime()));
            }
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        daySet.add(getDateOfWeekLastDay(endTime));
        dayList.addAll(daySet);
        return dayList;
    }

    /**
     * 获取两个日期之间的所有月份
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return
     */
    public static List<String> getMonths(Date startTime, Date endTime) {
        // 返回的日期集合
        List<String> months = new ArrayList<>();
        Set<String> monthSet = new HashSet<>();
        DateFormat dateFormat = getMonthDateFormat();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startTime);
        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(endTime);
        while (tempStart.before(tempEnd)) {
            monthSet.add(dateFormat.format(tempStart.getTime()));
            tempStart.add(Calendar.MONTH, 1);
        }
        monthSet.add(dateFormat.format(tempEnd.getTime()));
        months.addAll(monthSet);
        return months;
    }

    /**
     * 获取指定日期所在周的周一日期和周末日期
     *
     * @param date
     * @return
     */
    public static String getDateOfWeekFirstDayAndLastDay(Date date) {
        DateFormat sdf = getMinDateFormat();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        //设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        StringBuilder dateSb = new StringBuilder();
        dateSb.append(sdf.format(cal.getTime())).append("_");
        cal.add(Calendar.DATE, 6);
        dateSb.append(sdf.format(cal.getTime()));
        return dateSb.toString();
    }

    /**
     * 获取当前日期所在周的周末日期
     *
     * @param date
     * @return
     */
    public static String getDateOfWeekLastDay(Date date) {
        DateFormat sdf = getShortDateFormat();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        //设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        cal.add(Calendar.DATE, 6);
        return sdf.format(cal.getTime());
    }

    /**
     * 获取当前时间的前一个月的开始时间
     *
     * @return
     */
    public static Date getPreMonthFirstDay(Date monthDate) {
        DateTime dateTime = new DateTime(monthDate);
        dateTime = dateTime.monthOfYear().setCopy(-1).dayOfMonth().setCopy(1).withTime(0, 0, 0, 0);
        return dateTime.toDate();
    }


    /**
     * 获取指定日期所在月份开始时间
     *
     * @param monthDate 日期
     * @return 返回指定日期当月第一天的0点
     */
    public static Date getMonthFirstTime(Date monthDate) {
        DateTime dateTime = new DateTime(monthDate);
        dateTime = dateTime.dayOfMonth().setCopy(1).withTime(0, 0, 0, 0);
        return dateTime.toDate();
    }

    /**
     * 获取指定日期所在月份结束时间
     *
     * @param monthDate 日期
     * @return 返回指定日期当月最后一天的日期
     */
    public static Date getMonthLastTime(Date monthDate) {
        DateTime dateTime = new DateTime(monthDate);
        //获取月份天数
        int monthDay = dateTime.dayOfMonth().getMaximumValue();
        dateTime = dateTime.dayOfMonth().setCopy(monthDay).withTime(23, 59, 59, 0);
        return dateTime.toDate();
    }

    /**
     * 获取指定日期前后浮动指定天数之后日期的最小时间 既0点
     *
     * @param originalDate 原日期
     * @param assignDay    浮动天数 可正可负
     * @return
     */
    public static Date getAssignDateMinTime(Date originalDate, int assignDay) {
        DateTime dateTime = new DateTime(originalDate);
        dateTime = dateTime.dayOfYear().addToCopy(assignDay).withTime(0, 0, 0, 0);
        return dateTime.toDate();
    }

    /**
     * 获取指定日期前后浮动指定天数之后日期的最大时间 既23:59:59
     *
     * @param originalDate 原日期
     * @param assignDay    浮动天数 可正可负
     * @return
     */
    public static Date getAssignDateMaxTime(Date originalDate, int assignDay) {
        DateTime dateTime = new DateTime(originalDate);
        dateTime = dateTime.dayOfYear().addToCopy(assignDay).withTime(23, 59, 59, 59);
        return dateTime.toDate();
    }

    /**
     * 根据日期字符串获取日期对象
     *
     * @param dateStr 日期字符串
     * @param format  日期格式
     * @return 日期字符串获取的日期对象
     * @throws ParseException 日期转换异常
     */
    public static Date getDateByString(String dateStr, String format) throws ParseException {
        if ((dateStr == null) || dateStr.trim().isEmpty()) {
            return null;
        }
        return new SimpleDateFormat(format).parse(dateStr);
    }

    /**
     * 根据日期对象获取指定格式字符串对象
     *
     * @param date   日期对象
     * @param format 日期格式
     * @return
     * @throws ParseException
     */
    public static String getDateString(Date date, String format) throws ParseException {
        if (date == null || format == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }
}
