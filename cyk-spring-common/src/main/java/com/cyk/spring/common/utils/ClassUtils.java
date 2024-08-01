package com.cyk.spring.common.utils;

/**
 * The class ClassUtils.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class ClassUtils {

    public static ClassLoader getContextClassLoader(Class<?> caller) {
        ClassLoader cl;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = caller.getClassLoader();
        }
        return cl;
    }
}
