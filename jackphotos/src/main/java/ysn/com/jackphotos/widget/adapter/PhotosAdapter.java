package ysn.com.jackphotos.widget.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ysn.com.jackphotos.R;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.ImageUtils;
import ysn.com.jackphotos.utils.TimeUtils;
import ysn.com.jackphotos.utils.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName PhotosAdapter
 * @Description 相片adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PHOTO = 2;

    private Context context;
    private int maxSelectCount;
    private boolean isSingle, canPreview;

    private LayoutInflater layoutInflater;
    private ArrayList<Photo> photoList;
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();

    private OnPhotosMultiListener onPhotosMultiListener;

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
    private boolean useCamera;

    /**
     * @param maxSelectCount 图片的最大选择数量, 小于等于0时, 不限数量, isSingle为false时才有用
     * @param isSingle       是否单选
     * @param canPreview     是否点击放大图片查看
     */
    public PhotosAdapter(Context context, int maxSelectCount, boolean isSingle, boolean canPreview) {
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
            return new ViewHolder(layoutInflater.inflate(R.layout.jack_item_photo, parent, false));
        }
        return new ViewHolder(layoutInflater.inflate(R.layout.jack_item_photo_camera, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            final int realPosition = getRealPosition(position);
            final Photo photo = photoList.get(realPosition);
            if (isAndroidQ) {
                ImageUtils.loadImageAsBitmap(context, photo.getThumbnailsUri(), holder.photoImageView);
            } else {
                ImageUtils.loadImageAsBitmap(context, photo.getThumbnails(), holder.photoImageView);
            }

            if (photo.isVideo()) {
                holder.videoLayout.setVisibility(View.VISIBLE);
                holder.videoTimeTextView.setText(TimeUtils.formatMinutesSeconds(photo.getDuration()));
            } else {
                holder.videoLayout.setVisibility(View.GONE);
            }

            upSelectPhotoUi(holder, selectedPhotoList.contains(photo));

            holder.gifTagImageView.setVisibility(photo.isGif() ? View.VISIBLE : View.GONE);

            holder.selectedTagImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(holder, photo);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canPreview) {
                        if (onPhotosMultiListener != null) {
                            onPhotosMultiListener.onPhotoItemClick(photo, realPosition);
                        }
                    } else {
                        select(holder, photo);
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
            holder.selectedTagImageView.setImageResource(R.drawable.jack_ic_selected_tag);
            holder.photoMaskImageView.setAlpha(0.5f);
        } else {
            holder.selectedTagImageView.setImageResource(R.drawable.jack_ic_un_selected_tag);
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

    private void select(ViewHolder holder, Photo image) {
        if (selectedPhotoList.contains(image)) {
            // 如果图片已经选中，就取消选中
            unSelectPhoto(image);
            upSelectPhotoUi(holder, false);
        } else if (isSingle) {
            //如果是单选，就先清空已经选中的图片，再选中当前图片
            clearSingleSelect();
            selectPhoto(image);
            upSelectPhotoUi(holder, true);
        } else if (maxSelectCount <= 0 || selectedPhotoList.size() < maxSelectCount) {
            //如果不限制图片的选中数量，或者图片的选中数量
            // 还没有达到最大限制，就直接选中当前图片。
            selectPhoto(image);
            upSelectPhotoUi(holder, true);
        }
    }

    /**
     * 取消选中图片
     */
    private void unSelectPhoto(Photo photo) {
        selectedPhotoList.remove(photo);
        if (onPhotosMultiListener != null) {
            onPhotosMultiListener.onPhotoSelectChange(photo, false, selectedPhotoList.size());
        }
    }

    /**
     * 选中图片
     */
    private void selectPhoto(Photo photo) {
        selectedPhotoList.add(photo);
        if (onPhotosMultiListener != null) {
            onPhotosMultiListener.onPhotoSelectChange(photo, true, selectedPhotoList.size());
        }
    }

    /**
     * 清空单选
     */
    private void clearSingleSelect() {
        if (photoList != null && selectedPhotoList.size() == 1) {
            int index = photoList.indexOf(selectedPhotoList.get(0));
            selectedPhotoList.clear();
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
        return photoList == null ? 0 : photoList.size();
    }

    public ArrayList<Photo> getData() {
        return photoList;
    }

    public void refresh(ArrayList<Photo> photoList, boolean useCamera) {
        this.photoList = photoList;
        this.useCamera = useCamera;
        notifyDataSetChanged();
    }

    public Photo getFirstVisibleItem(int firstVisibleItemPosition) {
        if (ValidatorUtils.isNotEmptyList(photoList)) {
            return photoList.get(firstVisibleItemPosition < 0 ? getRealPosition(firstVisibleItemPosition) : 0);
        }
        return null;
    }

    public void setSelectedPhotoList(ArrayList<String> selectedPhotoPathList) {
        if (photoList != null && selectedPhotoPathList != null) {
            for (String selectedPhotoPath : selectedPhotoPathList) {
                if (isSelectedFull()) {
                    return;
                }
                for (Photo photo : photoList) {
                    if (selectedPhotoPath.equals(photo.getFilePath())) {
                        if (!this.selectedPhotoList.contains(photo)) {
                            this.selectedPhotoList.add(photo);
                        }
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private boolean isSelectedFull() {
        if (isSingle && selectedPhotoList.size() == 1) {
            return true;
        }
        return maxSelectCount > 0 && selectedPhotoList.size() == maxSelectCount;
    }

    public ArrayList<Photo> getSelectedPhotoList() {
        return selectedPhotoList;
    }

    public void setOnPhotosMultiListener(OnPhotosMultiListener onPhotosMultiListener) {
        this.onPhotosMultiListener = onPhotosMultiListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;
        ImageView selectedTagImageView;
        ImageView photoMaskImageView;
        View videoLayout;
        TextView videoTimeTextView;
        ImageView gifTagImageView;
        ImageView cameraImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.jack_photo_item_photo);
            selectedTagImageView = itemView.findViewById(R.id.jack_photo_item_selected_tag);
            photoMaskImageView = itemView.findViewById(R.id.jack_photo_item_photo_mask);
            videoLayout = itemView.findViewById(R.id.jack_photo_item_video_layout);
            videoTimeTextView = itemView.findViewById(R.id.jack_photo_item_video_time);
            gifTagImageView = itemView.findViewById(R.id.jack_photo_item_selected_gif_tag);

            cameraImageView = itemView.findViewById(R.id.jack_photo_camera_item_camera);
        }
    }

    public interface OnPhotosMultiListener {

        /**
         * 选中状态更改的回调
         */
        void onPhotoSelectChange(Photo photo, boolean isSelect, int selectedCount);

        /**
         * 图片点击事件回调
         */
        void onPhotoItemClick(Photo photo, int position);

        /**
         * 相机点击回调
         */
        void onCameraClick();
    }
}
