package ysn.com.jackphotos.utils;

import android.view.View;

/**
 * @Author yangsanning
 * @ClassName ViewUtils
 * @Description 一句话概括作用
 * @Date 2020/1/13
 * @History 2020/1/13 author: description:
 */
public class ViewUtils {

    public static void setVisibility(View... views) {
        setVisibility(View.VISIBLE, views);
    }

    public static void setVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
}
