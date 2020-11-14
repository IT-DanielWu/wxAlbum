package ysn.com.wxalbum.page;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ysn.com.wxalbum.R;
import ysn.com.wxalbum.constant.AlbumConstant;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.utlis.AndroidVersionUtils;
import ysn.com.utlis.AnimatorUtils;
import ysn.com.utlis.ToastUtils;
import ysn.com.wxalbum.widget.adapter.AlbumPreviewAdapter;
import ysn.com.wxalbum.widget.adapter.AlbumPreviewPagerAdapter;
import ysn.com.wxalbum.widget.component.AlbumPreviewViewPager;
import ysn.com.wxalbum.widget.component.SmoothScrollLayoutManager;
import ysn.com.wxalbum.widget.component.AlbumTitleBarView;
import ysn.com.statusbar.StatusBarUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumPreviewActivity
 * @Description 相册预览
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumPreviewActivity extends AppCompatActivity {

    /**
     * 用静态变量的好处: 1.保证两个页面操作的是同一个列表数据, 2.可以避免数据量大时, Intent传输发生错误问题
     */
    public static ArrayList<Album> tempAlbumList;
    public static ArrayList<Album> tempAlbumSelectList;
    private ArrayList<Album> albumList;
    private ArrayList<Album> selectAlbumList;
    private AlbumPreviewAdapter albumPreviewAdapter;

    private boolean isSingle;
    private int maxCount;
    private int position;

    private boolean isBarShow = true;
    private boolean isConfirm = false;

    private AlbumTitleBarView titleBarView;
    private AlbumPreviewViewPager previewViewPager;
    private RecyclerView previewRecyclerView;
    private LinearLayout bottomLayout;
    private ImageView selectTagImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_preview);

        if (AndroidVersionUtils.isAndroidP()) {
            // 设置页面全屏显示
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            // 设置页面延伸到刘海区显示
            getWindow().setAttributes(lp);
        }
        StatusBarUtils.showStatusBar(this);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.album_title_bar));

        albumList = tempAlbumList;
        tempAlbumList = null;
        selectAlbumList = tempAlbumSelectList;
        tempAlbumSelectList = null;

        Intent intent = getIntent();
        maxCount = intent.getIntExtra(AlbumConstant.EXTRA_MAX_SELECT_COUNT, 0);
        isSingle = intent.getBooleanExtra(AlbumConstant.EXTRA_IS_SINGLE, Boolean.FALSE);
        position = intent.getIntExtra(AlbumConstant.EXTRA_POSITION, 0);

        initView();
        setViewClickListener();
        initPreviewAdapter();
        initViewPager();
    }

    private void initView() {
        titleBarView = findViewById(R.id.album_preview_activity_title_bar_view);
        previewViewPager = findViewById(R.id.album_preview_activity_view_pager);
        bottomLayout = findViewById(R.id.album_preview_activity_bottom_layout);
        previewRecyclerView = findViewById(R.id.album_preview_activity_preview_recycler_view);
        selectTagImageView = findViewById(R.id.album_preview_activity_select_tag);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) titleBarView.getLayoutParams();
        layoutParams.topMargin = StatusBarUtils.getStatusBarHeight(this);
        titleBarView.setLayoutParams(layoutParams);
        titleBarView.setTitle(1 + "/" + albumList.size());
    }

    private void setViewClickListener() {
        titleBarView.setOnTitleBarClickListener(new AlbumTitleBarView.OnTitleBarClickListener() {
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

        findViewById(R.id.album_preview_activity_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = previewViewPager.getCurrentItem();
                if (albumList != null && albumList.size() > position) {
                    Album photo = albumList.get(position);
                    if (selectAlbumList.contains(photo)) {
                        selectAlbumList.remove(photo);
                    } else if (isSingle) {
                        selectAlbumList.clear();
                        selectAlbumList.add(photo);
                    } else if (maxCount <= 0 || selectAlbumList.size() < maxCount) {
                        selectAlbumList.add(photo);
                    } else {
                        ToastUtils.showMsg(AlbumPreviewActivity.this, R.string.album_format_photo_max_count, maxCount);
                    }
                    selectChange(position);
                }
            }
        });
    }

    private void selectChange(int position) {
        Album photo = albumList.get(position);
        selectTagImageView.setImageResource(
            selectAlbumList.contains(photo) ? R.drawable.album_ic_selected_tag : R.drawable.album_ic_un_selected_tag);

        int count = selectAlbumList.size();
        if (count == 0) {
            titleBarView.setConfirmEnabled(Boolean.FALSE);
            titleBarView.setConfirmText(R.string.album_text_confirm);
        } else {
            titleBarView.setConfirmEnabled(Boolean.TRUE);
            if (isSingle) {
                titleBarView.setConfirmText(R.string.album_text_confirm);
            } else if (maxCount > 0) {
                titleBarView.setConfirmText(getString(R.string.album_text_confirm) + "(" + count + "/" + maxCount + ")");
            } else {
                titleBarView.setConfirmText(getString(R.string.album_text_confirm) + "(" + count + ")");
            }
        }

        if (isSingle) {
            return;
        }
        albumPreviewAdapter.setNewDatas(selectAlbumList, photo);
        previewRecyclerView.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

    private void initPreviewAdapter() {
        if (isSingle) {
            return;
        }
        albumPreviewAdapter = new AlbumPreviewAdapter(this, selectAlbumList)
            .bindRecyclerView(previewRecyclerView)
            .setOnItemClickListener(new AlbumPreviewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Album photo) {
                    for (int i = 0; i < albumList.size(); i++) {
                        if (photo.equals(albumList.get(i))) {
                            previewViewPager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            });
        SmoothScrollLayoutManager layoutManager = new SmoothScrollLayoutManager(this, 60);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        previewRecyclerView.setLayoutManager(layoutManager);
        previewRecyclerView.setAdapter(albumPreviewAdapter);
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        AlbumPreviewPagerAdapter albumPreviewPagerAdapter = new AlbumPreviewPagerAdapter(albumList);
        previewViewPager.setAdapter(albumPreviewPagerAdapter);
        albumPreviewPagerAdapter.setOnItemClickListener(new AlbumPreviewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Album photo) {
                if (isBarShow) {
                    isBarShow = false;
                    AnimatorUtils.translationY(titleBarView, AlbumConstant.ANIMATOR_DURATION, new AnimatorUtils.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(View view, Animator animation) {
                            if (view != null) {
                                view.setVisibility(View.GONE);
                                StatusBarUtils.hideStatusBar(AlbumPreviewActivity.this);
                            }
                        }
                    }, 0, -titleBarView.getHeight());
                    AnimatorUtils.translationY(bottomLayout, AlbumConstant.ANIMATOR_DURATION, 0, bottomLayout.getHeight());
                } else {
                    isBarShow = true;
                    StatusBarUtils.showStatusBar(AlbumPreviewActivity.this);
                    AnimatorUtils.translationY(titleBarView, AlbumConstant.ANIMATOR_DURATION, titleBarView.getTranslationY(), 0);
                    AnimatorUtils.translationY(bottomLayout, AlbumConstant.ANIMATOR_DURATION, bottomLayout.getTranslationY(), 0);
                }
            }
        }).setOnItemChildClickListener(new AlbumPreviewPagerAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(Album photo) {
                // todo: 暂定调用系统播放器播放
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(photo.getFilePath()),"video/*");
                startActivity(intent);
            }
        });
        previewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                titleBarView.setTitle(position + 1 + "/" + albumList.size());
                selectChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        selectChange(0);
        previewViewPager.setCurrentItem(position);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(AlbumConstant.EXTRA_IS_CONFIRM, isConfirm);
        setResult(AlbumConstant.PAGE_REQUEST_CODE_PREVIEW, intent);
        super.finish();
    }
}

