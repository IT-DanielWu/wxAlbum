package ysn.com.jackphotos.model.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author yangsanning
 * @ClassName Photo
 * @Description 相片
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class Photo implements Parcelable {

    private String filePath;
    private long time;
    private String name;
    private String mimeType;
    private String thumbnails;
    private Uri thumbnailsUri;

    private boolean isVideo;

    public Photo(String filePath, long time, String name, String mimeType, String thumbnails, Uri thumbnailsUri) {
        this(filePath, time, name, mimeType, thumbnails, thumbnailsUri, false);
    }

    public Photo(String filePath, long time, String name, String mimeType, String thumbnails,
                 Uri thumbnailsUri, boolean isVideo) {
        this.filePath = filePath;
        this.time = time;
        this.name = name;
        this.mimeType = mimeType;
        this.thumbnails = thumbnails;
        this.thumbnailsUri = thumbnailsUri;
        this.isVideo = isVideo;
    }

    public Uri getThumbnailsUri() {
        return thumbnailsUri;
    }

    public void setThumbnailsUri(Uri thumbnailsUri) {
        this.thumbnailsUri = thumbnailsUri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isPhoto() {
        return !isVideo();
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isGif() {
        return "image/gif".equals(mimeType);
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeLong(this.time);
        dest.writeString(this.name);
        dest.writeString(this.mimeType);
        dest.writeString(this.thumbnails);
        dest.writeParcelable(this.thumbnailsUri, flags);

        dest.writeBoolean(isVideo);
    }

    protected Photo(Parcel in) {
        this.filePath = in.readString();
        this.time = in.readLong();
        this.name = in.readString();
        this.mimeType = in.readString();
        this.thumbnails = in.readString();
        this.thumbnailsUri = in.readParcelable(Uri.class.getClassLoader());

        this.isVideo = in.readBoolean();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
