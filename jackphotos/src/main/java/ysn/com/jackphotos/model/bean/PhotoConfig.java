package ysn.com.jackphotos.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @Author yangsanning
 * @ClassName PhotoConfig
 * @Description 配置信息
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoConfig implements Parcelable {

    /**
     * 是否剪切
     */
    public boolean isCrop = false;

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

    /**
     * 图片剪切的宽高比, 宽固定为手机屏幕的宽
     */
    public float cropRatio = 1.0f;

    public int requestCode;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isCrop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.useCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.onlyTakePhotos ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSingle ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canPreview ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSelectCount);
        dest.writeStringList(this.selectedPhotoPathList);
        dest.writeFloat(this.cropRatio);
        dest.writeInt(this.requestCode);
    }

    public PhotoConfig() {
    }

    protected PhotoConfig(Parcel in) {
        this.isCrop = in.readByte() != 0;
        this.useCamera = in.readByte() != 0;
        this.onlyTakePhotos = in.readByte() != 0;
        this.isSingle = in.readByte() != 0;
        this.canPreview = in.readByte() != 0;
        this.maxSelectCount = in.readInt();
        this.selectedPhotoPathList = in.createStringArrayList();
        this.cropRatio = in.readFloat();
        this.requestCode = in.readInt();
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
