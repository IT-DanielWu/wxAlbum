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
import ysn.com.wxalbum.model.bean.AlbumFolder;
import ysn.com.utlis.AndroidVersionUtils;
import ysn.com.utlis.ImageUtils;
import ysn.com.utlis.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName AlbumFolderAdapter
 * @Description 相册文件夹Adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class AlbumFolderAdapter extends RecyclerView.Adapter<AlbumFolderAdapter.ViewHolder> {

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
    private Context context;
    private ArrayList<AlbumFolder> albumFolderList;
    private LayoutInflater layoutInflater;

    private int selectPosition;
    private OnFolderSelectListener onFolderSelectListener;

    public AlbumFolderAdapter(Context context, ArrayList<AlbumFolder> albumFolderList) {
        this.context = context;
        this.albumFolderList = albumFolderList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_album_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AlbumFolder albumFolder = albumFolderList.get(position);
        holder.folderNameTextView.setText(albumFolder.getName());
        holder.selectImageView.setVisibility(selectPosition == position ? View.VISIBLE : View.GONE);

        ArrayList<Album> photoList = albumFolder.getPhotoList();
        if (ValidatorUtils.isEmptyList(photoList)) {
            holder.photoCountTextView.setText(context.getString(R.string.album_format_image_count, 0));
            holder.thumbnailsImageView.setImageBitmap(null);
        } else {
            holder.photoCountTextView.setText(context.getString(R.string.album_format_image_count, photoList.size()));
            if (isAndroidQ) {
                ImageUtils.loadImageAsBitmap(context, albumFolder.getThumbnailsUri(), holder.thumbnailsImageView);
            } else {
                ImageUtils.loadImageAsBitmap(context, albumFolder.getThumbnails(), holder.thumbnailsImageView);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                notifyDataSetChanged();
                if (onFolderSelectListener != null) {
                    onFolderSelectListener.onFolderSelect(albumFolder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFolderList == null ? 0 : albumFolderList.size();
    }

    public void setOnFolderSelectListener(OnFolderSelectListener listener) {

        this.onFolderSelectListener = listener;
    }


    public interface OnFolderSelectListener {

        /**
         * 选择文件夹回调
         */
        void onFolderSelect(AlbumFolder photoFolder);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnailsImageView;
        ImageView selectImageView;
        TextView folderNameTextView;
        TextView photoCountTextView;

        ViewHolder(View itemView) {
            super(itemView);
            thumbnailsImageView = itemView.findViewById(R.id.album_folder_item_thumbnails);
            selectImageView = itemView.findViewById(R.id.album_folder_item_select);
            folderNameTextView = itemView.findViewById(R.id.album_folder_item_folder_name);
            photoCountTextView = itemView.findViewById(R.id.album_folder_item_photo_count);
        }
    }
}
