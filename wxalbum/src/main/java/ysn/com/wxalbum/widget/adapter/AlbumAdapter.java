package ysn.com.wxalbum.widget.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ysn.com.wxalbum.R;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.utils.AndroidVersionUtils;
import ysn.com.wxalbum.utils.ImageUtils;
import ysn.com.wxalbum.utils.TimeUtils;
import ysn.com.wxalbum.utils.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumAdapter
 * @Description 相册adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PHOTO = 2;

    private Context context;
    private int maxSelectCount;
    private boolean isSingle, canPreview;

    private LayoutInflater layoutInflater;
    private ArrayList<Album> albumList;
    private ArrayList<Album> selectedAlbumList = new ArrayList<>();

    private OnAlbumMultiListener onPhotosMultiListener;

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
    private boolean useCamera;

    /**
     * @param maxSelectCount 图片的最大选择数量, 小于等于0时, 不限数量, isSingle为false时才有用
     * @param isSingle       是否单选
     * @param canPreview     是否点击放大图片查看
     */
    public AlbumAdapter(Context context, int maxSelectCount, boolean isSingle, boolean canPreview) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
        this.maxSelectCount = maxSelectCount;
        this.isSingle = isSingle;
        this.canPreview = canPreview;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PHOTO) {
            return new ViewHolder(layoutInflater.inflate(R.layout.item_album, parent, false));
        }
        return new ViewHolder(layoutInflater.inflate(R.layout.jack_item_album_camera, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            final int realPosition = getRealPosition(position);
            final Album album = albumList.get(realPosition);
            if (isAndroidQ) {
                ImageUtils.loadImageAsBitmap(context, album.getThumbnailsUri(), holder.thumbnailsImageView);
            } else {
                ImageUtils.loadImageAsBitmap(context, album.getThumbnails(), holder.thumbnailsImageView);
            }

            if (album.isVideo()) {
                holder.videoLayout.setVisibility(View.VISIBLE);
                holder.videoTimeTextView.setText(TimeUtils.formatMinutesSeconds(album.getDuration()));
            } else {
                holder.videoLayout.setVisibility(View.GONE);
            }

            upSelectPhotoUi(holder, selectedAlbumList.contains(album));

            holder.gifTagImageView.setVisibility(album.isGif() ? View.VISIBLE : View.GONE);

            holder.selectedTagImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(holder, album);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canPreview) {
                        if (onPhotosMultiListener != null) {
                            onPhotosMultiListener.onAlbumItemClick(album, realPosition);
                        }
                    } else {
                        select(holder, album);
                    }
                }
            });
        } else if (getItemViewType(position) == TYPE_CAMERA) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPhotosMultiListener != null) {
                        onPhotosMultiListener.onCameraClick();
                    }
                }
            });
        }
    }

    private int getRealPosition(int position) {
        return useCamera ? position - 1 : position;
    }

    /**
     * 更新选中未选择ui
     */
    private void upSelectPhotoUi(ViewHolder holder, boolean isSelect) {
        if (isSelect) {
            holder.selectedTagImageView.setImageResource(R.drawable.album_ic_selected_tag);
            holder.photoMaskImageView.setAlpha(0.5f);
        } else {
            holder.selectedTagImageView.setImageResource(R.drawable.album_ic_un_selected_tag);
            holder.photoMaskImageView.setAlpha(0.2f);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (useCamera && position == 0) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    private void select(ViewHolder holder, Album image) {
        if (selectedAlbumList.contains(image)) {
            // 如果图片已经选中，就取消选中
            unSelectPhoto(image);
            upSelectPhotoUi(holder, false);
        } else if (isSingle) {
            //如果是单选，就先清空已经选中的图片，再选中当前图片
            clearSingleSelect();
            selectPhoto(image);
            upSelectPhotoUi(holder, true);
        } else if (maxSelectCount <= 0 || selectedAlbumList.size() < maxSelectCount) {
            //如果不限制图片的选中数量，或者图片的选中数量
            // 还没有达到最大限制，就直接选中当前图片。
            selectPhoto(image);
            upSelectPhotoUi(holder, true);
        }
    }

    /**
     * 取消选中图片
     */
    private void unSelectPhoto(Album photo) {
        selectedAlbumList.remove(photo);
        if (onPhotosMultiListener != null) {
            onPhotosMultiListener.onAlbumSelectChange(photo, false, selectedAlbumList.size());
        }
    }

    /**
     * 选中图片
     */
    private void selectPhoto(Album photo) {
        selectedAlbumList.add(photo);
        if (onPhotosMultiListener != null) {
            onPhotosMultiListener.onAlbumSelectChange(photo, true, selectedAlbumList.size());
        }
    }

    /**
     * 清空单选
     */
    private void clearSingleSelect() {
        if (albumList != null && selectedAlbumList.size() == 1) {
            int index = albumList.indexOf(selectedAlbumList.get(0));
            selectedAlbumList.clear();
            if (index != -1) {
                notifyItemChanged(useCamera ? index + 1 : index);
            }
        }
    }

    @Override
    public int getItemCount() {
        return useCamera ? getImageCount() + 1 : getImageCount();
    }

    private int getImageCount() {
        return albumList == null ? 0 : albumList.size();
    }

    public ArrayList<Album> getData() {
        return albumList;
    }

    public void refresh(ArrayList<Album> photoList, boolean useCamera) {
        this.albumList = photoList;
        this.useCamera = useCamera;
        notifyDataSetChanged();
    }

    public Album getFirstVisibleItem(int firstVisibleItemPosition) {
        if (ValidatorUtils.isNotEmptyList(albumList)) {
            return albumList.get(firstVisibleItemPosition < 0 ? getRealPosition(firstVisibleItemPosition) : 0);
        }
        return null;
    }

    public void setSelectedAlbumList(ArrayList<String> selectedPhotoPathList) {
        if (albumList != null && selectedPhotoPathList != null) {
            for (String selectedPhotoPath : selectedPhotoPathList) {
                if (isSelectedFull()) {
                    return;
                }
                for (Album photo : albumList) {
                    if (selectedPhotoPath.equals(photo.getFilePath())) {
                        if (!this.selectedAlbumList.contains(photo)) {
                            this.selectedAlbumList.add(photo);
                        }
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private boolean isSelectedFull() {
        if (isSingle && selectedAlbumList.size() == 1) {
            return true;
        }
        return maxSelectCount > 0 && selectedAlbumList.size() == maxSelectCount;
    }

    public ArrayList<Album> getSelectedAlbumList() {
        return selectedAlbumList;
    }

    public void setOnPhotosMultiListener(OnAlbumMultiListener onPhotosMultiListener) {
        this.onPhotosMultiListener = onPhotosMultiListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnailsImageView;
        ImageView selectedTagImageView;
        ImageView photoMaskImageView;
        View videoLayout;
        TextView videoTimeTextView;
        ImageView gifTagImageView;
        ImageView cameraImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailsImageView = itemView.findViewById(R.id.album_item_thumbnails);
            selectedTagImageView = itemView.findViewById(R.id.album_item_selected_tag);
            photoMaskImageView = itemView.findViewById(R.id.album_item_photo_mask);
            videoLayout = itemView.findViewById(R.id.album_item_video_layout);
            videoTimeTextView = itemView.findViewById(R.id.album_item_video_time);
            gifTagImageView = itemView.findViewById(R.id.album_item_selected_gif_tag);

            cameraImageView = itemView.findViewById(R.id.album_camera_item_camera);
        }
    }

    public interface OnAlbumMultiListener {

        /**
         * 选中状态更改的回调
         */
        void onAlbumSelectChange(Album photo, boolean isSelect, int selectedCount);

        /**
         * 图片点击事件回调
         */
        void onAlbumItemClick(Album photo, int position);

        /**
         * 相机点击回调
         */
        void onCameraClick();
    }
}
