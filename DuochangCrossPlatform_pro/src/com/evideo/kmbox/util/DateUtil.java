package com.evideo.kmbox.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @brief      : [文件功能说明]
 * @verbatim
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 * @endverbatim
 *
 *  Modification History:
 *  Date            Author        Version        Description
 *  -----------------------------------------------
 *  2014-3-31        hemm        1.0        [修订说明]
 *
 */
public class DateUtil {

    public static String getWeekOfDate() {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        Date curDate = new Date(System.currentTimeMillis());
        cal.setTime(curDate);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
    public static String getSystemDate(String dateFormat) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        String  date = dateFormatter.format(Calendar.getInstance().getTime());
        return date;
    }
    
    public static String getSystemTime(String timeFormat) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat(timeFormat);
        String  time = timeFormatter.format(Calendar.getInstance().getTime());
        return time;
    }

}
