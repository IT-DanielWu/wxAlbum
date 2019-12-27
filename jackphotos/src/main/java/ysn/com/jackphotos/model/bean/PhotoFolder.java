package ysn.com.jackphotos.model.bean;

import java.util.ArrayList;

import ysn.com.jackphotos.utils.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName PhotoFolder
 * @Description 相片文件夹
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoFolder {

    private boolean useCamera;
    private String name;
    private ArrayList<Photo> photoList;

    public PhotoFolder(String name) {
        this.name = name;
    }

    public PhotoFolder(String name, ArrayList<Photo> photoList) {
        this.name = name;
        this.photoList = photoList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<Photo> photoList) {
        this.photoList = photoList;
    }

    public boolean isUseCamera() {
        return useCamera;
    }

    public void setUseCamera(boolean useCamera) {
        this.useCamera = useCamera;
    }

    public void addImage(Photo image) {
        if (image != null && ValidatorUtils.isNotBlank(image.getPath())) {
            if (photoList == null) {
                photoList = new ArrayList<>();
            }
            photoList.add(image);
        }
    }
}
