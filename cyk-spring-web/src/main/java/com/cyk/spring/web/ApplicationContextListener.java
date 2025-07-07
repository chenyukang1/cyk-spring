package com.cyk.spring.web;

import com.cyk.spring.ioc.context.ApplicationContext;
import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.ioc.utils.StringUtils;
import com.cyk.spring.web.context.AnnotationConfigWebApplicationContext;
import com.cyk.spring.web.exception.ApplicationContextInitException;
import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

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
        var applicationContext = createApplicationContext(configClassName, servletContext);
        servletContext.setAttribute("applicationContext", applicationContext);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);
        ServletRegistration.Dynamic dynamic = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dynamic.addMapping("/");
        dynamic.setLoadOnStartup(1);

        try {
            dispatcherServlet.initServlet();
        } catch (ServletException e) {
            throw new ApplicationContextInitException("Failed to initialize DispatcherServlet", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.warn("Servlet context destroyed, closing application context...");
        ServletContext servletContext = sce.getServletContext();
        if (servletContext.getAttribute("applicationContext") instanceof ApplicationContext applicationContext) {
            applicationContext.close();
        }
    }

    private AnnotationConfigWebApplicationContext createApplicationContext(String configClassName, ServletContext servletContext) {
        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
        } catch (ClassNotFoundException e) {
            throw new ApplicationContextInitException("Failed to load configuration class: " + configClassName, e);
        }
        Properties properties = ConfigLoader.load();
        var context = new AnnotationConfigWebApplicationContext(configClass, new PropertyResolver(properties));
        context.setServletContext(servletContext);
        return context;
    }

}
