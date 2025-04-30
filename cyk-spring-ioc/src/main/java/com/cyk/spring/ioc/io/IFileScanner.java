package com.cyk.spring.ioc.io;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * The interface IFileScanner.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public interface IFileScanner {

    Set<Resource> doFileScan(String basePackagePath, String uriBaseStr, URI uri) throws IOException;
}
