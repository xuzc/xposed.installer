package de.robv.android.xposed.installer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lvyonggang on 2017/4/28.
 */

public class DateUtil {

    /**
     * 获取当前日期标准串
     */
    public static String getNormalFromatDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = formatter.format(currentTime);
        return dateStr ;
    }

    /**
     * @function： 日期换成字符串
     *  @param  date ：日期对象
     */
    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = formatter.format(date);
        return dateStr ;
    }

    /**
     * 字符串转换成日期
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

}
