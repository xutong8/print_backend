package com.zju.vis.print_backend.Utils;

public class StringUtil {
    // 判断字符串是否为空
    public static boolean isBlank(String content) {
        if (null == content || "".equals(content)) {
            return true;
        }
        return false;
    }
}
