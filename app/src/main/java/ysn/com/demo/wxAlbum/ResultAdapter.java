package ysn.com.demo.wxAlbum;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import ysn.com.wxalbum.model.bean.Album;
import ysn.com.wxalbum.utils.AndroidVersionUtils;
import ysn.com.wxalbum.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName ResultAdapter
 * @Description 一句话概括作用
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class ResultAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();

    public ResultAdapter() {
        super(R.layout.item_result);
    }

    @Override
    protected void convert(BaseViewHolder helper, Album item) {
        ImageView imageView = helper.getView(R.id.result_item_image);
        if (isAndroidQ) {
            ImageUtils.loadImage(mContext,  item.getThumbnailsUri(), imageView);
        } else {
            ImageUtils.loadImage(mContext,  item.getThumbnails(), imageView);
        }
    }
}
