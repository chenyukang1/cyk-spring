package com.cyk.spring.boot;

import com.cyk.spring.boot.exception.StartFailException;
import com.cyk.spring.web.ConfigLoader;
import jakarta.servlet.ServletContainerInitializer;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * The class BootApplication
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
public class SpringApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringApplication.class);

    private SpringApplication() {
        // Private constructor to prevent instantiation
    }

    public static void run(Class<?> configClass) {
        run(configClass, null);
    }

    public static void run(Class<?> configClass, String[] args) {
        StartOption option;
        try {
            option = prepareStartOption();
        } catch (IOException e) {
            logger.error("Failed to prepare start options", e);
            throw new StartFailException("Failed to prepare start options", e);
        }

        int port = Integer.parseInt(option.getProperties().getProperty("server.port", "8080"));
        Server server = startTomcat(option, port, configClass);

        server.await();
    }

    private static StartOption prepareStartOption() throws IOException {
        // 判定是否从jar/war启动:
        String jarFile = SpringApplication.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        boolean isJarFile = jarFile.endsWith(".war") || jarFile.endsWith(".jar");
        // 定位webapp根目录:
        String webDir = isJarFile ? "tmp-webapp" : "src/main/webapp";
        if (isJarFile) {
            // 解压到tmp-webapp:
            Path baseDir = Paths.get(webDir).normalize().toAbsolutePath();
            if (Files.isDirectory(baseDir)) {
                Files.delete(baseDir);
            }
            Files.createDirectories(baseDir);
            try (JarFile jar = new JarFile(jarFile)) {
                List<JarEntry> entries = jar.stream().sorted(Comparator.comparing(JarEntry::getName)).toList();
                for (JarEntry entry : entries) {
                    Path res = baseDir.resolve(entry.getName());
                    if (!entry.isDirectory()) {
                        Files.createDirectories(res.getParent());
                        Files.copy(jar.getInputStream(entry), res);
                    }
                }
            }
            // JVM退出时自动删除tmp-webapp:
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try (Stream<Path> stream = Files.walk(baseDir)) {
                    stream.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(file -> {
                                if (!file.delete()) {
                                    logger.warn("Failed to delete file: {}", file.getAbsolutePath());
                                }
                            });
                } catch (IOException e) {
                    logger.error("Error while deleting temporary webapp files", e);
                }
            }));
        }

        return new StartOption(webDir, isJarFile ? "tmp-webapp" : "target/classes", ConfigLoader.load());
    }

    private static Server startTomcat(StartOption option, int port, Class<?> configClass) {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector().setThrowOnFailure(true); // Initialize the connector

        Context ctx = tomcat.addWebapp("", new File(option.getWebPath()).getAbsolutePath());
        WebResourceRoot root = new StandardRoot(ctx);
        root.addPreResources(new DirResourceSet(root, "/WEB-INF/classes", new File(option.getBasePath()).getAbsolutePath(), "/"));
        ctx.setResources(root);

        ServletContainerInitializer initializer = new ApplicationContextInitializer(configClass);
        ctx.addServletContainerInitializer(initializer, new HashSet<>());

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            logger.error("Failed to start Tomcat server", e);
            throw new StartFailException("Failed to start Tomcat server", e);
        }
        // Initialize the application context with the provided configuration class
        logger.info("Spring Application started on port {}", port);

        return tomcat.getServer();
    }

    static class StartOption {

        private String webPath;

        private String basePath;

        private Properties properties;

        public StartOption(String webPath, String basePath, Properties properties) {
            this.webPath = webPath;
            this.basePath = basePath;
            this.properties = properties;
        }

        public String getWebPath() {
            return webPath;
        }

        public void setWebPath(String webPath) {
            this.webPath = webPath;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }
    }
}
