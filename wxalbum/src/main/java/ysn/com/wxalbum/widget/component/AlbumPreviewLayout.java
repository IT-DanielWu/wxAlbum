package ysn.com.wxalbum.widget.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.github.chrisbanes.photoview.PhotoView;

import ysn.com.wxalbum.R;

/**
 * @Author yangsanning
 * @ClassName PreviewLayout
 * @Description 相册预览Layout
 * @Date 2020/11/11
 */
public class AlbumPreviewLayout extends FrameLayout {

    public PhotoView photoView;
    public View playView;

    public AlbumPreviewLayout(Context context) {
        this(context, null);
    }

    public AlbumPreviewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_album_preview_layout, this);
        photoView = findViewById(R.id.album_preview_layout_photo_view);
        playView = findViewById(R.id.album_preview_layout_video_play);
    }
}
