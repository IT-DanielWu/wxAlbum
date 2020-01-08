package ysn.com.jackphotos;

import android.content.Context;

import ysn.com.jackphotos.widget.helper.PhotofolderHelper;

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
        PhotofolderHelper.get(context).preload();
    }

    /**
     * 清空缓存
     */
    public static void clearCache(Context context) {
        PhotofolderHelper.get(context).clearCache();
    }
}
