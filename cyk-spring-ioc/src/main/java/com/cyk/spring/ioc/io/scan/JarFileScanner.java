package com.cyk.spring.ioc.io.scan;

import com.cyk.spring.ioc.io.IFileScanner;
import com.cyk.spring.ioc.io.Resource;
import com.cyk.spring.ioc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class JarFileScanner.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class JarFileScanner implements IFileScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarFileScanner.class);

    @Override
    public Set<Resource> doFileScan(String basePackagePath, String uriBaseStr, URI uri) throws IOException {
        Set<Resource> set;
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, new HashMap<>())) {
            String baseDir = StringUtils.removeTrailingSlash(uriBaseStr);
            Path root = fileSystem.getPath(basePackagePath);
            try (Stream<Path> pathStream = Files.walk(root)) {
                set = pathStream.filter(Files::isRegularFile).map(file -> {
                    Resource resource = new Resource(StringUtils.removeLeadingSlash(file.toString()), baseDir);
                    LOGGER.debug("找到资源: {}", resource);
                    return resource;
                }).collect(Collectors.toSet());
            }
        }

        return set;
    }
}
