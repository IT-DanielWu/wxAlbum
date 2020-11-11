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
import ysn.com.jackphotos.widget.component.PreviewLayout;

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
    private OnItemChildClickListener onItemChildClickListener;

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
        PreviewLayout previewLayout = new PreviewLayout(context);
        PhotoView photoView = previewLayout.photoView;
        photoView.setZoomable(true);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

        View playView = previewLayout.playView;
        if (photo.isVideo()) {
            playView.setVisibility(View.VISIBLE);
            playView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemChildClickListener != null) {
                        onItemChildClickListener.onItemChildClick(photo);
                    }
                }
            });
        } else {
            playView.setVisibility(View.GONE);
        }

        container.addView(previewLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return previewLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public PreviewPagerAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public PreviewPagerAdapter setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
        return this;
    }

    public interface OnItemClickListener {

        void onItemClick(int position, Photo photo);
    }

    public interface OnItemChildClickListener {

        void onItemChildClick(Photo photo);
    }
}