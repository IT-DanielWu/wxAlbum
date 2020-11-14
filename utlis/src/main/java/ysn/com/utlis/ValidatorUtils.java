package ysn.com.utlis;

import java.util.List;

/**
 * @Author yangsanning
 * @ClassName ValidatorUtils
 * @Description 校验工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class ValidatorUtils {

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(final CharSequence cs) {
        if (isNull(cs) || (cs.toString().trim().length()) == 0) {
            return true;
        }
        for (int i = 0; i < cs.toString().length(); i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isEmptyList(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmptyList(List list) {
        return !isEmptyList(list);
    }
}
