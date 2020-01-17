package ysn.com.demo.jackphotos;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import ysn.com.jackphotos.utils.AndroidVersionUtils;
import ysn.com.jackphotos.utils.ImageUtils;
import ysn.com.jackphotos.utils.UriUtils;

/**
 * @Author yangsanning
 * @ClassName ResultAdapter
 * @Description 一句话概括作用
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class ResultAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private boolean isAndroidQ = AndroidVersionUtils.isAndroidQ();

    public ResultAdapter() {
        super(R.layout.item_result);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView imageView = helper.getView(R.id.result_item_image);
        if (isAndroidQ) {
            ImageUtils.loadImage(mContext, UriUtils.getImageContentUri(mContext, item), imageView);
        } else {
            ImageUtils.loadImage(mContext, item, imageView);
        }
    }
}
