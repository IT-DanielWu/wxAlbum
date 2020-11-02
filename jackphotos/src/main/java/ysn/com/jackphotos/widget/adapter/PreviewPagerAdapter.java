package ysn.com.jackphotos.widget.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName PreviewPagerAdapter
 * @Description 图片预览PagerAdapter
 * @Date 2020/1/16
 * @History 2020/1/16 author: description:
 */
public class PreviewPagerAdapter extends PagerAdapter {

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
    private List<Photo> photoList;

    private OnItemClickListener onItemClickListener;

    public PreviewPagerAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        Context context = container.getContext();
        PhotoView photoView = new PhotoView(context);
        photoView.setZoomable(true);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Photo photo = photoList.get(position);
        if (isAndroidQ) {
            ImageUtils.loadImage(context, photo.getThumbnailsUri(), photoView);
        } else {
            ImageUtils.loadImage(context, photo.getThumbnails(), photoView);
        }
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, photo);
                }
            }
        });
        return photoView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position, Photo photo);
    }
}