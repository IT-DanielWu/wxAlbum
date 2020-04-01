# JackPhotos
[![](https://jitpack.io/v/yangsanning/JackPhotos.svg)](https://jitpack.io/#yangsanning/JackPhotos)
[![API](https://img.shields.io/badge/API-19%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=19)

## 效果预览

| [JackPhotos]                      |
| ------------------------------- |
| <img src="images/image1.gif" height="512" /> |


## 主要文件
| 名字             | 摘要           |
| ---------------- | -------------- |
| [JackPhotos] | 图片选择主入口  |


### 1. 基本用法

#### 1.1 AndroidManifest.xml中添加
```android
    	<activity
            android:name="ysn.com.jackphotos.page.JackPhotosActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
        <activity
            android:name="ysn.com.jackphotos.page.JackPreviewActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
```

#### 1.2.1 多选
```android
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
```

#### 1.2.2 单选并剪裁
```android
        JackPhotos.create()
            .useCamera(true)
            // 设置裁剪模式
            .setCropMore(JackCropMode.SYSTEM)
            .setSingle(true)
            .canPreview(true)
            .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
```

#### 1.2.3 仅拍照
```android
        JackPhotos.create()
            // 仅拍照
            .onlyTakePhoto(true)
            // 设置文件输出路径(可选)
            .setRootDirPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
            .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
```

#### 1.2.4 仅拍照并裁剪
```android
        JackPhotos.create()
            .setCropMore(JackCropMode.SYSTEM)
            .onlyTakePhoto(true)
            .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
```

#### 1.2.5 更多使用自行摸索


### 2. 结果获取
```android
        if (requestCode == PAGE_REQUEST_CODE_JACK_PHOTOS && data != null) {
            ArrayList<String> photoPathList = data.getStringArrayListExtra(JackPhotos.EXTRA_PHOTOS);
        }
```

### 3.添加方法

#### 3.1 添加仓库

在项目的 `build.gradle` 文件中配置仓库地址。

```android
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

#### 3.2 添加项目依赖

在需要添加依赖的 Module 下添加以下信息，使用方式和普通的远程仓库一样。

```android
implementation 'com.github.yangsanning:JackPhotos:1.0.0'
```

[JackPhotos]:https://github.com/yangsanning/JackPhotos/blob/master/jackphotos/src/main/java/ysn/com/jackphotos/JackPhotos.java


