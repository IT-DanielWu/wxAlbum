package ysn.com.wxalbum.widget.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import ysn.com.wxalbum.R;
import ysn.com.wxalbum.model.bean.Album;
import ysn.com.utlis.AndroidVersionUtils;
import ysn.com.utlis.ImageUtils;
import ysn.com.wxalbum.widget.component.FrameImageView;

/**
 * @Author yangsanning
 * @ClassName AlbumPreviewAdapter
 * @Description 相册预览adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumPreviewAdapter extends RecyclerView.Adapter<AlbumPreviewAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();

    private ArrayList<Album> albumList;
    private Album previewAlbum;

    private OnItemClickListener onItemClickListener;

    private RecyclerView recyclerView;

    public AlbumPreviewAdapter(Context context, ArrayList<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_album_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Album album = albumList.get(position);
        if (isAndroidQ) {
            ImageUtils.loadImageAsBitmap(context, album.getThumbnailsUri(), holder.thumbnailsImageView);
        } else {
            ImageUtils.loadImageAsBitmap(context, album.getThumbnails(), holder.thumbnailsImageView);
        }

        holder.thumbnailsImageView.setShowFrame(album.equals(previewAlbum));

        holder.videoIconImageView.setVisibility(album.isVideo() ? View.VISIBLE : View.GONE);
        holder.gifTagImageView.setVisibility(album.isGif() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(album);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList == null ? 0 : albumList.size();
    }

    public void setNewDatas(ArrayList<Album> newDatas, Album previewPhoto) {
        this.albumList = newDatas;
        this.previewAlbum = previewPhoto;
        notifyDataSetChanged();
        scrollToPosition(previewPhoto);
    }

    public AlbumPreviewAdapter bindRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    public void scrollToPosition(@NonNull Album previewPhoto) {
        for (int i = 0; i < albumList.size(); i++) {
            if (previewPhoto.equals(albumList.get(i))) {
                recyclerView.smoothScrollToPosition(i);
            }
        }
    }

    public AlbumPreviewAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

    public interface OnItemClickListener {

        /**
         * 图片点击事件回调
         */
        void onItemClick(Album photo);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        FrameImageView thumbnailsImageView;
        ImageView videoIconImageView;
        ImageView gifTagImageView;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnailsImageView = itemView.findViewById(R.id.album_preview_item_thumbnails);
            videoIconImageView = itemView.findViewById(R.id.album_preview_item_video_icon);
            gifTagImageView = itemView.findViewById(R.id.album_preview_item_selected_gif_tag);
        }
    }
}
