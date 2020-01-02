package com.croshe.android.base.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Janesen on 16/4/15.
 */
public class SelfDateTimeUtils {


    public static String formatToTimeTip(String dateTime) {
        if (dateTime == null || dateTime.equals("null")) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar cd = Calendar.getInstance();
        try {
            cd.setTime(sdf.parse(dateTime));

            long minu = (System.currentTimeMillis() - cd.getTimeInMillis()) / 1000 / 60;

            if (minu == 0) {
                return "刚刚";
            }

            if (minu < 60 && minu > 0) {

                return minu + "分钟前";
            } else {
                if (minu < 12 * 60 && minu > 0) {//12个小时内
                    return minu / 60 + "小时" + minu % 60 + "分钟前";
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDateMinute(dateTime);
    }


    public static String formatDate(String dateTime) {
        return formatDateStr(dateTime, "");
    }


    public static String formatDate(String dateTime, String datePattern) {
        return formatDateStr(dateTime, datePattern, "");
    }


    public static String formatDateMinute(String dateTime) {
        return formatDateStr(dateTime, " HH:mm");
    }

    public static String formatDateMinuteSecond(String dateTime) {
        return formatDateStr(dateTime, " HH:mm:ss");
    }

    private static String formatDateStr(String dateTime, String timePattern) {
        if (dateTime == null || dateTime.equals("null")) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(dateTime));
            SimpleDateFormat sdf2 = new SimpleDateFormat(timePattern);

            int subDay = subDay(cd.getTime(), new Date());
            if (subDay == 0) {
                return "今天" + sdf2.format(cd.getTime());
            } else if (subDay == 1) {
                return "昨天" + sdf2.format(cd.getTime());
            } else if (subDay == 2) {
                return "前天" + sdf2.format(cd.getTime());
            }
            Date date = sdf.parse(dateTime);
            return new SimpleDateFormat("yyyy-MM-dd").format(date) + "" + sdf2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }


    private static String formatDateStr(String dateTime, String pattern, String timePattern) {
        if (dateTime == null || dateTime.equals("null")) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(dateTime));
            SimpleDateFormat sdf2 = new SimpleDateFormat(timePattern);

            int subDay = subDay(cd.getTime(), new Date());
            if (subDay == 0) {
                return "今天" + sdf2.format(cd.getTime());
            } else if (subDay == 1) {
                return "昨天" + sdf2.format(cd.getTime());
            } else if (subDay == 2) {
                return "前天" + sdf2.format(cd.getTime());
            }
            Date date = sdf.parse(dateTime);
            return new SimpleDateFormat(pattern).format(date) + "" + sdf2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }


    /**
     * 获得当前时间
     *
     * @return
     */
    public static String getDateTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date());
    }

    /**
     * 获得当前时间
     *
     * @return
     */
    public static String getDateTimeStr(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    /**
     * 获得当前星期
     *
     * @return
     */
    public static String getDataForWeekStr(int week, String showType) {
        String weekStr = null;
        switch (week) {
            case 0:
                weekStr = showType + "日";
                break;
            case 1:
                weekStr = showType + "一";
                break;
            case 2:
                weekStr = showType + "二";
                break;
            case 3:
                weekStr = showType + "三";
                break;
            case 4:
                weekStr = showType + "四";
                break;
            case 5:
                weekStr = showType + "五";
                break;
            case 6:
                weekStr = showType + "六";
                break;
        }
        return weekStr;
    }

    /**
     * 将长时间格式字符串转换为时间
     *
     * @param strDate
     * @param type    yyyy-MM-dd,yyyy-MM-dd HH:mm
     * @return
     */
    public static Date strToDateLong(String strDate, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.CHINA);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = sdf.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 计算日期差
     *
     * @param first
     * @param two
     * @return 单位：天
     */
    public static int subDay(Date first, Date two) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return Math.abs((int) ((sdf.parse(sdf.format(first)).getTime() - sdf.parse(sdf.format(two)).getTime()) / (60 * 60 * 1000 * 24)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算日期差
     *
     * @param start
     * @param end
     * @return 单位：10天12小时
     */
    public static String subDayHours(Date start, Date end) {
        try {
            long timeDiff = Math.abs(end.getTime() - start.getTime());
            long days = timeDiff / (1000 * 60 * 60 * 24);
            long hours = (timeDiff - (days * (1000 * 60 * 60 * 24))) / (1000 * 60 * 60);
            if (days == 0) {
                return hours + "小时";
            } else if (hours == 0) {
                return days + "天";
            }
            return days + "天" + hours + "小时";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }


    /**
     * 获得当前时间
     *
     * @return
     */
    public static String formatDateTime(String dateTime, String pattern) {
        if (StringUtils.isNotEmpty(dateTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                return sdf.format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dateTime;
    }


    public static String formatMillisecond(long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return simpleDateFormat.format(new Date(millisecond));
    }

    public static String formatMillisecond(long millisecond, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(millisecond));
    }


}
