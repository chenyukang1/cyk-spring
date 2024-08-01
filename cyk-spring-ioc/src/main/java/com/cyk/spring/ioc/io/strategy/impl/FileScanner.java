package com.cyk.spring.ioc.io.strategy.impl;

import com.cyk.spring.common.utils.StringUtils;
import com.cyk.spring.ioc.io.Resource;
import com.cyk.spring.ioc.io.strategy.IFileScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class FileScanner.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class FileScanner implements IFileScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanner.class);

    @Override
    public Set<Resource> doFileScan(String basePackagePath, String uriBaseStr, URI uri) throws IOException {
        if (!uriBaseStr.startsWith("file:")) throw new RuntimeException("Illegal uriBaseStr: " + uriBaseStr);
        String baseDir = StringUtils.removeTrailingSlash(uriBaseStr.substring(5));
        Path root = Paths.get(uri);
        Set<Resource> set;
        try (Stream<Path> pathStream = Files.walk(root)) {
            set = pathStream.filter(Files::isRegularFile).map(file -> {
                String path = file.toString();
                String name = StringUtils.removeLeadingSlash(path.substring(baseDir.length()));
                Resource resource = new Resource(name, "file:" + path);
                LOGGER.debug("找到资源: {}", resource);
                return resource;
            }).collect(Collectors.toSet());
        }

        return set;
    }
}
