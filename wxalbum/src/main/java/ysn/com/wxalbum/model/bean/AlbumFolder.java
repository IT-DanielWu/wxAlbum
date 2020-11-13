package ysn.com.wxalbum.model.bean;

import android.net.Uri;

import java.util.ArrayList;

import ysn.com.wxalbum.utils.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumFolder
 * @Description 相册文件夹
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumFolder {

    private boolean useCamera;
    private String name;
    private ArrayList<Album> photoList;

    public AlbumFolder(String name) {
        this.name = name;
    }

    public AlbumFolder(String name, ArrayList<Album> photoList) {
        this.name = name;
        this.photoList = photoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Album> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<Album> photoList) {
        this.photoList = photoList;
    }

    public boolean isUseCamera() {
        return useCamera;
    }

    public void setUseCamera(boolean useCamera) {
        this.useCamera = useCamera;
    }

    public void addPhoto(Album photo) {
        if (photo != null && ValidatorUtils.isNotBlank(photo.getFilePath())) {
            if (isNull()) {
                photoList = new ArrayList<>();
            }
            photoList.add(photo);
        }
    }

    public boolean isNotNull() {
        return !isNull();
    }

    public boolean isNull() {
        return photoList == null;
    }

    public String getThumbnails() {
        return photoList.get(0).getThumbnails();
    }

    public Uri getThumbnailsUri() {
        return photoList.get(0).getThumbnailsUri();
    }
}
