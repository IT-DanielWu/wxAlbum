package ysn.com.wxalbum.widget.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.utils.AndroidVersionUtils;
import ysn.com.wxalbum.utils.ImageUtils;
import ysn.com.wxalbum.widget.component.AlbumPreviewLayout;

/**
 * @Author yangsanning
 * @ClassName AlbumPreviewPagerAdapter
 * @Description 相册预览PagerAdapter
 * @Date 2020/1/16
 * @History 2020/1/16 author: description:
 */
public class AlbumPreviewPagerAdapter extends PagerAdapter {

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
    private List<Album> albumList;

    private OnItemClickListener onItemClickListener;
    private OnItemChildClickListener onItemChildClickListener;

    public AlbumPreviewPagerAdapter(List<Album> albumList) {
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        Context context = container.getContext();
        AlbumPreviewLayout albumPreviewLayout = new AlbumPreviewLayout(context);
        PhotoView photoView = albumPreviewLayout.photoView;
        photoView.setZoomable(true);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        final Album photo = albumList.get(position);
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

        View playView = albumPreviewLayout.playView;
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

        container.addView(albumPreviewLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return albumPreviewLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public AlbumPreviewPagerAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public AlbumPreviewPagerAdapter setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
        return this;
    }

    public interface OnItemClickListener {

        void onItemClick(int position, Album photo);
    }

    public interface OnItemChildClickListener {

        void onItemChildClick(Album photo);
    }
}