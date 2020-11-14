package ysn.com.utlis;

import android.os.Build;

/**
 * @Author yangsanning
 * @ClassName AndroidVersionUtils
 * @Description 安卓版本号工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AndroidVersionUtils {

    /**
     * 是不是大于安卓 5
     */
    public static boolean isAndroidL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是不是大于安卓 7
     */
    public static boolean isAndroidN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 是不是大于安卓 9
     */
    public static boolean isAndroidP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 是不是大于安卓 10
     */
    public static boolean isAndroidQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
