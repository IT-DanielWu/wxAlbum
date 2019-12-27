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

    private String path;
    private long time;
    private String name;
    private String mimeType;
    private Uri uri;

    public Photo(String path, long time, String name, String mimeType, Uri uri) {
        this.path = path;
        this.time = time;
        this.name = name;
        this.mimeType = mimeType;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public boolean isGif() {
        return "image/gif".equals(mimeType);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.time);
        dest.writeString(this.name);
        dest.writeString(this.mimeType);
        dest.writeParcelable(this.uri, flags);
    }

    protected Photo(Parcel in) {
        this.path = in.readString();
        this.time = in.readLong();
        this.name = in.readString();
        this.mimeType = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
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
