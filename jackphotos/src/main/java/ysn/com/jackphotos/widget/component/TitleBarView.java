package ysn.com.jackphotos.widget.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ysn.com.jackphotos.R;
import ysn.com.jackphotos.constant.JackConstant;
import ysn.com.jackphotos.utils.AnimatorUtils;

/**
 * @Author yangsanning
 * @ClassName TitleBarView
 * @Description 标题栏
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class TitleBarView extends LinearLayout implements View.OnClickListener {

    private int iconRes;
    private boolean isSpecial;

    private OnTitleBarClickListener onTitleBarClickListener;

    private ImageView iconImageView;
    private TextView titleTextView;
    private View specialLayout;
    private TextView specialTitleTextView;
    private ImageView specialArrowIamgeView;
    private TextView confirmTextView;

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initView(context);
        setViewListener();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBarView);

        iconRes = typedArray.getResourceId(R.styleable.TitleBarView_tbv_icon, R.drawable.jack_ic_close);
        isSpecial = typedArray.getBoolean(R.styleable.TitleBarView_tbv_special, Boolean.FALSE);

        typedArray.recycle();
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
        iconImageView = findViewById(R.id.title_bar_view_icon);
        titleTextView = findViewById(R.id.title_bar_view_title);
        specialLayout = findViewById(R.id.title_bar_view_special_title_layout);
        specialTitleTextView = findViewById(R.id.title_bar_view_special_title);
        specialArrowIamgeView = findViewById(R.id.title_bar_view_special_title_arrow);
        confirmTextView = findViewById(R.id.title_bar_view_confirm);

        iconImageView.setImageResource(iconRes);
        if (isSpecial) {
            titleTextView.setVisibility(GONE);
            specialLayout.setVisibility(VISIBLE);
        }
    }

    private void setViewListener() {
        findViewById(R.id.title_bar_view_icon_layout).setOnClickListener(this);
        specialLayout.setOnClickListener(this);
        confirmTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_bar_view_icon_layout) {
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.onIconClick();
            }
        } else if (view.getId() == R.id.title_bar_view_confirm) {
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.onConfirmClick();
            }
        } else if (view.getId() == R.id.title_bar_view_special_title_layout) {
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.onSpecialTitleClick();
            }
            view.setEnabled(Boolean.FALSE);
            if (specialArrowIamgeView.getTag() == null) {
                specialArrowIamgeView.setTag(Boolean.TRUE);
                AnimatorUtils.rotate(specialArrowIamgeView, JackConstant.ANIMATOR_DURATION, 0, 180);
            } else {
                specialArrowIamgeView.setTag(null);
                AnimatorUtils.rotate(specialArrowIamgeView, JackConstant.ANIMATOR_DURATION, 180, 360);
            }
        }
    }

    public TitleBarView setTitle(String title) {
        if (isSpecial) {
            specialTitleTextView.setText(title);
        } else {
            titleTextView.setText(title);
        }
        return this;
    }

    public void setSpecialTitleEnabled(boolean enabled) {
        specialLayout.setEnabled(enabled);
    }

    public void setConfirmEnabled(boolean enabled) {
        confirmTextView.setEnabled(enabled);
    }

    public void setConfirmText(int confirmTextRes) {
        confirmTextView.setText(confirmTextRes);
    }

    public void setConfirmText(String confirmText) {
        confirmTextView.setText(confirmText);
    }

    public void setConfirmView(boolean enabled, @StringRes int confirmTextRes) {
        confirmTextView.setEnabled(enabled);
        confirmTextView.setText(confirmTextRes);
    }

    public void setConfirmView(boolean enabled, String confirmText) {
        confirmTextView.setEnabled(enabled);
        confirmTextView.setText(confirmText);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
        this.onTitleBarClickListener = onTitleBarClickListener;
    }

    public interface OnTitleBarClickListener {

        void onIconClick();

        void onSpecialTitleClick();

        void onConfirmClick();
    }
}
