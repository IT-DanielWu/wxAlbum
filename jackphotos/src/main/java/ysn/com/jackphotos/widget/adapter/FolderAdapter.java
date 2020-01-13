package ysn.com.jackphotos.widget.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ysn.com.jackphotos.R;
import ysn.com.jackphotos.model.bean.Photo;
import ysn.com.jackphotos.model.bean.PhotoFolder;
import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.ImageUtils;
import ysn.com.jackphotos.utils.ValidatorUtils;

/**
 * @Author yangsanning
 * @ClassName FolderAdapter
 * @Description 文件夹Adapter
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();
    private Context context;
    private ArrayList<PhotoFolder> photoFolderList;
    private LayoutInflater layoutInflater;

    private int selectPosition;
    private OnFolderSelectListener onFolderSelectListener;

    public FolderAdapter(Context context, ArrayList<PhotoFolder> photoFolderList) {
        this.context = context;
        this.photoFolderList = photoFolderList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PhotoFolder photoFolder = photoFolderList.get(position);
        holder.folderNameTextView.setText(photoFolder.getName());
        holder.selectImageView.setVisibility(selectPosition == position ? View.VISIBLE : View.GONE);

        ArrayList<Photo> photoList = photoFolder.getPhotoList();
        if (ValidatorUtils.isEmptyList(photoList)) {
            holder.photoCountTextView.setText(context.getString(R.string.jack_photo_format_image_count, 0));
            holder.photoImageView.setImageBitmap(null);
        } else {
            holder.photoCountTextView.setText(context.getString(R.string.jack_photo_format_image_count, photoList.size()));
            if (isAndroidQ) {
                ImageUtils.loadImage(context, photoList.get(0).getUri(), holder.photoImageView);
            } else {
                ImageUtils.loadImage(context, photoList.get(0).getPath(), holder.photoImageView);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
                notifyDataSetChanged();
                if (onFolderSelectListener != null) {
                    onFolderSelectListener.onFolderSelect(photoFolder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoFolderList == null ? 0 : photoFolderList.size();
    }

    public void setOnFolderSelectListener(OnFolderSelectListener listener) {

        this.onFolderSelectListener = listener;
    }


    public interface OnFolderSelectListener {

        /**
         * 选择文件夹回调
         */
        void onFolderSelect(PhotoFolder photoFolder);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;
        ImageView selectImageView;
        TextView folderNameTextView;
        TextView photoCountTextView;

        ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.folder_item_photo);
            selectImageView = itemView.findViewById(R.id.folder_item_select);
            folderNameTextView = itemView.findViewById(R.id.folder_item_folder_name);
            photoCountTextView = itemView.findViewById(R.id.folder_item_photo_count);
        }
    }
}
