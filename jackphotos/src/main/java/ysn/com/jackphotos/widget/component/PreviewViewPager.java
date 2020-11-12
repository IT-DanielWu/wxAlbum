package ysn.com.jackphotos.widget.component;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @Author yangsanning
 * @ClassName PreviewViewPager
 * @Description 预览viewpager
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PreviewViewPager extends ViewPager {

    public PreviewViewPager(Context context) {
        super(context);
    }

    public PreviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 结合PhotoView使用会抛异常
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
