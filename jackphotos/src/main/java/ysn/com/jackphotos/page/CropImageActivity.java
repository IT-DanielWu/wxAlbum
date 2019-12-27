package ysn.com.jackphotos.page;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;

import ysn.com.jackphotos.JackPhotos;
import ysn.com.jackphotos.R;
import ysn.com.jackphotos.model.bean.PhotoConfig;
import ysn.com.jackphotos.utils.FileUtils;
import ysn.com.jackphotos.utils.ImageUtils;
import ysn.com.jackphotos.utils.PhotoPageUtils;
import ysn.com.jackphotos.utils.TimeUtils;
import ysn.com.jackphotos.utils.ValidatorUtils;
import ysn.com.jackphotos.widget.component.CropPhotoView;
import ysn.com.jackphotos.widget.component.TitleBarView;
import ysn.com.statusbar.StatusBarUtils;

/**
 * @Author yangsanning
 * @ClassName CropImageActivity
 * @Description 一句话概括作用
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class CropImageActivity extends Activity implements TitleBarView.OnTitleBarClickListener {

    private PhotoConfig photoConfig;
    private boolean isFromCamera;

    private TitleBarView titleBarView;
    private CropPhotoView cropPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_crop_photo);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.jack_title_bar));

        photoConfig = getIntent().getParcelableExtra(PhotoPageUtils.KEY_CONFIG);
        photoConfig.isSingle = true;
        photoConfig.maxSelectCount = 0;
        PhotoPageUtils.startPhotoPage(this, PhotosActivity.class, photoConfig.requestCode, photoConfig);

        initView();
    }

    private void initView() {
        titleBarView = findViewById(R.id.crop_photo_activity_title_bar_view);
        cropPhotoView = findViewById(R.id.crop_photo_activity_crop_photo_view);

        titleBarView.setOnTitleBarClickListener(this);
        cropPhotoView.setRatio(photoConfig.cropRatio);
    }

    @Override
    public void onIconClick() {
        finish();
    }

    @Override
    public void onConfirmClick() {
        if (cropPhotoView.getDrawable() != null) {
            titleBarView.setConfirmEnabled(false);

            Bitmap bitmap = cropPhotoView.clipImage();
            String photoPath = null;
            if (bitmap != null) {
                photoPath = FileUtils.savePhoto(bitmap, FileUtils.getPhotoCacheDir(this), TimeUtils.getTime());
                bitmap.recycle();
            }

            if (ValidatorUtils.isNotBlank(photoPath)) {
                ArrayList<String> photoPathList = new ArrayList<>();
                photoPathList.add(photoPath);
                Intent intent = new Intent();
                intent.putStringArrayListExtra(JackPhotos.EXTRA_PHOTOS, photoPathList);
                intent.putExtra(JackPhotos.EXTRA_IS_FROM_CAMERA, isFromCamera);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == photoConfig.requestCode) {
            ArrayList<String> photoList = data.getStringArrayListExtra(JackPhotos.EXTRA_PHOTOS);
            isFromCamera = data.getBooleanExtra(JackPhotos.EXTRA_IS_FROM_CAMERA, false);
            if (ValidatorUtils.isNotEmptyList(photoList)) {
                Bitmap bitmap = ImageUtils.decodeSampledBitmapFromFile(this, photoList.get(0), 720, 1080);
                if (bitmap != null) {
                    cropPhotoView.setBitmapData(bitmap);
                    return;
                }
            }
        }
        finish();
    }
}
