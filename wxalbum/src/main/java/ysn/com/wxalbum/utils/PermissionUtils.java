package ysn.com.wxalbum.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ysn.com.wxalbum.constant.AlbumConstant;

/**
 * @Author yangsanning
 * @ClassName PermissionUtils
 * @Description 权限工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PermissionUtils {

    /**
     * 是否有写权限
     */
    public static boolean hasWriteExternalPermission(Context context) {
        return hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 是否有相机权限
     */
    public static boolean hasCameraPermission(Context context) {
        return hasPermission(context, Manifest.permission.CAMERA);
    }

    private static boolean hasPermission(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission);
    }

    /**
     * 请求写权限
     */
    public static void requestWriteExternalPermission(Activity activity) {
        requestPermission(activity, AlbumConstant.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 请求相机权限
     */
    public static void requestCameraPermission(Activity activity) {
        requestPermission(activity, AlbumConstant.PERMISSION_REQUEST_CODE_CAMERA, Manifest.permission.CAMERA);
    }

    /**
     * 请求写权限和相机权限
     */
    public static void requestWriteExternalAndCameraPermission(Activity activity) {
        requestPermission(activity, AlbumConstant.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_AND_CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    private static void requestPermission(Activity activity, int requestCode, String... permission) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }
}
