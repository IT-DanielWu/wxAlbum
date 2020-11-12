package ysn.com.demo.jackphotos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ysn.com.jackphotos.JackPhotos;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.model.mode.JackCropMode;

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

        setOnClickListener((ViewGroup) findViewById(R.id.main_activity_root));

        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            // 预加载手机图片以及视频
            JackPhotos.preload(this, false);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE_EXTERNAL);
        }
    }

    private void setOnClickListener(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                setOnClickListener((ViewGroup) child);
            } else if (child.getId() != View.NO_ID) {
                child.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAGE_REQUEST_CODE_JACK_PHOTOS && data != null) {
            resultAdapter.setNewData(data.<Photo>getParcelableArrayListExtra(JackPhotos.EXTRA_PHOTOS));
        }
    }

    @Override
    public void onClick(View v) {
        // 为方便演示，清楚缓存
        JackPhotos.clearCache(this);

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
                        // 设置裁剪模式
                        .setCropMore(JackCropMode.SYSTEM)
                        // 设置裁剪路径
                        .setSingle(true)
                        .canPreview(true)
                        .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            case R.id.main_activity_camera:
                JackPhotos.create()
                        // 仅拍照
                        .onlyTakePhoto(true)
                        // 设置文件输出路径
                        .setRootDirPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            case R.id.main_activity_camera_and_crop:
                JackPhotos.create()
                        .setCropMore(JackCropMode.SYSTEM)
                        .onlyTakePhoto(true)
                        .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            case R.id.main_activity_video:
                // 多选含视频(最多9张)
                JackPhotos.create()
                        // 设置是否使用拍照
                        .useCamera(true)
                        // 设置是否支持视频
                        .useVideo(true)
                        // 设置是否单选
                        .setSingle(false)
                        // 是否点击放大图片查看,，默认为true
                        .canPreview(true)
                        // 图片的最大选择数量，小于等于0时，不限数量。
                        .setMaxSelectCount(9)
                        // 打开相册
                        .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            default:
                break;
        }
    }
}
