package ysn.com.jackphotos.widget.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import ysn.com.jackphotos.R;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.ImageUtils;
import ysn.com.jackphotos.widget.component.FrameImageView;

/**
 * @Author yangsanning
 * @ClassName PhotosAdapter
 * @Description 相片adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();

    private ArrayList<Photo> datas;
    private Photo previewPhoto;

    private OnItemClickListener onPhotosMultiListener;

    public PreviewAdapter(Context context, ArrayList<Photo> datas) {
        this.context = context;
        this.datas = datas;

        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Photo photo = datas.get(position);
        if (isAndroidQ) {
            ImageUtils.loadImageAsBitmap(context, photo.getUri(), holder.photoImageView);
        } else {
            ImageUtils.loadImageAsBitmap(context, photo.getPath(), holder.photoImageView);
        }

        holder.photoImageView.setShowFrame(photo.equals(previewPhoto));
        holder.gifTagImageView.setVisibility(photo.isGif() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPhotosMultiListener != null) {
                    onPhotosMultiListener.onItemClick(photo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setNewDatas(ArrayList<Photo> newDatas) {
        this.datas = newDatas;
        notifyDataSetChanged();
    }

    public void selectPhoto(Photo photo) {
        if (datas.contains(photo)) {
            previewPhoto = photo;
        } else {
            previewPhoto = null;
        }
        notifyDataSetChanged();
    }

    public void setOnPhotosMultiListener(OnItemClickListener onPhotosMultiListener) {
        this.onPhotosMultiListener = onPhotosMultiListener;
    }

    public interface OnItemClickListener {

        /**
         * 图片点击事件回调
         */
        void onItemClick(Photo photo);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        FrameImageView photoImageView;
        ImageView gifTagImageView;

        ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.preview_item_photo);
            gifTagImageView = itemView.findViewById(R.id.preview_item_selected_gif_tag);
        }
    }
}
