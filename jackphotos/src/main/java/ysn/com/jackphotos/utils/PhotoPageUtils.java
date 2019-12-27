package ysn.com.jackphotos.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import ysn.com.jackphotos.constant.JackConstant;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.model.bean.PhotoConfig;
import ysn.com.jackphotos.page.PreviewActivity;

/**
 * @Author yangsanning
 * @ClassName PhotoPageUtils
 * @Description 页面跳转工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoPageUtils {

    public static final String KEY_CONFIG = "KEY_CONFIG";

    /**
     * 跳转应用设置页面
     */
    public static void startAppSettings(Activity activity, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        activity.startActivity(intent);
    }

    /**
     * 跳转选择图片页面
     */
    public static void startPhotoPage(Activity activity, Class<?> cls, int requestCode, PhotoConfig config) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_CONFIG, config);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转选择图片页面
     */
    public static void startPhotoPage(Fragment fragment, Class<?> cls, int requestCode, PhotoConfig config) {
        Intent intent = new Intent(fragment.getActivity(), cls);
        intent.putExtra(KEY_CONFIG, config);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转选择图片页面
     */
    public static void startPhotoPage(android.app.Fragment fragment, Class<?> cls, int requestCode, PhotoConfig config) {
        Intent intent = new Intent(fragment.getActivity(), cls);
        intent.putExtra(KEY_CONFIG, config);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转选择图片预览页面
     */
    public static void startPreviewActivity(Activity activity, ArrayList<Photo> photoList,
                                            ArrayList<Photo> selectedPhotoList, boolean isSingle,
                                            int maxSelectCount, int position) {
        PreviewActivity.tempPhotoList = photoList;
        PreviewActivity.tempSelectPhotoList = selectedPhotoList;
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(JackConstant.EXTRA_MAX_SELECT_COUNT, maxSelectCount);
        intent.putExtra(JackConstant.EXTRA_IS_SINGLE, isSingle);
        intent.putExtra(JackConstant.EXTRA_POSITION, position);
        activity.startActivityForResult(intent, JackConstant.PAGE_REQUEST_CODE_PREVIEW);
    }
}
