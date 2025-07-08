package com.cyk.spring.web;

import com.cyk.spring.ioc.context.ApplicationContext;
import com.cyk.spring.ioc.utils.StringUtils;
import com.cyk.spring.web.exception.ApplicationContextInitException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化和创建ioc容器
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class ApplicationContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        String configClassName = servletContext.getInitParameter("configClassName");
        if (StringUtils.isEmpty(configClassName)) {
            throw new ApplicationContextInitException("Missing init parameter: configClassName");
        }
        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
        } catch (ClassNotFoundException e) {
            throw new ApplicationContextInitException("Failed to load configuration class: " + configClassName, e);
        }
        WebContext.initialize(configClass, servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.warn("Servlet context destroyed, closing application context...");
        ServletContext servletContext = sce.getServletContext();
        if (servletContext.getAttribute("applicationContext") instanceof ApplicationContext applicationContext) {
            applicationContext.close();
        }
    }


}
