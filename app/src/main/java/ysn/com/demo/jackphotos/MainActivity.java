package ysn.com.demo.jackphotos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import ysn.com.jackphotos.JackPhotos;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PAGE_REQUEST_CODE_JACK_PHOTOS = 2020;
    private static final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL = 0x00000012;

    private ResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.main_activity_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        resultAdapter = new ResultAdapter();
        recyclerView.setAdapter(resultAdapter);

        findViewById(R.id.main_activity_select).setOnClickListener(this);
        findViewById(R.id.main_activity_single_and_crop).setOnClickListener(this);
        findViewById(R.id.main_activity_camera).setOnClickListener(this);
        findViewById(R.id.main_activity_camera_and_crop).setOnClickListener(this);

        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            // 预加载手机图片
            JackPhotos.preload(this);
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE_EXTERNAL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAGE_REQUEST_CODE_JACK_PHOTOS && data != null) {
            ArrayList<String> photoPathList = data.getStringArrayListExtra(JackPhotos.EXTRA_PHOTOS);
            boolean isFromCamera = data.getBooleanExtra(JackPhotos.EXTRA_IS_FROM_CAMERA, false);
            Log.d("JackPhotos", "是否是拍照图片：" + isFromCamera);
            resultAdapter.setNewData(photoPathList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_select:
                //多选(最多9张)
                JackPhotos.create()
                    // 设置是否使用拍照
                    .useCamera(true)
                    // 设置是否单选
                    .setSingle(false)
                    // 是否点击放大图片查看,，默认为true
                    .canPreview(true)
                    // 图片的最大选择数量，小于等于0时，不限数量。
                    .setMaxSelectCount(9)
                    // 打开相册
                    .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            case R.id.main_activity_single_and_crop:
                //单选并剪裁
                JackPhotos.create()
                    .useCamera(true)
                    // 设置是否使用裁剪
                    .setCrop(true)
                    // 图片剪切的宽高比(默认1.0f, 宽固定为手机屏幕的宽)
                    .setCropRatio(1.0f)
                    .setSingle(true)
                    .canPreview(true)
                    .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            case R.id.main_activity_camera:
                JackPhotos.create()
                    // 仅拍照
                    .onlyTakePhoto(true)
                    .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;

            case R.id.main_activity_camera_and_crop:
                JackPhotos.create()
                    .setCrop(true)
                    .setCropRatio(1.0f)
                    .onlyTakePhoto(true)
                    .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            default:
                break;
        }
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_WRITE_EXTERNAL) {
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 预加载手机相片
                JackPhotos.preload(this);
            }
        }
    }
}
