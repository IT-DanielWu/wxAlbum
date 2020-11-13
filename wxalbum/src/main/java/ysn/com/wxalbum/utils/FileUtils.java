package ysn.com.wxalbum.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ysn.com.wxalbum.R;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.model.bean.AlbumFolder;

/**
 * @Author yangsanning
 * @ClassName FileUtils
 * @Description 文件工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class FileUtils {

    /**
     * 从SDCard加载图片
     */
    public static synchronized ArrayList<Album> loadPhotos(Context context) {
        ArrayList<Album> photoList = loadPhotoForSDCard(context);
        photoList.addAll(loadVideoForSDCard(context));
        return photoList;
    }

    /**
     * 从SDCard加载图片
     */
    public static synchronized ArrayList<Album> loadPhotoForSDCard(Context context) {

        // 扫描图片
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(imageUri, new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.MIME_TYPE},
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED);

        ArrayList<Album> photoList = new ArrayList<>();
        // 读取扫描到的图片
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取图片的路径
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                // 获取图片名称
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                // 获取图片时间
                long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                if (String.valueOf(time).length() < 13) {
                    time *= 1000;
                }
                //获取图片类型
                String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                //获取图片uri
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
                photoList.add(new Album(filePath, time, name, mimeType, filePath, uri));
            }
            cursor.close();
        }
        return photoList;
    }


    /**
     * 从SDCard加载视频
     */
    public static synchronized ArrayList<Album> loadVideoForSDCard(Context context) {

        // 扫描视频
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        String[] videoProjection = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION};
        Cursor videoCursor = contentResolver.query(videoUri, videoProjection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED);

        ArrayList<Album> photoList = new ArrayList<>();
        // 读取扫描到的视频
        if (videoCursor != null) {
            while (videoCursor.moveToNext()) {
                // 获取视频路径
                long id = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String filePath = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                // 获取视频名称
                String name = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                // 获取视频时间
                long time = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                if (String.valueOf(time).length() < 13) {
                    time *= 1000;
                }
                // 获取视频类型
                String mimeType = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));

                // 获取视频时长
                long duration = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                // 提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                Cursor cursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                        new String[]{id + ""},
                        null);
                String thumbnails = "";
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        thumbnails = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    }
                    cursor.close();
                }
                // 获取视频缩略图uri
                Uri thumbnailsUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();

                photoList.add(new Album(filePath, time, duration, name, mimeType, thumbnails, thumbnailsUri));
            }
            videoCursor.close();
        }
        return photoList;
    }

    /**
     * 判断文件是否有效(过滤未下载完成或者不存在的文件)
     */
    public static boolean isEffective(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outWidth > 0 && options.outHeight > 0;
    }

    /**
     * 判断文件是否有效(过滤未下载完成或者不存在的文件), AndroidQ 需要使用uri(耗时操作)
     */
    public static boolean isEffective(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            return options.outWidth > 0 && options.outHeight > 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param rootDirPath 存储目录
     * @param fileName    文件名
     */
    public static File createFile(String rootDirPath, String fileName) {
        return createFile(new File(rootDirPath), fileName);
    }

    /**
     * @param rootDir  存储目录
     * @param fileName 文件名
     */
    public static File createFile(File rootDir, String fileName) {
        if (rootDir.exists()) {
            rootDir.mkdir();
        }
        return new File(rootDir, fileName);
    }

    /**
     * 删除文件
     */
    public static void deleteFile(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 24) {
            if (uri == null) {
                return;
            }
            context.getContentResolver().delete(uri, null, null);
        } else {
            String path = UriUtils.getPathForUri(context, uri);
            if (path == null) {
                return;
            }
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 把图片/视频按文件夹拆分(第一个文件夹保存所有的图片/视频，第二个保存所有视频)
     */
    public static ArrayList<AlbumFolder> splitFolder(Context context, ArrayList<Album> photoList, boolean useVideo) {
        ArrayList<AlbumFolder> photoFolderList = new ArrayList<>();
        if (useVideo) {
            photoFolderList.add(new AlbumFolder(context.getString(R.string.album_text_photos_and_videos), photoList));
            AlbumFolder videosPhotoFolder = new AlbumFolder(context.getString(R.string.album_text_all_video));
            if (ValidatorUtils.isNotEmptyList(photoList)) {
                for (Album photo : photoList) {
                    String name = FileUtils.getFolderName(photo.getFilePath());
                    if (ValidatorUtils.isNotBlank(name)) {
                        getFolder(name, photoFolderList).addPhoto(photo);
                    }
                    if (photo.isVideo()) {
                        videosPhotoFolder.addPhoto(photo);
                    }
                }
            }
            if (videosPhotoFolder.isNotNull()) {
                photoFolderList.add(1, videosPhotoFolder);
            }
        } else {
            AlbumFolder allPhotoFolder = new AlbumFolder(context.getString(R.string.album_text_all_photos));
            photoFolderList.add(allPhotoFolder);
            if (ValidatorUtils.isNotEmptyList(photoList)) {
                for (Album photo : photoList) {
                    if (photo.isVideo()) {
                        continue;
                    }
                    allPhotoFolder.addPhoto(photo);
                    String name = getFolderName(photo.getFilePath());
                    if (ValidatorUtils.isNotBlank(name)) {
                        getFolder(name, photoFolderList).addPhoto(photo);
                    }
                }
            }
        }
        return photoFolderList;
    }

    /**
     * 根据文件路径, 获取文件夹名称
     */
    public static String getFolderName(String path) {
        if (ValidatorUtils.isNotBlank(path)) {
            String[] strings = path.split(File.separator);
            if (strings.length >= 2) {
                return strings[strings.length - 2];
            }
        }
        return "";
    }

    public static AlbumFolder getFolder(String name, List<AlbumFolder> photoFolderList) {
        for (AlbumFolder folder : photoFolderList) {
            if (name.equals(folder.getName())) {
                return folder;
            }
        }
        AlbumFolder newFolder = new AlbumFolder(name);
        photoFolderList.add(newFolder);
        return newFolder;
    }
}
