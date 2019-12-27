package ysn.com.jackphotos.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @Author yangsanning
 * @ClassName PhotoContentObserver
 * @Description 图片观察者
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoContentObserver extends ContentObserver {

    private OnPhotoChangeListener onPhotoChangeListener;

    public PhotoContentObserver(Context context, OnPhotoChangeListener onPhotoChangeListener) {
        super(null);
        this.onPhotoChangeListener = onPhotoChangeListener;
        // 监听媒体库变化
        context.getApplicationContext().getContentResolver().registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, this);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (onPhotoChangeListener != null) {
            onPhotoChangeListener.onPhotoContentChange();
        }
    }

    public interface OnPhotoChangeListener {

        void onPhotoContentChange();
    }
}
