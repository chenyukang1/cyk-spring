package com.cyk.spring.ioc.utils;

import com.cyk.spring.ioc.exception.ClassPathException;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * The class ClassPathUtils
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class ClassPathUtils {

    public static <T> T readInputStream(String path, Function<InputStream, T> callback) throws ClassPathException {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream is = ClassUtils.getContextClassLoader(ClassPathUtils.class).getResourceAsStream(path)) {
            return callback.apply(is);
        } catch (IOException e) {
            throw new ClassPathException("Error reading input stream from path: " + path, e);
        } catch (Exception e) {
            throw new ClassPathException("Unexpected error while reading input stream from path: " + path, e);
        }
    }
}
