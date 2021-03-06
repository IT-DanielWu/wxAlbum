package ysn.com.wxalbum.widget.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ysn.com.utlis.AndroidVersionUtils;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.model.bean.AlbumFolder;
import ysn.com.wxalbum.observer.AlbumContentObserver;
import ysn.com.wxalbum.utils.AlbumFileUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumFolderHelper
 * @Description 相册加载工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumFolderHelper {

    /**
     * 缓存图片
     */
    private static ArrayList<AlbumFolder> cacheAlbumFolderList = null;
    private boolean isNeedCache = false;

    private Context context;
    private AlbumContentObserver photoContentObserver;
    private static AlbumFolderHelper instance;

    public static AlbumFolderHelper get(Context context) {
        if (instance == null) {
            synchronized (AlbumFolderHelper.class) {
                if (instance == null) {
                    instance = new AlbumFolderHelper(context);
                }
            }
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    private AlbumFolderHelper(Context context) {
        this.context = context;
    }

    /**
     * 预加载图片/视频
     *
     * @param useVideo 是否加载视频
     */
    public void preload(boolean useVideo) {
        initPhotoContentObserver(useVideo);

        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            loadPhotos(context, true, useVideo, null);
        }
    }

    /**
     * 初始化图片观察
     *
     * @param userVideo 是否加载视频
     */
    private void initPhotoContentObserver(final boolean userVideo) {
        isNeedCache = true;
        if (photoContentObserver == null) {
            photoContentObserver = new AlbumContentObserver(context, new AlbumContentObserver.OnAlbumChangeListener() {
                @Override
                public void onAlbumChange() {
                    preload(userVideo);
                }
            });
        }
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        isNeedCache = false;
        if (photoContentObserver != null) {
            context.getApplicationContext().getContentResolver().unregisterContentObserver(photoContentObserver);
            photoContentObserver = null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (AlbumFolderHelper.class) {
                    if (cacheAlbumFolderList != null) {
                        cacheAlbumFolderList.clear();
                        cacheAlbumFolderList = null;
                    }
                }
            }
        }).start();
    }

    /**
     * 从SDCard加载图片/视频
     *
     * @param useVideo             是否加载视频
     * @param onFolderListListener 文件夹实体类监听
     */
    public void loadPhotos(final Context context, final boolean useVideo, final OnAlbumFolderListListener onFolderListListener) {
        loadPhotos(context, false, useVideo, onFolderListListener);
    }

    /**
     * 从SDCard加载图片/视频
     *
     * @param isPreload            是否是预加载
     * @param useVideo             是否加载视频
     * @param onAlbumFolderListListener 文件夹实体类监听
     */
    private void loadPhotos(final Context context, final boolean isPreload, final boolean useVideo,
                            final OnAlbumFolderListListener onAlbumFolderListListener) {
        //由于扫描图片是耗时的操作，所以要在子线程处理。
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (AlbumFolderHelper.class) {
                    boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
                    ArrayList<AlbumFolder> albumFolderList;
                    if (cacheAlbumFolderList == null || isPreload) {
                        ArrayList<Album> sdCardAlbumList = AlbumFileUtils.loadPhotos(context);
                        Collections.sort(sdCardAlbumList, new Comparator<Album>() {
                            @Override
                            public int compare(Album image, Album t1) {
                                if (image.getTime() > t1.getTime()) {
                                    return 1;
                                } else if (image.getTime() < t1.getTime()) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        });

                        ArrayList<Album> albumList = new ArrayList<>();
                        for (Album album : sdCardAlbumList) {
                            // 过滤未下载完成或者不存在的文件(因Android Q用uri判断图片是否有效的方法耗时, 故这里不进行判断)
                            boolean isEffective = isAndroidQ || AlbumFileUtils.isEffective(album.getThumbnails());
                            if (isEffective) {
                                albumList.add(album);
                            }
                        }
                        Collections.reverse(albumList);
                        albumFolderList = AlbumFileUtils.splitFolder(context, albumList, useVideo);
                        if (isNeedCache) {
                            cacheAlbumFolderList = albumFolderList;
                        }
                    } else {
                        albumFolderList = cacheAlbumFolderList;
                    }

                    if (onAlbumFolderListListener != null) {
                        onAlbumFolderListListener.onAlbumFolderList(albumFolderList);
                    }
                }
            }
        }).start();
    }

    public interface OnAlbumFolderListListener {

        /**
         * @param albumList 相册文件夹实体类集合
         */
        void onAlbumFolderList(ArrayList<AlbumFolder> albumList);
    }
}
