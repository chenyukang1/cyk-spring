package com.cyk.spring.web;

import freemarker.cache.TemplateLoader;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * The class ServletTemplateLoader
 *
 * @author yukang.chen
 * @date 2025/6/28
 */
public class ServletTemplateLoader implements TemplateLoader {

    private static final Logger logger = LoggerFactory.getLogger(ServletTemplateLoader.class);

    private final ServletContext servletContext;

    private final String subDirPath;

    public ServletTemplateLoader(ServletContext servletContext, String subDirPath) {
        Objects.requireNonNull(servletContext);
        Objects.requireNonNull(subDirPath);

        subDirPath = subDirPath.replace('\\', '/');
        if (!subDirPath.endsWith("/")) {
            subDirPath += "/";
        }
        if (!subDirPath.startsWith("/")) {
            subDirPath = "/" + subDirPath;
        }
        this.subDirPath = subDirPath;
        this.servletContext = servletContext;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        String fullPath = subDirPath + name;

        try {
            String realPath = servletContext.getRealPath(fullPath);
            logger.debug("load template {}: real path: {}", name, realPath);
            if (realPath != null) {
                File file = new File(realPath);
                if (file.canRead() && file.isFile()) {
                    return file;
                }
            }
        } catch (SecurityException e) {
            ;// ignore
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        if (templateSource instanceof File) {
            return ((File) templateSource).lastModified();
        }
        return 0;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if (templateSource instanceof File) {
            return new InputStreamReader(new FileInputStream((File) templateSource), encoding);
        }
        throw new IOException("File not found.");
    }

    @Override
    public void closeTemplateSource(Object o) throws IOException {

    }
}
