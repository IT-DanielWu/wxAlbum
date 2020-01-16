package ysn.com.jackphotos.page;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ysn.com.jackphotos.R;
import ysn.com.jackphotos.constant.JackConstant;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.AnimatorUtils;
import ysn.com.jackphotos.widget.adapter.PreviewAdapter;
import ysn.com.jackphotos.widget.adapter.PreviewPagerAdapter;
import ysn.com.jackphotos.widget.component.PreviewViewPager;
import ysn.com.jackphotos.widget.component.TitleBarView;
import ysn.com.statusbar.StatusBarUtils;

/**
 * @Author yangsanning
 * @ClassName PreviewActivity
 * @Description 图片预览
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PreviewActivity extends AppCompatActivity {

    /**
     * 用静态变量的好处: 1.保证两个页面操作的是同一个列表数据, 2.可以避免数据量大时, Intent传输发生错误问题
     */
    public static ArrayList<Photo> tempPhotoList;
    public static ArrayList<Photo> tempSelectPhotoList;
    private ArrayList<Photo> photoList;
    private ArrayList<Photo> selectPhotoList;
    private PreviewAdapter previewAdapter;

    private boolean isSingle;
    private int maxCount;
    private int position;

    private boolean isBarShow = true;
    private boolean isConfirm = false;

    private TitleBarView titleBarView;
    private PreviewViewPager previewViewPager;
    private RecyclerView previewRecyclerView;
    private LinearLayout bottomLayout;
    private ImageView selectTagImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        if (AndroidVersionUtils.isAndroidP()) {
            // 设置页面全屏显示
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            // 设置页面延伸到刘海区显示
            getWindow().setAttributes(lp);
        }
        StatusBarUtils.showStatusBar(this);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.jack_title_bar));

        photoList = tempPhotoList;
        tempPhotoList = null;
        selectPhotoList = tempSelectPhotoList;
        tempSelectPhotoList = null;

        Intent intent = getIntent();
        maxCount = intent.getIntExtra(JackConstant.EXTRA_MAX_SELECT_COUNT, 0);
        isSingle = intent.getBooleanExtra(JackConstant.EXTRA_IS_SINGLE, Boolean.FALSE);
        position = intent.getIntExtra(JackConstant.EXTRA_POSITION, 0);

        initView();
        setViewClickListener();
        initPreviewAdapter();
        initViewPager();
    }

    private void initView() {
        titleBarView = findViewById(R.id.preview_activity_title_bar_view);
        previewViewPager = findViewById(R.id.preview_activity_view_pager);
        bottomLayout = findViewById(R.id.preview_activity_bottom_layout);
        previewRecyclerView = findViewById(R.id.preview_activity_preview_recycler_view);
        selectTagImageView = findViewById(R.id.preview_activity_select_tag);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) titleBarView.getLayoutParams();
        layoutParams.topMargin = StatusBarUtils.getStatusBarHeight(this);
        titleBarView.setLayoutParams(layoutParams);
        titleBarView.setTitle(1 + "/" + photoList.size());
    }

    private void setViewClickListener() {
        titleBarView.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onIconClick() {
                finish();
            }

            @Override
            public void onSpecialTitleClick() {

            }

            @Override
            public void onConfirmClick() {
                isConfirm = true;
                finish();
            }
        });

        findViewById(R.id.preview_activity_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = previewViewPager.getCurrentItem();
                if (photoList != null && photoList.size() > position) {
                    Photo photo = photoList.get(position);
                    if (selectPhotoList.contains(photo)) {
                        selectPhotoList.remove(photo);
                    } else if (isSingle) {
                        selectPhotoList.clear();
                        selectPhotoList.add(photo);
                    } else if (maxCount <= 0 || selectPhotoList.size() < maxCount) {
                        selectPhotoList.add(photo);
                    }
                    selectChange();
                }
            }
        });
    }

    private void initPreviewAdapter() {
        if (isSingle) {
            return;
        }
        previewAdapter = new PreviewAdapter(this, selectPhotoList);
        previewAdapter.setOnPhotosMultiListener(new PreviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Photo photo) {
                for (int i = 0; i < photoList.size(); i++) {
                    if (photo.equals(photoList.get(i))) {
                        previewViewPager.setCurrentItem(i, false);
                        break;
                    }
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        previewRecyclerView.setLayoutManager(linearLayoutManager);
        previewRecyclerView.setAdapter(previewAdapter);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        PreviewPagerAdapter previewPagerAdapter = new PreviewPagerAdapter(photoList);
        previewViewPager.setAdapter(previewPagerAdapter);
        previewPagerAdapter.setOnItemClickListener(new PreviewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Photo photo) {
                if (isBarShow) {
                    isBarShow = false;
                    AnimatorUtils.translationY(titleBarView, JackConstant.ANIMATOR_DURATION, new AnimatorUtils.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(View view, Animator animation) {
                            if (view != null) {
                                view.setVisibility(View.GONE);
                                StatusBarUtils.hideStatusBar(PreviewActivity.this);
                            }
                        }
                    }, 0, -titleBarView.getHeight());
                    AnimatorUtils.translationY(bottomLayout, JackConstant.ANIMATOR_DURATION, 0, bottomLayout.getHeight());
                } else {
                    isBarShow = true;
                    StatusBarUtils.showStatusBar(PreviewActivity.this);
                    AnimatorUtils.translationY(titleBarView, JackConstant.ANIMATOR_DURATION, titleBarView.getTranslationY(), 0);
                    AnimatorUtils.translationY(bottomLayout, JackConstant.ANIMATOR_DURATION, bottomLayout.getTranslationY(), 0);
                }
            }
        });
        previewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                titleBarView.setTitle(position + 1 + "/" + photoList.size());
                Photo photo = photoList.get(position);
                selectTagImageView.setImageResource(selectPhotoList.contains(photo) ?
                        R.drawable.jack_ic_selected_tag : R.drawable.jack_ic_un_selected_tag);
                if (!isSingle) {
                    previewAdapter.selectPhoto(photo);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        selectChange();
        previewViewPager.setCurrentItem(position);
    }

    private void selectChange() {
        int count = selectPhotoList.size();
        if (count == 0) {
            titleBarView.setConfirmEnabled(Boolean.FALSE);
            titleBarView.setConfirmText(R.string.jack_photo_text_confirm);
        } else {
            titleBarView.setConfirmEnabled(Boolean.TRUE);
            if (isSingle) {
                titleBarView.setConfirmText(R.string.jack_photo_text_confirm);
            } else if (maxCount > 0) {
                titleBarView.setConfirmText(getString(R.string.jack_photo_text_confirm) + "(" + count + "/" + maxCount + ")");
            } else {
                titleBarView.setConfirmText(getString(R.string.jack_photo_text_confirm) + "(" + count + ")");
            }
        }
        if (!isSingle) {
            previewAdapter.setNewDatas(selectPhotoList);
            previewRecyclerView.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(JackConstant.EXTRA_IS_CONFIRM, isConfirm);
        setResult(JackConstant.PAGE_REQUEST_CODE_PREVIEW, intent);
        super.finish();
    }
}

