package com.cyk.spring.ioc.io;

import com.cyk.spring.ioc.exception.IllegalURIException;
import com.cyk.spring.ioc.io.scan.FileScanner;
import com.cyk.spring.ioc.io.scan.JarFileScanner;
import com.cyk.spring.ioc.utils.ClassUtils;
import com.cyk.spring.ioc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * A simple classpath scan works both in directory and jar
 * <a href="https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection#58773038">
 *     Can you find all classes in a package using reflection?</a>
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class ResourceResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceResolver.class);

    private final String basePackage;
    private final FileScanner defaultScanner = new FileScanner();
    private final JarFileScanner jarFileScanner = new JarFileScanner();

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Scan list.
     *
     * @return the list
     */
    public List<String> scan() {
        return scan(resource -> {
            String name = resource.getName();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6)
                        .replace("/", ".")
                        .replace("\\", ".");
            }
            return null;
        });
    }

    /**
     * Scan list.
     *
     * @param <R>    the type parameter
     * @param mapper the mapper
     * @return the list
     */
    public <R> List<R> scan(Function<Resource, R> mapper) {
        String basePackagePath = basePackage.replace(".", "/");
        try {
            List<R> collector = new ArrayList<>();
            scan0(basePackagePath, collector, mapper);
            return collector;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <R> void scan0(String basePackagePath, List<R> collector, Function<Resource, R> mapper)
            throws IOException, URISyntaxException {
        LOGGER.debug("开始扫描路径: {}", basePackagePath);
        Enumeration<URL> urls = ClassUtils.getContextClassLoader(getClass()).getResources(basePackagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            URI uri = url.toURI();
            String uriStr = StringUtils.removeTrailingSlash(URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8));
            String uriBaseStr = uriStr.substring(0, uriStr.length() - basePackagePath.length());

            IFileScanner fileScanner;
            if (uriStr.startsWith("jar:")) {
                fileScanner = jarFileScanner;
            } else if (uriStr.startsWith("file:")) {
                fileScanner = defaultScanner;
            } else throw new IllegalURIException("Illegal uri!");
            Set<Resource> resourceStream = fileScanner.doFileScan(basePackagePath, uriBaseStr, uri);
            resourceStream.forEach(resource -> {
                R r = mapper.apply(resource);
                if (r != null) {
                    collector.add(r);
                }
            });
        }
    }
}
