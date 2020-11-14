package ysn.com.wxalbum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ysn.com.wxalbum.model.bean.AlbumConfig;
import ysn.com.wxalbum.model.mode.AlbumPhotoCropMode;
import ysn.com.wxalbum.page.AlbumActivity;
import ysn.com.wxalbum.utils.AlbumPageUtils;

/**
 * @Author yangsanning
 * @ClassName WxAlbumBuilder
 * @Description WxAlbumBuilder
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class WxAlbumBuilder {

    private AlbumConfig albumConfig;

    public WxAlbumBuilder() {
        albumConfig = new AlbumConfig();
    }

    /**
     * 设置裁剪模式(裁剪功能仅支持单选)
     */
    public WxAlbumBuilder setCropMore(AlbumPhotoCropMode jackCropMode) {
        albumConfig.jackCropMode = jackCropMode;
        return this;
    }

    /**
     * 设置裁剪参数(当裁剪模式为 {@link AlbumPhotoCropMode#SYSTEM} 时方有效)
     * outputX, outputY: 输出图片大小
     */
    public WxAlbumBuilder setSystemCropConfig(int outputX, int outputY) {
        albumConfig.outputX = outputX;
        albumConfig.outputY = outputY;
        return this;
    }

    /**
     * 设置裁剪参数(当裁剪模式为 {@link AlbumPhotoCropMode#SYSTEM} 时方有效)
     * aspectX, aspectY: 裁剪框比例
     * outputX, outputY: 输出图片大小
     */
    public WxAlbumBuilder setSystemCropConfig(int aspectX, int aspectY, int outputX, int outputY) {
        albumConfig.aspectX = aspectX;
        albumConfig.aspectY = aspectY;
        albumConfig.outputX = outputX;
        albumConfig.outputY = outputY;
        return this;
    }

    /**
     * 设置文件输出路径(默认{@link ysn.com.view.cropimageview.utils.FileUtils#getYsnUri(Context, Bitmap.CompressFormat)})
     */
    public WxAlbumBuilder setRootDirPath(String rootDirPath) {
        albumConfig.rootDirPath = rootDirPath;
        return this;
    }

    /**
     * 是否单选(默认false)
     */
    public WxAlbumBuilder setSingle(boolean isSingle) {
        albumConfig.isSingle = isSingle;
        return this;
    }

    /**
     * 是否可以点击预览(默认为true)
     */
    public WxAlbumBuilder canPreview(boolean canPreview) {
        albumConfig.canPreview = canPreview;
        return this;
    }

    /**
     * 是否使用拍照功能(默认true)
     */
    public WxAlbumBuilder useCamera(boolean useCamera) {
        albumConfig.useCamera = useCamera;
        return this;
    }

    /**
     * 是否支持视频(默认false)
     */
    public WxAlbumBuilder useVideo(boolean useVideo) {
        albumConfig.useVideo = useVideo;
        return this;
    }

    /**
     * 仅使用拍照功能
     */
    public WxAlbumBuilder onlyTakePhoto(boolean onlyTakePhoto) {
        albumConfig.onlyTakePhotos = onlyTakePhoto;
        return this;
    }

    /**
     * 图片的最大选择数量, 小于等于0时, 不限数量, isSingle为false时才有用。
     */
    public WxAlbumBuilder setMaxSelectCount(int maxSelectCount) {
        albumConfig.maxSelectCount = maxSelectCount;
        return this;
    }

    /**
     * 设置默认选中相片
     */
    public WxAlbumBuilder setSelectedPhotoPathList(ArrayList<String> selectedPhotoPathList) {
        albumConfig.selectedPhotoPathList = selectedPhotoPathList;
        return this;
    }

    public WxAlbumBuilder setSpanCount(int portraitSpanCount, int landscapeSpanCount) {
        albumConfig.portraitSpanCount = portraitSpanCount;
        albumConfig.landscapeSpanCount = landscapeSpanCount;
        return this;
    }

    /**
     * 纠正参数
     */
    private void redressConfig() {
        // 仅拍照, useCamera必须为true
        if (albumConfig.onlyTakePhotos) {
            albumConfig.useCamera = true;
        }

        // 裁剪功能仅支持单选
        if (albumConfig.jackCropMode != AlbumPhotoCropMode.NO_USE) {
            albumConfig.isSingle = true;
        }
    }

    /**
     * 打开相册
     */
    public void start(Activity activity, int requestCode) {
        albumConfig.requestCode = requestCode;
        redressConfig();
        AlbumPageUtils.startPhotoPage(activity, AlbumActivity.class, requestCode, albumConfig);
    }

    /**
     * 打开相册
     */
    public void start(Fragment fragment, int requestCode) {
        albumConfig.requestCode = requestCode;
        redressConfig();
        AlbumPageUtils.startPhotoPage(fragment, AlbumActivity.class, requestCode, albumConfig);
    }

    /**
     * 打开相册
     */
    public void start(android.app.Fragment fragment, int requestCode) {
        albumConfig.requestCode = requestCode;
        redressConfig();
        AlbumPageUtils.startPhotoPage(fragment, AlbumActivity.class, requestCode, albumConfig);
    }
}
