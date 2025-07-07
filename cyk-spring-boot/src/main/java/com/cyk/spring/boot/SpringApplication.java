package com.cyk.spring.boot;

import com.cyk.spring.web.ConfigLoader;
import jakarta.servlet.ServletContainerInitializer;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;

/**
 * The class BootApplication
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
public class SpringApplication {

    private SpringApplication() {
        // Private constructor to prevent instantiation
    }

    public static void run(Class<?> configClass) {
        run(configClass, null);
    }

    public static void run(Class<?> configClass, String[] args) {
        Properties properties = ConfigLoader.load();
        int port = Integer.parseInt(properties.getProperty("server.port", "8080"));
        String webPath = properties.getProperty("cyk.spring.web.path", "8080");
        String basePath = properties.getProperty("cyk.spring.base.path", "8080");


        Tomcat tomcat = new Tomcat();

        Context ctx = tomcat.addWebapp("", new File(webPath).getAbsolutePath());
        WebResourceRoot root = new StandardRoot(ctx);
        root.addPreResources(new DirResourceSet(root, "/WEB-INF/classes", new File(basePath).getAbsolutePath(), "/"));
        ctx.setResources(root);

        ServletContainerInitializer initializer = new ApplicationContextInitializer(configClass, properties);
        ctx.addServletContainerInitializer(initializer, new HashSet<>());

        tomcat.setPort(port);
        tomcat.getConnector().setThrowOnFailure(true); // Initialize the connector
        // Initialize the application context with the provided configuration class
        ApplicationContextInitializer initializer = new ApplicationContextInitializer(configClass, properties);
    }
}
