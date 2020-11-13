package ysn.com.wxalbum.utils;

import android.os.Build;

/**
 * @Author yangsanning
 * @ClassName AndroidVersionUtils
 * @Description 安卓版本号工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AndroidVersionUtils {

    public static boolean isAndroidL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAndroidN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isAndroidP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static boolean isAndroidQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
