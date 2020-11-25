package ysn.com.wxalbum.page;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;

import ysn.com.statusbar.StatusBarUtils;
import ysn.com.utlis.AnimatorUtils;
import ysn.com.utlis.ValidatorUtils;
import ysn.com.utlis.ViewUtils;
import ysn.com.wxalbum.R;
import ysn.com.wxalbum.WxAlbum;
import ysn.com.wxalbum.constant.AlbumConstant;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.model.bean.AlbumConfig;
import ysn.com.wxalbum.model.bean.AlbumFolder;
import ysn.com.wxalbum.model.mode.AlbumPhotoCropMode;
import ysn.com.wxalbum.utils.AlbumFileUtils;
import ysn.com.wxalbum.utils.AlbumPageUtils;
import ysn.com.wxalbum.utils.AlbumTimeUtils;
import ysn.com.wxalbum.utils.AlbumUriUtils;
import ysn.com.wxalbum.utils.PermissionUtils;
import ysn.com.wxalbum.widget.adapter.AlbumAdapter;
import ysn.com.wxalbum.widget.adapter.AlbumFolderAdapter;
import ysn.com.wxalbum.widget.component.AlbumTitleBarView;
import ysn.com.wxalbum.widget.helper.AlbumFolderHelper;

/**
 * @Author yangsanning
 * @ClassName AlbumActivity
 * @Description 相册activity
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumActivity extends AppCompatActivity implements View.OnClickListener, AlbumTitleBarView.OnTitleBarClickListener {

    private AlbumConfig albumConfig;

    private GridLayoutManager gridLayoutManager;
    private AlbumAdapter albumAdapter;

    private ArrayList<AlbumFolder> albumFolderList;
    private boolean isShowFolderRecyclerView;
    private AlbumFolder currentAlbumFolder;

    private boolean isShowTime;
    private Handler hideTimeHandler = new Handler();
    private Runnable hideTimeRunnable = new Runnable() {
        @Override
        public void run() {
            hideTime();
        }
    };

    private boolean applyLoadImage = Boolean.FALSE;
    private boolean applyCamera = Boolean.FALSE;

    private Uri cameraUri;
    private Uri cropUri;

    private AlbumTitleBarView titleBarView;
    private TextView timeTextView;
    private RecyclerView recyclerView;
    private TextView previewTextView;
    private RecyclerView folderRecyclerView;
    private View maskView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumConfig = getIntent().getParcelableExtra(AlbumPageUtils.KEY_CONFIG);

        if (albumConfig.onlyTakePhotos) {
            openCamera();
        } else {
            setContentView(R.layout.activity_album);
            StatusBarUtils.setColor(this, getResources().getColor(R.color.album_title_bar));
            initView();
            loadPhotos();
            updatePhotoCountUi(0);
        }
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        if (PermissionUtils.hasWriteExternalPermission(this) && PermissionUtils.hasCameraPermission(this)) {
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (imageCaptureIntent.resolveActivity(getPackageManager()) != null) {
                cameraUri = AlbumUriUtils.getCameraUri(this, albumConfig.rootDirPath);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                imageCaptureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(imageCaptureIntent, AlbumConstant.PAGE_REQUEST_CODE_CAMERA);
            }
        } else {
            PermissionUtils.requestWriteExternalAndCameraPermission(this);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        titleBarView = findViewById(R.id.album_activity_title_bar_view);
        timeTextView = findViewById(R.id.album_activity_time);
        recyclerView = findViewById(R.id.album_activity_recycler_view);
        folderRecyclerView = findViewById(R.id.album_activity_folder_recycler_view);
        previewTextView = findViewById(R.id.album_activity_preview);
        maskView = findViewById(R.id.album_activity_mask);

        titleBarView.setOnTitleBarClickListener(this);
        previewTextView.setOnClickListener(this);
        maskView.setOnClickListener(this);

        //  默认隐藏文件夹列表
        folderRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                folderRecyclerView.setTranslationY(-folderRecyclerView.getHeight());
                folderRecyclerView.setVisibility(View.GONE);
            }
        }, 100);

        initPhotoRecyclerView();
    }

    @Override
    public void onIconClick() {
        finish();
    }

    @Override
    public void onSpecialTitleClick() {
        if (ValidatorUtils.isNotEmptyList(albumFolderList)) {
            if (isShowFolderRecyclerView) {
                hideFolderRecyclerView();
            } else {
                showFolderRecyclerView();
            }
        }
    }

    /**
     * 隐藏文件夹列表
     */
    private void hideFolderRecyclerView() {
        if (isShowFolderRecyclerView) {
            maskView.setVisibility(View.GONE);
            AnimatorUtils.translationY(folderRecyclerView, AlbumConstant.ANIMATOR_DURATION, new AnimatorUtils.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(View view, Animator animation) {
                    folderRecyclerView.setVisibility(View.GONE);
                    titleBarView.setSpecialTitleEnabled(true);
                }
            }, 0, -folderRecyclerView.getHeight());
            isShowFolderRecyclerView = false;
        }
    }

    /**
     * 显示文件夹列表
     */
    private void showFolderRecyclerView() {
        if (!isShowFolderRecyclerView) {
            ViewUtils.setVisibility(maskView, folderRecyclerView);
            AnimatorUtils.translationY(folderRecyclerView, AlbumConstant.ANIMATOR_DURATION, new AnimatorUtils.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(View view, Animator animation) {
                    titleBarView.setSpecialTitleEnabled(true);
                }
            }, -folderRecyclerView.getHeight(), 0);
            isShowFolderRecyclerView = true;
        }
    }

    @Override
    public void onConfirmClick() {
        confirm();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.album_activity_preview) {
            startPreviewActivity(new ArrayList<>(albumAdapter.getSelectedAlbumList()), 0);
        } else if (id == R.id.album_activity_mask) {
            hideFolderRecyclerView();
        }
    }

    /**
     * 跳转图片预览
     */
    private void startPreviewActivity(ArrayList<Album> photoList, int position) {
        if (ValidatorUtils.isNotEmptyList(photoList)) {
            AlbumPageUtils.startPreviewActivity(this, photoList,
                    albumAdapter.getSelectedAlbumList(), albumConfig.isSingle, albumConfig.maxSelectCount, position);
        }
    }

    /**
     * 初始化图片列表
     */
    private void initPhotoRecyclerView() {
        albumAdapter = new AlbumAdapter((this), albumConfig.maxSelectCount, albumConfig.isSingle, albumConfig.canPreview);
        albumAdapter.setOnPhotosMultiListener(new AlbumAdapter.OnAlbumMultiListener() {
            @Override
            public void onAlbumSelectChange(Album album, boolean isSelect, int selectedCount) {
                updatePhotoCountUi(selectedCount);
            }

            @Override
            public void onAlbumItemClick(Album album, int position) {
                startPreviewActivity(albumAdapter.getData(), position);
            }

            @Override
            public void onCameraClick() {
                openCamera();
            }
        });

        // 判断屏幕方向
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, albumConfig.portraitSpanCount);
        } else {
            gridLayoutManager = new GridLayoutManager(this, albumConfig.landscapeSpanCount);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(albumAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateTimeView();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateTimeView();
            }

            /**
             * 更新时间显示
             */
            private void updateTimeView() {
                Album album = albumAdapter.getFirstVisibleItem(gridLayoutManager.findFirstVisibleItemPosition());
                if (album != null) {
                    timeTextView.setText(AlbumTimeUtils.formatPhotoTime((AlbumActivity.this), album.getTime()));
                    showTime();
                }
            }
        });
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (ValidatorUtils.isNotEmptyList(albumFolderList)) {
            selectFolder(albumFolderList.get(0));
        }
    }

    /**
     * 选择文件夹
     */
    private void selectFolder(AlbumFolder photoFolder) {
        if (photoFolder != null && albumAdapter != null && !photoFolder.equals(this.currentAlbumFolder)) {
            currentAlbumFolder = photoFolder;
            titleBarView.setTitle(photoFolder.getName());
            recyclerView.scrollToPosition(0);
            albumAdapter.refresh(photoFolder.getPhotoList(), photoFolder.isUseCamera());
        }
    }

    /**
     * 更新图片数量显示
     */
    private void updatePhotoCountUi(int count) {
        if (count == 0) {
            titleBarView.setConfirmView(false, R.string.album_text_confirm);
            previewTextView.setEnabled(false);
            previewTextView.setText(R.string.album_text_preview);
        } else {
            previewTextView.setEnabled(true);
            previewTextView.setText(getString(R.string.album_text_preview) + "(" + count + ")");
            if (albumConfig.isSingle) {
                titleBarView.setConfirmView(true, R.string.album_text_confirm);
            } else if (albumConfig.maxSelectCount > 0) {
                titleBarView.setConfirmView(true,
                        (getString(R.string.album_text_confirm) + "(" + count + "/" + albumConfig.maxSelectCount + ")"));
            } else {
                titleBarView.setConfirmView(true, (getString(R.string.album_text_confirm) + "(" + count + ")"));
            }
        }
    }

    /**
     * 隐藏时间条
     */
    private void hideTime() {
        if (isShowTime) {
            AnimatorUtils.alpha(timeTextView, AlbumConstant.ANIMATOR_DURATION, 1, 0);
            isShowTime = Boolean.FALSE;
        }
    }

    /**
     * 显示时间条
     */
    private void showTime() {
        if (!isShowTime) {
            AnimatorUtils.alpha(timeTextView, AlbumConstant.ANIMATOR_DURATION, 0, 1);
            isShowTime = Boolean.TRUE;
        }
        startTimeRunnable();
    }

    private void startTimeRunnable() {
        hideTimeHandler.removeCallbacks(hideTimeRunnable);
        hideTimeHandler.postDelayed(hideTimeRunnable, 1500);
    }

    private void confirm() {
        if (albumAdapter == null) {
            return;
        }
        complete(albumAdapter.getSelectedAlbumList());
    }

    private void complete(ArrayList<Album> photoPathList) {
        if (AlbumPhotoCropMode.NO_USE == albumConfig.jackCropMode) {
            finish(photoPathList);
        } else {
            cropUri = AlbumPageUtils.startSystemCropActivity(this, albumConfig,
                    AlbumUriUtils.getImageContentUri(this, photoPathList.get(0).getFilePath()));
        }
    }

    private void finish(ArrayList<Album> photoPathList) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(WxAlbum.EXTRA_PHOTOS, photoPathList);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 从sd卡加载图片
     */
    private void loadPhotos() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 没有图片
            return;
        }

        if (PermissionUtils.hasWriteExternalPermission(this)) {
            AlbumFolderHelper.get(this).loadPhotos(this, albumConfig.useVideo, new AlbumFolderHelper.OnAlbumFolderListListener() {
                @Override
                public void onAlbumFolderList(ArrayList<AlbumFolder> albumList) {
                    AlbumActivity.this.albumFolderList = albumList;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ValidatorUtils.isNotEmptyList(AlbumActivity.this.albumFolderList)) {
                                initFolderRecyclerView();

                                AlbumActivity.this.albumFolderList.get(0).setUseCamera(albumConfig.useCamera);
                                selectFolder(AlbumActivity.this.albumFolderList.get(0));
                                if (albumConfig.selectedPhotoPathList != null && albumAdapter != null) {
                                    albumAdapter.setSelectedAlbumList(albumConfig.selectedPhotoPathList);
                                    updatePhotoCountUi(albumAdapter.getSelectedAlbumList().size());
                                }
                            }
                        }
                    });
                }
            });
        } else {
            PermissionUtils.requestWriteExternalPermission(this);
        }
    }

    /**
     * 初始化文件夹RecyclerView
     */
    private void initFolderRecyclerView() {
        folderRecyclerView.setLayoutManager(new LinearLayoutManager((this)));
        AlbumFolderAdapter albumFolderAdapter = new AlbumFolderAdapter((this), albumFolderList);
        albumFolderAdapter.setOnFolderSelectListener(new AlbumFolderAdapter.OnFolderSelectListener() {
            @Override
            public void onFolderSelect(AlbumFolder photoFolder) {
                selectFolder(photoFolder);
                hideFolderRecyclerView();
            }
        });
        folderRecyclerView.setAdapter(albumFolderAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (applyLoadImage) {
            applyLoadImage = Boolean.FALSE;
            loadPhotos();
        }
        if (applyCamera) {
            applyCamera = Boolean.FALSE;
            openCamera();
        }
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AlbumConstant.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPhotos();
                } else {
                    showPermissionTipsDialog(Boolean.TRUE);
                }
                break;
            case AlbumConstant.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_AND_CAMERA:
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    showPermissionTipsDialog(Boolean.FALSE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理图片预览或相机返回的结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AlbumConstant.PAGE_REQUEST_CODE_PREVIEW:
                if (data != null && data.getBooleanExtra(AlbumConstant.EXTRA_IS_CONFIRM, Boolean.FALSE)) {
                    confirm();
                } else {
                    albumAdapter.notifyDataSetChanged();
                    updatePhotoCountUi(albumAdapter.getSelectedAlbumList().size());
                }
                break;
            case AlbumConstant.PAGE_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, cameraUri));
                    ArrayList<Album> photoList = new ArrayList<>();
                    String photoPath = AlbumUriUtils.getPathForUri(this, cameraUri);
                    photoList.add(new Album(photoPath, photoPath, cameraUri));
                    complete(photoList);
                } else {
                    AlbumFileUtils.deleteFile(this, cameraUri);
                    cameraUri = null;
                    if (albumConfig.onlyTakePhotos) {
                        finish();
                    }
                }
                break;
            case AlbumConstant.PAGE_REQUEST_CODE_CROP:
                AlbumFileUtils.deleteFile(this, cameraUri);
                cameraUri = null;
                if (resultCode == RESULT_OK) {
                    ArrayList<Album> photoList = new ArrayList<>();
                    String photoPath = AlbumUriUtils.getPathForUri(this, cropUri);
                    photoList.add(new Album(photoPath, photoPath, cropUri));
                    finish(photoList);
                } else {
                    AlbumFileUtils.deleteFile(this, cropUri);
                    finish();
                }
            default:
                break;
        }
    }

    /**
     * 弹出授权提示弹窗
     */
    private void showPermissionTipsDialog(final boolean applyLoad) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.album_text_tips)
                .setMessage(R.string.album_text_permissions_hint)
                .setNegativeButton(R.string.album_text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                }).setPositiveButton(R.string.album_text_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                AlbumPageUtils.startAppSettings(AlbumActivity.this, AlbumActivity.this.getPackageName());
                if (applyLoad) {
                    applyLoadImage = true;
                } else {
                    applyCamera = true;
                }
            }
        }).show();
    }

    /**
     * 横竖屏切换处理
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (gridLayoutManager != null && albumAdapter != null) {
            //切换为竖屏
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager.setSpanCount(albumConfig.portraitSpanCount);
            }
            //切换为横屏
            else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridLayoutManager.setSpanCount(albumConfig.landscapeSpanCount);
            }
            albumAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 处理返回时间, 隐藏文件夹列表
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && isShowFolderRecyclerView) {
            hideFolderRecyclerView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
