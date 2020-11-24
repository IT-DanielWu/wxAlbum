package ysn.com.wxalbum.utils;

import android.content.Context;
import android.net.Uri;

import ysn.com.utlis.UriUtils;
import ysn.com.utlis.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumUriUtils
 * @Description Uri工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumUriUtils extends UriUtils {

    private static final String FILE_CAMERA_PREFIX = "/ysn_camera_";
    private static final String FILE_CROP_PREFIX = "/ysn_crop_";

    /**
     * 获取相机uri
     *
     * @param rootDirPath 存储目录
     */
    public static Uri getCameraUri(Context context, String rootDirPath) {
        long timeMillis = System.currentTimeMillis();
        return getImageUri(context, rootDirPath, (FILE_CAMERA_PREFIX + timeMillis + ".jpeg"), timeMillis);
    }

    /**
     * 获取裁剪uri
     *
     * @param rootDirPath 存储目录
     */
    public static Uri getCropUri(Context context, String rootDirPath) {
        long timeMillis = System.currentTimeMillis();
        return getImageUri(context, rootDirPath, (FILE_CROP_PREFIX + timeMillis + ".png"), timeMillis);
    }

    /**
     * 获取图片uri
     *
     * @param rootDirPath          存储目录
     * @param fileName             文件命
     * @param fileCreateTimeMillis 文件创建时间/修改时间
     */
    public static Uri getImageUri(Context context, String rootDirPath, String fileName, long fileCreateTimeMillis) {
        if (ValidatorUtils.isBlank(rootDirPath)) {
            rootDirPath = ysn.com.view.cropimageview.utils.FileUtils.getImageFolderFile().getAbsolutePath();
        }
        return ysn.com.view.cropimageview.utils.FileUtils.getImageUri(
                context, AlbumFileUtils.createFile(rootDirPath, fileName), (fileCreateTimeMillis / 1000));
    }
}
