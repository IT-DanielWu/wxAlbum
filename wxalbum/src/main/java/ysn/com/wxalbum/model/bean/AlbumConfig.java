package ysn.com.wxalbum.model.bean;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ysn.com.wxalbum.model.mode.AlbumPhotoCropMode;
import ysn.com.wxalbum.utils.AlbumPageUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumConfig
 * @Description 相册的配置信息
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumConfig implements Parcelable {

    /**
     * 是否支持拍照
     */
    public boolean useCamera = true;

    /**
     * 是否支持视频
     */
    public boolean useVideo = false;

    /**
     * 仅拍照, 不打开相册(true时, useCamera也必定为true)
     */
    public boolean onlyTakePhotos = false;

    /**
     * 是否单选
     */
    public boolean isSingle = false;

    /**
     * 是否可以点击图片预览
     */
    public boolean canPreview = true;

    /**
     * 图片的最大选择数量, 当小于等于0时选择不限数量, isSingle为false时才有用
     */
    public int maxSelectCount;

    /**
     * 已选中的图片
     */
    public ArrayList<String> selectedPhotoPathList;

    public int requestCode;

    /**
     * 裁剪模式
     */
    public AlbumPhotoCropMode jackCropMode = AlbumPhotoCropMode.NO_USE;

    /**
     * 采用系统裁剪的时候使用到的参数{@link AlbumPageUtils#startSystemCropActivity(Activity, AlbumConfig, Uri)}
     * aspectX, aspectY: 裁剪框比例
     * outputX, outputY: 输出图片大小
     */
    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 300;
    public int outputY = 300;

    /**
     * 文件输出路径
     */
    public String rootDirPath;

    /**
     * 照片选择页面横竖屏展示个数
     */
    public int portraitSpanCount = 4;
    public int landscapeSpanCount = 5;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.useCamera);
        dest.writeBoolean(this.useVideo);
        dest.writeBoolean(this.onlyTakePhotos);
        dest.writeBoolean(this.isSingle);
        dest.writeBoolean(this.canPreview);
        dest.writeInt(this.maxSelectCount);
        dest.writeStringList(this.selectedPhotoPathList);
        dest.writeInt(this.requestCode);

        dest.writeSerializable(jackCropMode);

        dest.writeInt(this.aspectX);
        dest.writeInt(this.aspectY);
        dest.writeInt(this.outputX);
        dest.writeInt(this.outputY);

        dest.writeString(this.rootDirPath);
    }

    public AlbumConfig() {
    }

    protected AlbumConfig(Parcel in) {
        this.useCamera = in.readBoolean();
        this.useVideo = in.readBoolean();
        this.onlyTakePhotos = in.readBoolean();
        this.isSingle = in.readBoolean();
        this.canPreview = in.readBoolean();
        this.maxSelectCount = in.readInt();
        this.selectedPhotoPathList = in.createStringArrayList();
        this.requestCode = in.readInt();

        this.jackCropMode = (AlbumPhotoCropMode) in.readSerializable();

        this.aspectX = in.readInt();
        this.aspectY = in.readInt();
        this.outputX = in.readInt();
        this.outputY = in.readInt();

        this.rootDirPath = in.readString();
    }

    public static final Creator<AlbumConfig> CREATOR = new Creator<AlbumConfig>() {
        @Override
        public AlbumConfig createFromParcel(Parcel source) {
            return new AlbumConfig(source);
        }

        @Override
        public AlbumConfig[] newArray(int size) {
            return new AlbumConfig[size];
        }
    };
}
