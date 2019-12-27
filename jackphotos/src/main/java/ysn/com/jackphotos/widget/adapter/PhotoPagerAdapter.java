package ysn.com.jackphotos.widget.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName PhotoPagerAdapter
 * @Description 预览图片adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private Context context;
    private List<PhotoView> photoViewList = new ArrayList<>(4);
    private List<Photo> photoList;
    private OnItemClickListener onItemClickListener;
    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();

    public PhotoPagerAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
        initPhotoViewList();
    }

    private void initPhotoViewList() {
        for (int i = 0; i < 4; i++) {
            PhotoView imageView = new PhotoView(context);
            imageView.setAdjustViewBounds(true);
            photoViewList.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return photoList == null ? 0 : photoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof PhotoView) {
            PhotoView photoView = (PhotoView) object;
            photoView.setImageDrawable(null);
            photoViewList.add(photoView);
            container.removeView(photoView);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoView photoView = photoViewList.remove(0);
        final Photo photo = photoList.get(position);
        container.addView(photoView);
        if (photo.isGif()) {
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(context).load(isAndroidQ ? photo.getUri() : photo.getPath())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).override(720, 1080)
                .into(photoView);
        } else {
            Glide.with(context).asBitmap()
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .load(isAndroidQ ? photo.getUri() : photo.getPath()).into(new SimpleTarget<Bitmap>(720, 1080) {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    int bw = resource.getWidth();
                    int bh = resource.getHeight();
                    if (bw > 4096 || bh > 4096) {
                        Bitmap bitmap = ImageUtils.zoomBitmap(resource, 4096, 4096);
                        setBitmap(photoView, bitmap);
                    } else {
                        setBitmap(photoView, resource);
                    }
                }
            });
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

    private void setBitmap(PhotoView photoView, Bitmap bitmap) {
        photoView.setImageBitmap(bitmap);
        if (bitmap != null) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int viewWidth = photoView.getWidth();
            int viewHeight = photoView.getHeight();
            if (bitmapWidth != 0 && bitmapHeight != 0 && viewWidth != 0 && viewHeight != 0) {
                if (1.0f * bitmapHeight / bitmapWidth > 1.0f * viewHeight / viewWidth) {
                    photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    float offset = (1.0f * bitmapHeight * viewWidth / bitmapWidth - viewHeight) / 2;
                    adjustOffset(photoView, offset);
                } else {
                    photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        }
    }

    private void adjustOffset(PhotoView view, float offset) {
        PhotoViewAttacher attacher = view.getAttacher();
        try {
            Field field = PhotoViewAttacher.class.getDeclaredField("mBaseMatrix");
            field.setAccessible(true);
            Matrix matrix = (Matrix) field.get(attacher);
            matrix.postTranslate(0, offset);
            Method method = PhotoViewAttacher.class.getDeclaredMethod("resetMatrix");
            method.setAccessible(true);
            method.invoke(attacher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position, Photo photo);
    }
}
