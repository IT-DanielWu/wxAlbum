package ysn.com.wxalbum.model.mode;

import ysn.com.view.cropimageview.mode.CropMode;

/**
 * @Author yangsanning
 * @ClassName AlbumPhotoCropMode
 * @Description 相册相片的裁剪模式
 * @Date 2020/1/8
 * @History 2020/1/8 author: description:
 */
public enum AlbumPhotoCropMode implements IEnum<CropMode> {

    /**
     * 不使用裁剪
     */
    NO_USE(10000),

    /**
     * 采用系统截图
     */
    SYSTEM(666),

    /**
     * 正方形
     */
    SQUARE(0),

    /**
     * 圆
     */
    CIRCLE(1),

    /**
     * 正方形截取
     */
    CIRCLE_SQUARE(2),

    /**
     * 满屏
     */
    FIT_IMAGE(3),

    /**
     * 4:3
     */
    RATIO_4_3(4),

    /**
     * 3:4
     */
    RATIO_3_4(5),

    /**
     * 16:9
     */
    RATIO_16_9(6),

    /**
     * 9:16
     */
    RATIO_9_16(7),

    FREE(8),

    /**
     * 自定义
     */
    CUSTOM(9);

    public final int mode;

    AlbumPhotoCropMode(final int mode) {
        this.mode = mode;
    }

    @Override
    public CropMode getParent() {
        return CropMode.valueOf(this.name());
    }
}
