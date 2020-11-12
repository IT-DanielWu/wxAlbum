package ysn.com.jackphotos.widget.component;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import ysn.com.jackphotos.R;

/**
 * @Author yangsanning
 * @ClassName PressedImageView
 * @Description 点击时泛黑
 * @Date 2020/1/13
 * @History 2020/1/13 author: description:
 */
public class PressedTextView extends AppCompatTextView {

    public PressedTextView(Context context) {
        super(context);
    }

    public PressedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPressed()) {
            canvas.drawColor(0x33000000);
        }
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackgroundResource(R.drawable.jack_bg_title_bar_confirm_enabled_true);
        } else {
            setBackgroundResource(R.drawable.jack_bg_title_bar_confirm_enabled_false);
        }
    }
}
