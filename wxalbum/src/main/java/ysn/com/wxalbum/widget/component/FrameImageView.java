package ysn.com.wxalbum.widget.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import ysn.com.wxalbum.R;

/**
 * @Author yangsanning
 * @ClassName FrameImageView
 * @Description 一句话概括作用
 * @Date 2020/1/16
 * @History 2020/1/16 author: description:
 */
public class FrameImageView extends AppCompatImageView {

    private Paint paint;

    private boolean isShowFrame;

    public FrameImageView(Context context) {
        this(context, null);
    }

    public FrameImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint(context);
    }

    private void initPaint(Context context) {
        paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.album_primary));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShowFrame) {
            canvas.drawRect(canvas.getClipBounds(), paint);
        }
    }

    public void setShowFrame(boolean showFrame) {
        isShowFrame = showFrame;
        invalidate();
    }
}
