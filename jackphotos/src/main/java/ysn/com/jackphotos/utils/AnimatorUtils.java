package ysn.com.jackphotos.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @Author yangsanning
 * @ClassName AnimatorUtils
 * @Description 动画工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AnimatorUtils {

    public static void translationY(final View view, long duration, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, ("translationY"), values)
            .setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
        animator.start();
    }

    public static void translationY(final View view, long duration, final OnAnimationEndListener onAnimationEndListener, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, ("translationY"), values)
            .setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd(view, animation);
                }
            }
        });
        animator.start();
    }

    public static void alpha(View view, long duration, float... values) {
        ObjectAnimator.ofFloat(view, "alpha", values)
            .setDuration(duration)
            .start();
    }

    public interface OnAnimationEndListener {

        void onAnimationEnd(View view, Animator animation);
    }
}
