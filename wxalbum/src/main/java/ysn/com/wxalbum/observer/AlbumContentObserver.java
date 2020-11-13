package ysn.com.wxalbum.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @Author yangsanning
 * @ClassName AlbumContentObserver
 * @Description 相册观察者
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumContentObserver extends ContentObserver {

    private OnAlbumChangeListener onAlbumChangeListener;

    public AlbumContentObserver(Context context, OnAlbumChangeListener onAlbumChangeListener) {
        super(null);
        this.onAlbumChangeListener = onAlbumChangeListener;
        // 监听相册变化
        context.getApplicationContext().getContentResolver().registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, this);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (onAlbumChangeListener != null) {
            onAlbumChangeListener.onAlbumChange();
        }
    }

    public interface OnAlbumChangeListener {

        void onAlbumChange();
    }
}
