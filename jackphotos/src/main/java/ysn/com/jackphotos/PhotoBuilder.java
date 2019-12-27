package ysn.com.jackphotos;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import ysn.com.jackphotos.model.bean.PhotoConfig;
import ysn.com.jackphotos.page.CropImageActivity;
import ysn.com.jackphotos.page.PhotosActivity;
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
     * 是否使用图片剪切功能(默认false), 裁剪功能仅支持单选
     */
    public PhotoBuilder setCrop(boolean isCrop) {
        photoConfig.isCrop = isCrop;
        return this;
    }

    /**
     * 图片裁剪的宽高比, 宽固定为手机屏幕的宽
     */
    public PhotoBuilder setCropRatio(float ratio) {
        photoConfig.cropRatio = ratio;
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

    /**
     * 打开相册
     */
    public void start(Activity activity, int requestCode) {
        photoConfig.requestCode = requestCode;
        // 仅拍照, useCamera必须为true
        if (photoConfig.onlyTakePhotos) {
            photoConfig.useCamera = true;
        }
        PhotoPageUtils.startPhotoPage(activity,
            (photoConfig.isCrop ? CropImageActivity.class : PhotosActivity.class), requestCode, photoConfig);
    }

    /**
     * 打开相册
     */
    public void start(Fragment fragment, int requestCode) {
        photoConfig.requestCode = requestCode;
        // 仅拍照, useCamera必须为true
        if (photoConfig.onlyTakePhotos) {
            photoConfig.useCamera = true;
        }
        PhotoPageUtils.startPhotoPage(fragment,
            (photoConfig.isCrop ? CropImageActivity.class : PhotosActivity.class), requestCode, photoConfig);
    }

    /**
     * 打开相册
     */
    public void start(android.app.Fragment fragment, int requestCode) {
        photoConfig.requestCode = requestCode;
        // 仅拍照, useCamera必须为true
        if (photoConfig.onlyTakePhotos) {
            photoConfig.useCamera = true;
        }
        PhotoPageUtils.startPhotoPage(fragment,
            (photoConfig.isCrop ? CropImageActivity.class : PhotosActivity.class), requestCode, photoConfig);
    }
}
