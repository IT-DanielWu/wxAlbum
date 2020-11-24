package ysn.com.utlis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author yangsanning
 * @ClassName TimeUtils
 * @Description 时间工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class TimeUtils {

    public static String format(String format, long time) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(time));
    }

    public static boolean sameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean sameWeek(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean sameMonth(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
    }
}
