package com.cyk.spring.boot;

import com.cyk.spring.web.WebContext;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * The class ApplicationContextInitializer
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
public class ApplicationContextInitializer implements ServletContainerInitializer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextInitializer.class);
    private final Class<?> configClass;

    public ApplicationContextInitializer(Class<?> configClass) {
        this.configClass = configClass;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) {
        log.info("Servlet container starting.");
        servletContext.setRequestCharacterEncoding("UTF-8");
        servletContext.setResponseCharacterEncoding("UTF-8");

        WebContext.initialize(configClass, servletContext);
    }
}
