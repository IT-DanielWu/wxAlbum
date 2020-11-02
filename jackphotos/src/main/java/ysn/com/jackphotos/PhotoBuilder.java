package ysn.com.jackphotos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import ysn.com.jackphotos.model.bean.PhotoConfig;
import ysn.com.jackphotos.model.mode.JackCropMode;
import ysn.com.jackphotos.page.JackPhotosActivity;
import ysn.com.jackphotos.utils.PhotoPageUtils;

/**
 * @Author yangsanning
 * @ClassName PhotoBuilder
 * @Description PhotoBuilder
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoBuilder {

    private PhotoConfig photoConfig;

    public PhotoBuilder() {
        photoConfig = new PhotoConfig();
    }

    /**
     * 设置裁剪模式(裁剪功能仅支持单选)
     */
    public PhotoBuilder setCropMore(JackCropMode jackCropMode) {
        photoConfig.jackCropMode = jackCropMode;
        return this;
    }

    /**
     * 设置裁剪参数(当裁剪模式为 {@link JackCropMode#SYSTEM} 时方有效)
     * outputX, outputY: 输出图片大小
     */
    public PhotoBuilder setSystemCropConfig(int outputX, int outputY) {
        photoConfig.outputX = outputX;
        photoConfig.outputY = outputY;
        return this;
    }

    /**
     * 设置裁剪参数(当裁剪模式为 {@link JackCropMode#SYSTEM} 时方有效)
     * aspectX, aspectY: 裁剪框比例
     * outputX, outputY: 输出图片大小
     */
    public PhotoBuilder setSystemCropConfig(int aspectX, int aspectY, int outputX, int outputY) {
        photoConfig.aspectX = aspectX;
        photoConfig.aspectY = aspectY;
        photoConfig.outputX = outputX;
        photoConfig.outputY = outputY;
        return this;
    }

    /**
     * 设置文件输出路径(默认{@link ysn.com.view.cropimageview.utils.FileUtils#getYsnUri(Context, Bitmap.CompressFormat)})
     */
    public PhotoBuilder setRootDirPath(String rootDirPath) {
        photoConfig.rootDirPath = rootDirPath;
        return this;
    }

    /**
     * 是否单选(默认false)
     */
    public PhotoBuilder setSingle(boolean isSingle) {
        photoConfig.isSingle = isSingle;
        return this;
    }

    /**
     * 是否可以点击预览(默认为true)
     */
    public PhotoBuilder canPreview(boolean canPreview) {
        photoConfig.canPreview = canPreview;
        return this;
    }

    /**
     * 是否使用拍照功能(默认true)
     */
    public PhotoBuilder useCamera(boolean useCamera) {
        photoConfig.useCamera = useCamera;
        return this;
    }

    /**
     * 是否支持视频(默认false)
     */
    public PhotoBuilder useVideo(boolean useVideo) {
        photoConfig.useVideo = useVideo;
        return this;
    }

    /**
     * 仅使用拍照功能
     */
    public PhotoBuilder onlyTakePhoto(boolean onlyTakePhoto) {
        photoConfig.onlyTakePhotos = onlyTakePhoto;
        return this;
    }

    /**
     * 图片的最大选择数量, 小于等于0时, 不限数量, isSingle为false时才有用。
     */
    public PhotoBuilder setMaxSelectCount(int maxSelectCount) {
        photoConfig.maxSelectCount = maxSelectCount;
        return this;
    }

    /**
     * 设置默认选中相片
     */
    public PhotoBuilder setSelectedPhotoPathList(ArrayList<String> selectedPhotoPathList) {
        photoConfig.selectedPhotoPathList = selectedPhotoPathList;
        return this;
    }

    public PhotoBuilder setSpanCount(int portraitSpanCount, int landscapeSpanCount) {
        photoConfig.portraitSpanCount = portraitSpanCount;
        photoConfig.landscapeSpanCount = landscapeSpanCount;
        return this;
    }

    /**
     * 纠正参数
     */
    private void redressConfig() {
        // 仅拍照, useCamera必须为true
        if (photoConfig.onlyTakePhotos) {
            photoConfig.useCamera = true;
        }

        // 裁剪功能仅支持单选
        if (photoConfig.jackCropMode != JackCropMode.NO_USE) {
            photoConfig.isSingle = true;
        }
    }

    /**
     * 打开相册
     */
    public void start(Activity activity, int requestCode) {
        photoConfig.requestCode = requestCode;
        redressConfig();
        PhotoPageUtils.startPhotoPage(activity, JackPhotosActivity.class, requestCode, photoConfig);
    }

    /**
     * 打开相册
     */
    public void start(Fragment fragment, int requestCode) {
        photoConfig.requestCode = requestCode;
        redressConfig();
        PhotoPageUtils.startPhotoPage(fragment, JackPhotosActivity.class, requestCode, photoConfig);
    }

    /**
     * 打开相册
     */
    public void start(android.app.Fragment fragment, int requestCode) {
        photoConfig.requestCode = requestCode;
        redressConfig();
        PhotoPageUtils.startPhotoPage(fragment, JackPhotosActivity.class, requestCode, photoConfig);
    }
}
