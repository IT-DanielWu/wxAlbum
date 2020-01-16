package ysn.com.jackphotos.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * @Author yangsanning
 * @ClassName ToastUtils
 * @Description 一句话概括作用
 * @Date 2020/1/16
 * @History 2020/1/16 author: description:
 */
public class ToastUtils {

    public static void showMsg(Context context, @StringRes int res, Object... msg) {
        Toast.makeText(context, String.format(context.getResources().getString(res), msg), Toast.LENGTH_SHORT).show();
    }
}
