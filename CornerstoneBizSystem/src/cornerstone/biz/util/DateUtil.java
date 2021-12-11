package cornerstone.biz.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author cs
 */
public class DateUtil {
    //
    private static Logger logger = LoggerFactory.get(DateUtil.class);

    //
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static Date parseDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @param date
     * @return
     */
    public static Date parse(String date) {
        return parse(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static Date parse(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }

    /**
     * 格式化从Excel里的时间 格式可能yyyy-MM-dd或者yyyyMMdd等
     *
     * @param date
     * @return
     */
    public static Date parseFromExcel(String date) {
        String[] fomats = new String[]{"yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd"};
        for (String fomat : fomats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fomat);
                return sdf.parse(date);
            } catch (Exception e) {
                logger.info(e.getMessage(), e);
            }
        }
        return null;
    }

    public static Date parseDateTimeFromExcel(String date) {
        String[] fomats = new String[]{
                "yyyy-MM-dd HH:mm:ss", "yyyyMMdd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
                "yyyy-MM-dd HH:mm", "yyyyMMdd HH:mm", "yyyy/MM/dd HH:mm",
                "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd"};
        for (String fomat : fomats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fomat);
                return sdf.parse(date);
            } catch (Exception e) {
                logger.info(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 格式化UTC时间
     *
     * @param date 2018-10-26T07:01:26Z
     * @return 2018-10-26 15:01:26
     */
    public static Date parseUTCDate(String date) {
        if (null == date || date.isEmpty()) {
            return null;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(date);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException("时间格式错误");
        }
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(format).format(date);
    }

    //

    /**
     * 通过时间秒毫秒数判断两个时间的间隔(单位：天)
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int differentDays(Date startDate, Date endDate) {
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static int differentDaysByDay(Date startDate,Date endDate){
        LocalDate b = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate l = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (int) ChronoUnit.DAYS.between(b,l);
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public static int leftDays(Date startDate, Date endDate) {
        int leftDay = differentDays(getBeginOfDay(startDate), getBeginOfDay(endDate));
        if (leftDay < 0) {
            leftDay = 0;
        }
        return leftDay;
    }

    /**
     * 获取距离今天 num天的日期
     *
     * @param num
     * @return
     */
    public static Date getNextDay(int num) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, num);
        return now.getTime();
    }

    /**
     * 获取距离date num天的日期
     *
     * @param date
     * @param num
     * @return
     */
    public static Date getNextDay(Date date, int num) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.DAY_OF_YEAR, num);
        return now.getTime();
    }

    /**
     * @param num
     * @return
     */
    public static Date addDay(Date date, int num) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.DAY_OF_YEAR, num);
        return now.getTime();
    }

    /**
     * @param minute
     * @return
     */
    public static Date addMinute(int minute) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minute);
        return now.getTime();
    }

    /**
     * @param date
     * @param minute
     * @return
     */
    public static Date addMinute(Date date, int minute) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MINUTE, minute);
        return now.getTime();
    }

    /**
     * @param date
     * @param hour
     * @return
     */
    public static Date addHour(Date date, int hour) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.HOUR_OF_DAY, hour);
        return now.getTime();
    }

    /**
     * 获取距离date的0店
     *
     * @param date
     * @return
     */
    public static Date getBeginOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * 是否是同一天
     *
     * @param dateA
     * @param dateB
     * @return
     */
    public static boolean isSameDay(Date dateA, Date dateB) {
        if (dateA == null || dateB == null) {
            return false;
        }
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);
        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取Date所在月第一天0点
     *
     * @param date
     * @return
     */
    public static Date getBeginOfMonth(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * 获取本月第一天0点
     *
     * @return
     */
    public static Date getBeginOfMonth() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    public static Date getBeginOfMonth(int addMonth) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, addMonth);
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * @return
     */
    public static Date getBeginOfYear() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, 0);
        now.set(Calendar.DAY_OF_YEAR, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * 获取本周一0点
     *
     * @return
     */
    public static Date getBeginOfWeek() {
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {// 周日
            now.add(Calendar.DAY_OF_YEAR, -6);
        } else {
            now.add(Calendar.DAY_OF_YEAR, -dayOfWeek + 2);
        }
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * @return
     */
    public static int getYear() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
    }

    /**
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取今天是哪一个月(1-12)
     *
     * @return
     */
    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取月份第一天0点
     *
     * @param year
     * @param month
     * @return
     */
    public static Date getBeginOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2018);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取今天哪一个月(1-12)
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * @param addMonth
     * @return
     */
    public static Date getNextMonth(Date date, int addMonth) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MONTH, addMonth);
        return now.getTime();
    }

    public static Date getNextYear(Date date, int addYear) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.YEAR, addYear);
        return now.getTime();
    }

    // 获取今日0点
    public static Date getToday() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    // 获取某一年的最大周数
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        return c.getWeeksInWeekYear();
    }

    /**
     * 返回1-7 （周一到周日）
     *
     * @param date
     * @return
     */
    public static int getWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 0) {
            week = 7;
        }
        return week;
    }

    /**
     * 获取这周几
     *
     * @param week 几
     * @return
     */
    public static Date getDateOfThisWeek(int week) {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + week);
        return c.getTime();
    }

    /**
     * @return
     */
    public static Date getMondayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 1);
        return c.getTime();
    }

    //
    //
    public static Date getNextHour(int num) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, num);
        return now.getTime();
    }

    //
    public static Date getNextMinute(int num) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, num);
        return now.getTime();
    }

    public static Date getNextSecond(int num) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, num);
        return now.getTime();
    }

    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayDiff(Date before, Date later) {
        LocalDate b = before.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate l = later.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (int) ChronoUnit.DAYS.between(b, l);
    }

    public static Date max(Date d0, Date d1) {
        if (null == d0) {
            return d1;
        }
        if (null == d1) {
            return d0;
        }
        return d0.before(d1) ? d1 : d0;
    }

    public static Date min(Date d0, Date d1) {
        if (null == d0) {
            return d1;
        }
        if (null == d1) {
            return d0;
        }
        return d0.before(d1) ? d0 : d1;
    }

    //


}
