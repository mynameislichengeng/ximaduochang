package com.evideo.kmbox.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.evideo.kmbox.BaseApplication;
import com.evideo.kmbox.R;

import android.content.Context;

public class TimeUtil {
    
    public static int DAY_VALUE_BY_SECOND = 24 * 60 * 60;
    public static int HOUR_VALUE_BY_SECOND = 60 * 60;
    public static int MIN_VALUE_BY_SECOND = 60;
    
    public static int DAY_VALUE_BY_MINUTE = 24 * 60;
    public static int HOUR_VALUE_BY_MINUTE = 60;

    public static String formatTimeByMinuteToDay(long minute) {
        if (minute < 0) {
            return "";
        }
        long tempTime = minute;
        String formatTime = "";

        long day = tempTime / DAY_VALUE_BY_MINUTE;
        formatTime = day + BaseApplication.getInstance().getResources().getString(R.string.day);
        return formatTime;
    }
    
    public static String formatTimeByMinuteToHour(long minute) {
        if (minute < 0) {
            return "";
        }
        long tempTime = minute;
        String formatTime = "";

//        int sec = 0;
        long day = tempTime / DAY_VALUE_BY_MINUTE;
        tempTime -= day * DAY_VALUE_BY_MINUTE;
        long hour = tempTime / HOUR_VALUE_BY_MINUTE;
//        tempTime -= hour * HOUR_VALUE_BY_MINUTE;
        formatTime = (hour + BaseApplication.getInstance().getResources().getString(R.string.hour));
        return formatTime;
    }
    
    public static String formatTimeByMinute(/*Context context,*/ long minute) {
        if (minute < 0) {
            return "";
        }
        long tempTime = minute;
        String formatTime = "";

//        int sec = 0;
        long day = tempTime / DAY_VALUE_BY_MINUTE;
        tempTime -= day * DAY_VALUE_BY_MINUTE;
        long hour = tempTime / HOUR_VALUE_BY_MINUTE;
        tempTime -= hour * HOUR_VALUE_BY_MINUTE;
        long min = minute - day*DAY_VALUE_BY_MINUTE - hour*HOUR_VALUE_BY_MINUTE;
       /* min = tempTime / MIN_VALUE_BY_SECOND;
        tempTime -= min * MIN_VALUE_BY_SECOND;
        sec = tempTime;*/

       /* if (day > 0)*/ {
            formatTime = day + BaseApplication.getInstance().getResources().getString(R.string.day);
        }
        /*if (hour > 0)*/ {
            formatTime += (hour + BaseApplication.getInstance().getResources().getString(R.string.hour));
        }
       /* if (min > 0)*/ {
            formatTime += (min + BaseApplication.getInstance().getResources().getString(R.string.minute));
        }

        return formatTime;
    }
    
    /**
     * @brief : [格式化时间段]
     * @param time
     *            单位为秒
     * @return
     */
    public static String formatTime(Context context, int time) {
        if (context == null || time < 0) {
            return "";
        }
        int tempTime = time;
        String formatTime = "";

        int day = 0;
        int hour = 0;
        int min = 0;
        int sec = 0;
        day = tempTime / DAY_VALUE_BY_SECOND;
        tempTime -= day * DAY_VALUE_BY_SECOND;
        hour = tempTime / HOUR_VALUE_BY_SECOND;
        tempTime -= hour * HOUR_VALUE_BY_SECOND;
        min = tempTime / MIN_VALUE_BY_SECOND;
        tempTime -= min * MIN_VALUE_BY_SECOND;
        sec = tempTime;

        if (day > 0) {
            formatTime = day + context.getString(R.string.day);
        }
        if (hour > 0) {
            formatTime += (hour + context.getString(R.string.hour));
        }
        if (min > 0) {
            formatTime += (min + context.getString(R.string.minute));
        }
        formatTime += (sec + context.getString(R.string.second));

        return formatTime;
    }
    
    /**
     * 获取系统时间（格式为yyyy-MM-dd HH:mm:ss  主要用在数据库修改时间上）
     * */
    public static String getDateTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
    
    /**
     * [把"201508311255"转化成"08-31 12:55"的格式]
     * @param time 时间
     * @return time
     */
    public static String formatTime(String time) {
        if (!(time.length() == 12)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(time.substring(4, 6)).append("-").
        append(time.substring(6, 8)).append(" "+time.substring(8, 10)+":").append(time.substring(10, 12));
        return builder.toString();
    }
    
    /**
     * [功能说明] 时间转化成分秒
     * @param time
     * @return
     */
    public static String getShowTime(int time) {
        time /= 1000;// 将ms转换为s
//        int hour = time / (60*60);
        int minute = time / MIN_VALUE_BY_SECOND;
        int second = time - minute * MIN_VALUE_BY_SECOND;
        return String.format("%02d:%02d", minute, second);
    }
}
