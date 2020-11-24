package ysn.com.wxalbum.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import ysn.com.utlis.FileUtils;
import ysn.com.utlis.ValidatorUtils;
import ysn.com.wxalbum.R;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.model.bean.AlbumFolder;

/**
 * @Author yangsanning
 * @ClassName AlbumFileUtils
 * @Description 相册文件工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumFileUtils extends FileUtils {

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
     * 把图片/视频按文件夹拆分(第一个文件夹保存所有的图片/视频，第二个保存所有视频)
     */
    public static ArrayList<AlbumFolder> splitFolder(Context context, ArrayList<Album> photoList, boolean useVideo) {
        ArrayList<AlbumFolder> photoFolderList = new ArrayList<>();
        if (useVideo) {
            photoFolderList.add(new AlbumFolder(context.getString(R.string.album_text_photos_and_videos), photoList));
            AlbumFolder videosPhotoFolder = new AlbumFolder(context.getString(R.string.album_text_all_video));
            if (ValidatorUtils.isNotEmptyList(photoList)) {
                for (Album photo : photoList) {
                    String name = AlbumFileUtils.getFolderName(photo.getFilePath());
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
