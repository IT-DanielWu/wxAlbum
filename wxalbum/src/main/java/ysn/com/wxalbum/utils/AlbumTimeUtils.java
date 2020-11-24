package ysn.com.wxalbum.utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import ysn.com.utlis.TimeUtils;
import ysn.com.wxalbum.R;

/**
 * @Author yangsanning
 * @ClassName AlbumTimeUtils
 * @Description 相册时间工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumTimeUtils extends TimeUtils {

    public static final String MONTH_FORMAT = "yyyy/MM";
    public static final String MINUTES_SECONDS_FORMAT = "mm:ss";

    public static String formatMinutesSeconds(long time) {
        return format(MINUTES_SECONDS_FORMAT, time);
    }

    public static String formatPhotoTime(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Calendar imageTime = Calendar.getInstance();
        imageTime.setTimeInMillis(time);
        if (sameDay(calendar, imageTime)) {
            return context.getString(R.string.album_text_today);
        } else if (sameWeek(calendar, imageTime)) {
            return context.getString(R.string.album_text_this_week);
        } else if (sameMonth(calendar, imageTime)) {
            return context.getString(R.string.album_text_this_month);
        } else {
            return format(MONTH_FORMAT, time);
        }
    }
}
