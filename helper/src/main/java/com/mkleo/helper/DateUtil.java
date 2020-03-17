package com.mkleo.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    /**
     * 获取当前日期字符串
     *
     * @return
     */
    public static String getCurrentDateString(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date());
    }

    public static String getCurrentDateString() {
        return getCurrentDateString("yyyy年MM月dd日");
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH);
    }

    /**
     * 获取当前日
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE);
    }


    public static String getFormatDate(String pattern, long timestamp) {
        if (timestamp <= 0) {
            timestamp = System.currentTimeMillis();
        }
        SimpleDateFormat formatTime = new SimpleDateFormat(pattern);
        return formatTime.format(new Date(timestamp));
    }

}
