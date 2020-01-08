package ysn.com.jackphotos.model.mode;

/**
 * @Author yangsanning
 * @ClassName IEnum
 * @Description 一句话概括作用
 * @Date 2020/1/8
 * @History 2020/1/8 author: description:
 */
public interface IEnum <T extends Enum<T>> {

    T getParent();
}
