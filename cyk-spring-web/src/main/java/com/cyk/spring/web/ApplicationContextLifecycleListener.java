package com.cyk.spring.web;

import com.cyk.spring.ioc.context.ApplicationContext;
import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.ioc.utils.StringUtils;
import com.cyk.spring.ioc.utils.YamlUtils;
import com.cyk.spring.web.context.AnnotationConfigWebApplicationContext;
import com.cyk.spring.web.context.WebApplicationContext;
import com.cyk.spring.web.exception.ApplicationContextInitException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * 初始化和创建ioc容器
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class ApplicationContextLifecycleListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextLifecycleListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        String configClassName = servletContext.getInitParameter("configClassName");
        if (StringUtils.isEmpty(configClassName)) {
            throw new ApplicationContextInitException("Missing init parameter: configClassName");
        }
        WebApplicationContext applicationContext = createApplicationContext(configClassName, servletContext);
        servletContext.setAttribute("applicationContext", applicationContext);

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        ServletRegistration.Dynamic dynamic = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dynamic.addMapping("/");
        dynamic.setLoadOnStartup(1);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.warn("Servlet context destroyed, closing application context...");
        ServletContext servletContext = sce.getServletContext();
        if (servletContext.getAttribute("applicationContext") instanceof ApplicationContext applicationContext) {
            applicationContext.close();
        }
    }

    private WebApplicationContext createApplicationContext(String configClassName, ServletContext servletContext) {
        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
        } catch (ClassNotFoundException e) {
            throw new ApplicationContextInitException("Failed to load configuration class: " + configClassName, e);
        }
        var context = new AnnotationConfigWebApplicationContext(configClass, new PropertyResolver(loadProperties()));
        context.setServletContext(servletContext);
        return context;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        Map<String, Object> yamlMap = YamlUtils.loadYamlAsPlainMap("/application.yml");
        yamlMap.forEach((key, value) -> {
            if (value instanceof String strValue) {
                properties.put(key, strValue);
            }
        });
        logger.debug("Loaded properties from application.yml: {}", properties);
        return properties;
    }
}
