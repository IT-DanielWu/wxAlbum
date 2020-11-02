package ysn.com.jackphotos;

import android.content.Context;

import ysn.com.jackphotos.widget.helper.PhotoFolderHelper;

/**
 * @Author yangsanning
 * @ClassName JackPhotos
 * @Description 提供给外部使用
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class JackPhotos {

    /**
     * 通过它获取图片集合
     */
    public static final String EXTRA_PHOTOS = "EXTRA_PHOTOS";

    public static PhotoBuilder create() {
        return new PhotoBuilder();
    }

    /**
     * 预加载图片
     */
    public static void preload(Context context) {
        PhotoFolderHelper.get(context).preload(false);
    }

    /**
     * 预加载图片/视频
     *
     * @param isLoadVideo 是否加载视频
     */
    public static void preload(Context context, boolean isLoadVideo) {
        PhotoFolderHelper.get(context).preload(isLoadVideo);
    }

    /**
     * 清空缓存（为避免没必要的错误，每次使用完，清除一下）
     */
    public static void clearCache(Context context) {
        PhotoFolderHelper.get(context).clearCache();
    }
}
