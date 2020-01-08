package ysn.com.jackphotos.page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ysn.com.jackphotos.JackPhotos;
import ysn.com.jackphotos.R;
import ysn.com.jackphotos.constant.JackConstant;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.model.bean.PhotoConfig;
import ysn.com.jackphotos.model.bean.PhotoFolder;
import ysn.com.jackphotos.model.mode.JackCropMode;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.AnimatorUtils;
import ysn.com.jackphotos.utils.FileUtils;
import ysn.com.jackphotos.utils.PermissionUtils;
import ysn.com.jackphotos.utils.PhotoPageUtils;
import ysn.com.jackphotos.utils.TimeUtils;
import ysn.com.jackphotos.utils.UriUtils;
import ysn.com.jackphotos.utils.ValidatorUtils;
import ysn.com.jackphotos.widget.adapter.FolderAdapter;
import ysn.com.jackphotos.widget.adapter.PhotosAdapter;
import ysn.com.jackphotos.widget.component.TitleBarView;
import ysn.com.jackphotos.widget.helper.PhotofolderHelper;
import ysn.com.statusbar.StatusBarUtils;

/**
 * @Author yangsanning
 * @ClassName PhotosActivity
 * @Description 一句话概括作用
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotosActivity extends AppCompatActivity implements View.OnClickListener, TitleBarView.OnTitleBarClickListener {

    private PhotoConfig photoConfig;

    private GridLayoutManager gridLayoutManager;
    private PhotosAdapter photosAdapter;

    private ArrayList<PhotoFolder> photoFolderList;
    private boolean isShowFolderRecyclerView;
    private PhotoFolder currentPhotoFolder;

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
    private String cameraPhotoPath;

    private TitleBarView titleBarView;
    private TextView timeTextView;
    private RecyclerView photoRecyclerView;
    private FrameLayout previewLayout;
    private TextView previewTextView;
    private TextView folderNameTextView;
    private RecyclerView folderRecyclerView;
    private View maskView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoConfig = getIntent().getParcelableExtra(PhotoPageUtils.KEY_CONFIG);

        if (photoConfig.onlyTakePhotos) {
            openCamera();
        } else {
            setContentView(R.layout.activity_photos);
            StatusBarUtils.setColor(this, getResources().getColor(R.color.jack_title_bar));
            initView();
            loadPhotos();
            hidePhotoFolderList();
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
                File photoFile;
                Uri photoUri = null;
                if (AndroidVersionUtils.isAndroidQ()) {
                    photoUri = FileUtils.createPhotoPathUri(this);
                } else {
                    photoFile = FileUtils.createPhotoFile();

                    if (photoFile != null) {
                        cameraPhotoPath = photoFile.getAbsolutePath();
                        if (AndroidVersionUtils.isAndroidN()) {
                            // 通过FileProvider创建一个content类型的Uri
                            photoUri = FileProvider.getUriForFile((this), (getPackageName() + ".JackPhotosProvider"), photoFile);
                        } else {
                            photoUri = Uri.fromFile(photoFile);
                        }
                    }
                }

                cameraUri = photoUri;
                if (photoUri != null) {
                    imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    imageCaptureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(imageCaptureIntent, JackConstant.PAGE_REQUEST_CODE_CAMERA);
                }
            }
        } else {
            PermissionUtils.requestWriteExternalAndCameraPermission(this);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        titleBarView = findViewById(R.id.photos_activity_title_bar_view);
        timeTextView = findViewById(R.id.photos_activity_time);
        photoRecyclerView = findViewById(R.id.photos_activity_photo_recycler_view);
        folderRecyclerView = findViewById(R.id.photos_activity_folder_recycler_view);
        previewTextView = findViewById(R.id.photos_activity_preview);
        previewLayout = findViewById(R.id.photos_activity_preview_layout);
        folderNameTextView = findViewById(R.id.photos_activity_folder_name);
        maskView = findViewById(R.id.photos_activity_mask);

        titleBarView.setOnTitleBarClickListener(this);
        previewLayout.setOnClickListener(this);
        findViewById(R.id.photos_activity_folder_select_layout).setOnClickListener(this);
        maskView.setOnClickListener(this);

        initPhotoRecyclerView();
    }

    @Override
    public void onIconClick() {
        finish();
    }

    @Override
    public void onConfirmClick() {
        confirm();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.photos_activity_preview_layout) {
            startPreviewActivity(new ArrayList<>(photosAdapter.getSelectedPhotoList()), 0);
        } else if (id == R.id.photos_activity_folder_select_layout) {
            if (ValidatorUtils.isNotEmptyList(photoFolderList)) {
                if (isShowFolderRecyclerView) {
                    hideFolderRecyclerView();
                } else {
                    showFolderRecyclerView();
                }
            }
        } else if (id == R.id.photos_activity_mask) {
            hideFolderRecyclerView();
        }
    }

    /**
     * 跳转图片预览
     */
    private void startPreviewActivity(ArrayList<Photo> photoList, int position) {
        if (ValidatorUtils.isNotEmptyList(photoList)) {
            PhotoPageUtils.startPreviewActivity(this, photoList,
                photosAdapter.getSelectedPhotoList(), photoConfig.isSingle, photoConfig.maxSelectCount, position);
        }
    }

    /**
     * 隐藏文件夹列表
     */
    private void hideFolderRecyclerView() {
        if (isShowFolderRecyclerView) {
            maskView.setVisibility(View.GONE);
            AnimatorUtils.translationY(folderRecyclerView, JackConstant.ANIMATOR_DURATION, 0, folderRecyclerView.getHeight());
            isShowFolderRecyclerView = false;
        }
    }

    /**
     * 显示文件夹列表
     */
    private void showFolderRecyclerView() {
        if (!isShowFolderRecyclerView) {
            maskView.setVisibility(View.VISIBLE);
            AnimatorUtils.translationY(folderRecyclerView, JackConstant.ANIMATOR_DURATION, folderRecyclerView.getHeight(), 0);
            isShowFolderRecyclerView = true;
        }
    }

    /**
     * 初始化图片列表
     */
    private void initPhotoRecyclerView() {
        photosAdapter = new PhotosAdapter((this), photoConfig.maxSelectCount, photoConfig.isSingle, photoConfig.canPreview);
        photosAdapter.setOnPhotosMultiListener(new PhotosAdapter.OnPhotosMultiListener() {
            @Override
            public void onPhotoSelectChange(Photo photo, boolean isSelect, int selectedCount) {
                updatePhotoCountUi(selectedCount);
            }

            @Override
            public void onPhotoItemClick(Photo photo, int position) {
                startPreviewActivity(photosAdapter.getData(), position);
            }

            @Override
            public void onCameraClick() {
                openCamera();
            }
        });

        // 判断屏幕方向
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 4);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 5);
        }
        photoRecyclerView.setLayoutManager(gridLayoutManager);
        photoRecyclerView.setAdapter(photosAdapter);
        photoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                Photo photo = photosAdapter.getFirstVisibleItem(gridLayoutManager.findFirstVisibleItemPosition());
                if (photo != null) {
                    timeTextView.setText(TimeUtils.formatPhotoTime((PhotosActivity.this), photo.getTime()));
                    showTime();
                }
            }
        });
        ((SimpleItemAnimator) photoRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (ValidatorUtils.isNotEmptyList(photoFolderList)) {
            selectFolder(photoFolderList.get(0));
        }
    }

    /**
     * 选择文件夹
     */
    private void selectFolder(PhotoFolder photoFolder) {
        if (photoFolder != null && photosAdapter != null && !photoFolder.equals(this.currentPhotoFolder)) {
            this.currentPhotoFolder = photoFolder;
            folderNameTextView.setText(photoFolder.getName());
            photoRecyclerView.scrollToPosition(0);
            photosAdapter.refresh(photoFolder.getPhotoList(), photoFolder.isUseCamera());
        }
    }

    /**
     * 更新图片数量显示
     */
    private void updatePhotoCountUi(int count) {
        if (count == 0) {
            titleBarView.setConfirmView(false, R.string.jack_photo_text_confirm);
            previewTextView.setText(R.string.jack_photo_text_preview);
        } else {
            previewLayout.setEnabled(true);
            previewTextView.setText(getString(R.string.jack_photo_text_preview) + "(" + count + ")");
            if (photoConfig.isSingle) {
                titleBarView.setConfirmView(true, R.string.jack_photo_text_confirm);
            } else if (photoConfig.maxSelectCount > 0) {
                titleBarView.setConfirmView(true,
                    (getString(R.string.jack_photo_text_confirm) + "(" + count + "/" + photoConfig.maxSelectCount + ")"));
            } else {
                titleBarView.setConfirmView(true, (getString(R.string.jack_photo_text_confirm) + "(" + count + ")"));
            }
        }
    }

    /**
     * 隐藏时间条
     */
    private void hideTime() {
        if (isShowTime) {
            AnimatorUtils.alpha(timeTextView, JackConstant.ANIMATOR_DURATION, 1, 0);
            isShowTime = Boolean.FALSE;
        }
    }

    /**
     * 显示时间条
     */
    private void showTime() {
        if (!isShowTime) {
            AnimatorUtils.alpha(timeTextView, JackConstant.ANIMATOR_DURATION, 0, 1);
            isShowTime = Boolean.TRUE;
        }
        startTimeRunnable();
    }

    private void startTimeRunnable() {
        hideTimeHandler.removeCallbacks(hideTimeRunnable);
        hideTimeHandler.postDelayed(hideTimeRunnable, 1500);
    }

    private void confirm() {
        if (photosAdapter == null) {
            return;
        }
        ArrayList<Photo> selectedPhotoList = photosAdapter.getSelectedPhotoList();
        ArrayList<String> photoPathList = new ArrayList<>();
        for (Photo photo : selectedPhotoList) {
            photoPathList.add(photo.getPath());
        }

        complete(photoPathList);
    }

    private void complete(ArrayList<String> photoPathList) {
        if (JackCropMode.NO_USE == photoConfig.jackCropMode) {
            finish(photoPathList);
        } else {
            cropUri = PhotoPageUtils.startSystemCropActivity(this, photoConfig,
                UriUtils.getImageContentUri(this, photoPathList.get(0)));
        }
    }

    private void finish(ArrayList<String> photoPathList) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(JackPhotos.EXTRA_PHOTOS, photoPathList);
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
            PhotofolderHelper.get(this).loadImageForSDCard(this, new PhotofolderHelper.OnPhotoFolderListListener() {
                @Override
                public void onPhotoFolderList(ArrayList<PhotoFolder> photoFolderList) {
                    PhotosActivity.this.photoFolderList = photoFolderList;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ValidatorUtils.isNotEmptyList(PhotosActivity.this.photoFolderList)) {
                                initFolderRecyclerView();

                                PhotosActivity.this.photoFolderList.get(0).setUseCamera(photoConfig.useCamera);
                                selectFolder(PhotosActivity.this.photoFolderList.get(0));
                                if (photoConfig.selectedPhotoPathList != null && photosAdapter != null) {
                                    photosAdapter.setSelectedPhotoList(photoConfig.selectedPhotoPathList);
                                    updatePhotoCountUi(photosAdapter.getSelectedPhotoList().size());
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
     * 默认隐藏文件夹列表
     */
    private void hidePhotoFolderList() {
        folderRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                folderRecyclerView.setTranslationY(folderRecyclerView.getHeight());
                folderRecyclerView.setVisibility(View.GONE);
                folderRecyclerView.setBackgroundColor(Color.WHITE);
            }
        });
    }

    /**
     * 初始化文件夹RecyclerView
     */
    private void initFolderRecyclerView() {
        folderRecyclerView.setLayoutManager(new LinearLayoutManager((this)));
        FolderAdapter folderAdapter = new FolderAdapter((this), photoFolderList);
        folderAdapter.setOnFolderSelectListener(new FolderAdapter.OnFolderSelectListener() {
            @Override
            public void onFolderSelect(PhotoFolder photoFolder) {
                selectFolder(photoFolder);
                hideFolderRecyclerView();
            }
        });
        folderRecyclerView.setAdapter(folderAdapter);
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
            case JackConstant.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadPhotos();
                } else {
                    showPermissionTipsDialog(Boolean.TRUE);
                }
                break;
            case JackConstant.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_AND_CAMERA:
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
            case JackConstant.PAGE_REQUEST_CODE_PREVIEW:
                if (data != null && data.getBooleanExtra(JackConstant.EXTRA_IS_CONFIRM, Boolean.FALSE)) {
                    confirm();
                } else {
                    photosAdapter.notifyDataSetChanged();
                    updatePhotoCountUi(photosAdapter.getSelectedPhotoList().size());
                }
                break;
            case JackConstant.PAGE_REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> photoPathList = new ArrayList<>();
                    if (AndroidVersionUtils.isAndroidQ()) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, cameraUri));
                        photoPathList.add(UriUtils.getPathForUri(this, cameraUri));
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPhotoPath))));
                        photoPathList.add(cameraPhotoPath);
                    }
                    complete(photoPathList);
                } else {
                    if (photoConfig.onlyTakePhotos) {
                        finish();
                    }
                }
                break;
            case JackConstant.PAGE_REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> photoPathList = new ArrayList<>();
                    photoPathList.add(UriUtils.getPathForUri(this, cropUri));
                    finish(photoPathList);
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
            .setTitle(R.string.jack_photo_text_tips)
            .setMessage(R.string.jack_photo_text_permissions_hint)
            .setNegativeButton(R.string.jack_photo_text_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            }).setPositiveButton(R.string.jack_photo_text_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                PhotoPageUtils.startAppSettings(PhotosActivity.this, PhotosActivity.this.getPackageName());
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
        if (gridLayoutManager != null && photosAdapter != null) {
            //切换为竖屏
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager.setSpanCount(3);
            }
            //切换为横屏
            else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridLayoutManager.setSpanCount(5);
            }
            photosAdapter.notifyDataSetChanged();
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
