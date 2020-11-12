package ysn.com.jackphotos.widget.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    private OnItemClickListener onItemClickListener;

    private RecyclerView recyclerView;

    public PreviewAdapter(Context context, ArrayList<Photo> datas) {
        this.context = context;
        this.datas = datas;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.jack_item_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Photo photo = datas.get(position);
        if (isAndroidQ) {
            ImageUtils.loadImageAsBitmap(context, photo.getThumbnailsUri(), holder.photoImageView);
        } else {
            ImageUtils.loadImageAsBitmap(context, photo.getThumbnails(), holder.photoImageView);
        }

        holder.photoImageView.setShowFrame(photo.equals(previewPhoto));

        holder.videoIconImageView.setVisibility(photo.isVideo() ? View.VISIBLE : View.GONE);
        holder.gifTagImageView.setVisibility(photo.isGif() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(photo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setNewDatas(ArrayList<Photo> newDatas, Photo previewPhoto) {
        this.datas = newDatas;
        this.previewPhoto = previewPhoto;
        notifyDataSetChanged();
        scrollToPosition(previewPhoto);
    }

    public PreviewAdapter bindRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    public void scrollToPosition(@NonNull Photo previewPhoto) {
        for (int i = 0; i < datas.size(); i++) {
            if (previewPhoto.equals(datas.get(i))) {
                recyclerView.smoothScrollToPosition(i);
            }
        }
    }

    public PreviewAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public interface OnItemClickListener {

        /**
         * 图片点击事件回调
         */
        void onItemClick(Photo photo);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        FrameImageView photoImageView;
        ImageView videoIconImageView;
        ImageView gifTagImageView;

        ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.jack_preview_item_photo);
            videoIconImageView = itemView.findViewById(R.id.jack_preview_item_video_icon);
            gifTagImageView = itemView.findViewById(R.id.jack_preview_item_selected_gif_tag);
        }
    }
}
