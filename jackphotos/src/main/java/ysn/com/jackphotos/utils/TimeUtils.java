package ysn.com.jackphotos.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ysn.com.jackphotos.R;

/**
 * @Author yangsanning
 * @ClassName TimeUtils
 * @Description 时间工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class TimeUtils {

    public static final String TIME_FORMAT = "yyyyMMdd_HHmmss";
    public static final String MONTH_FORMAT = "yyyy/MM";

    public static String getTime() {
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());
    }

    public static String formatPhotoTime(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Calendar imageTime = Calendar.getInstance();
        imageTime.setTimeInMillis(time);
        if (sameDay(calendar, imageTime)) {
            return context.getString(R.string.jack_photo_text_today);
        } else if (sameWeek(calendar, imageTime)) {
            return context.getString(R.string.jack_photo_text_this_week);
        } else if (sameMonth(calendar, imageTime)) {
            return context.getString(R.string.jack_photo_text_this_month);
        } else {
            return new SimpleDateFormat(MONTH_FORMAT, Locale.getDefault()).format(new Date(time));
        }
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
