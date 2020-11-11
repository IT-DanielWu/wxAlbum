package ysn.com.jackphotos.widget.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.github.chrisbanes.photoview.PhotoView;

import ysn.com.jackphotos.R;

/**
 * @Author yangsanning
 * @ClassName PreviewLayout
 * @Description 视频预览
 * @Date 2020/11/11
 */
public class PreviewLayout extends FrameLayout {

    public PhotoView photoView;
    public View playView;

    public PreviewLayout(Context context) {
        this(context, null);
    }

    public PreviewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.jack_view_preview_layout, this);
        photoView = findViewById(R.id.jack_preview_layout_photo_view);
        playView = findViewById(R.id.jack_preview_layout_video_play);
    }
}
