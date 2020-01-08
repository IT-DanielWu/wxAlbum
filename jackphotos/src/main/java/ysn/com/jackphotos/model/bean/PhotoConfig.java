package ysn.com.jackphotos.model.bean;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import ysn.com.jackphotos.model.mode.JackCropMode;

/**
 * @Author yangsanning
 * @ClassName PhotoConfig
 * @Description 配置信息
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoConfig implements Parcelable {

    /**
     * 是否支持拍照
     */
    public boolean useCamera = true;

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
    public JackCropMode jackCropMode = JackCropMode.NO_USE;

    /**
     * 文件保存路径
     */
    public String cropFilePath;

    /**
     * 采用系统裁剪的时候使用到的参数{@link ysn.com.jackphotos.utils.PhotoPageUtils#startSystemCropActivity(Activity, PhotoConfig, Uri)}
     * aspectX, aspectY: 裁剪框比例
     * outputX, outputY: 输出图片大小
     */
    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 300;
    public int outputY = 300;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.useCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.onlyTakePhotos ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSingle ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canPreview ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSelectCount);
        dest.writeStringList(this.selectedPhotoPathList);
        dest.writeInt(this.requestCode);

        dest.writeSerializable(jackCropMode);

        dest.writeString(this.cropFilePath);

        dest.writeInt(this.aspectX);
        dest.writeInt(this.aspectY);
        dest.writeInt(this.outputX);
        dest.writeInt(this.outputY);
    }

    public PhotoConfig() {
    }

    protected PhotoConfig(Parcel in) {
        this.useCamera = in.readByte() != 0;
        this.onlyTakePhotos = in.readByte() != 0;
        this.isSingle = in.readByte() != 0;
        this.canPreview = in.readByte() != 0;
        this.maxSelectCount = in.readInt();
        this.selectedPhotoPathList = in.createStringArrayList();
        this.requestCode = in.readInt();

        this.jackCropMode = (JackCropMode) in.readSerializable();

        this.cropFilePath = in.readString();

        this.aspectX = in.readInt();
        this.aspectY = in.readInt();
        this.outputX = in.readInt();
        this.outputY = in.readInt();
    }

    public static final Creator<PhotoConfig> CREATOR = new Creator<PhotoConfig>() {
        @Override
        public PhotoConfig createFromParcel(Parcel source) {
            return new PhotoConfig(source);
        }

        @Override
        public PhotoConfig[] newArray(int size) {
            return new PhotoConfig[size];
        }
    };
}
