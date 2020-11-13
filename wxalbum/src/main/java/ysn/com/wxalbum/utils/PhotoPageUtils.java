package ysn.com.wxalbum.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ysn.com.wxalbum.constant.AlbumConstant;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.model.bean.AlbumConfig;
import ysn.com.wxalbum.page.AlbumPreviewActivity;

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
    public static void startPhotoPage(Activity activity, Class<?> cls, int requestCode, AlbumConfig config) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_CONFIG, config);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转选择图片页面
     */
    public static void startPhotoPage(Fragment fragment, Class<?> cls, int requestCode, AlbumConfig config) {
        Intent intent = new Intent(fragment.getActivity(), cls);
        intent.putExtra(KEY_CONFIG, config);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转选择图片页面
     */
    public static void startPhotoPage(android.app.Fragment fragment, Class<?> cls, int requestCode, AlbumConfig config) {
        Intent intent = new Intent(fragment.getActivity(), cls);
        intent.putExtra(KEY_CONFIG, config);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转选择图片预览页面
     */
    public static void startPreviewActivity(Activity activity, ArrayList<Album> photoList,
                                            ArrayList<Album> selectedPhotoList, boolean isSingle,
                                            int maxSelectCount, int position) {
        AlbumPreviewActivity.tempAlbumList = photoList;
        AlbumPreviewActivity.tempAlbumSelectList = selectedPhotoList;
        Intent intent = new Intent(activity, AlbumPreviewActivity.class);
        intent.putExtra(AlbumConstant.EXTRA_MAX_SELECT_COUNT, maxSelectCount);
        intent.putExtra(AlbumConstant.EXTRA_IS_SINGLE, isSingle);
        intent.putExtra(AlbumConstant.EXTRA_POSITION, position);
        activity.startActivityForResult(intent, AlbumConstant.PAGE_REQUEST_CODE_PREVIEW);
    }

    /**
     * 跳转系统裁剪
     */
    public static Uri startSystemCropActivity(Activity activity, AlbumConfig photoConfig, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框比例
        intent.putExtra("aspectX", photoConfig.aspectX);
        intent.putExtra("aspectY", photoConfig.aspectY);
        // 输出图片大小
        intent.putExtra("outputX", photoConfig.outputX);
        intent.putExtra("outputY", photoConfig.outputY);
        intent.putExtra("scale", true);

        Uri uriFile = UriUtils.getCropUri(activity, photoConfig.rootDirPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        activity.startActivityForResult(intent, AlbumConstant.PAGE_REQUEST_CODE_CROP);
        return uriFile;
    }
}
