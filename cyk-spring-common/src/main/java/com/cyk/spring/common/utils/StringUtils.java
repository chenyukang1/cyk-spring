package com.cyk.spring.common.utils;

/**
 * The class StringUtils.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class StringUtils {

    /**
     * Remove leading slash string.
     *
     * @param path the path
     * @return the string
     */
    public static String removeLeadingSlash(String path) {
        if (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * Remove trailing slash string.
     *
     * @param path the path
     * @return the string
     */
    public static String removeTrailingSlash(String path) {
        if (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
