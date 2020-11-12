package ysn.com.jackphotos.widget.component;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * @Author yangsanning
 * @ClassName SmoothScrollLayoutManager
 * @Description 缓慢滚动的LayoutManager
 * @Date 2020/1/16
 * @History 2020/1/16 author: description:
 */
public class SmoothScrollLayoutManager extends LinearLayoutManager {

    private float duration;

    public SmoothScrollLayoutManager(Context context, float duration) {
        super(context);
        this.duration = duration;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

            // 返回：滑过1px时经历的时间(ms)。
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return duration / displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
