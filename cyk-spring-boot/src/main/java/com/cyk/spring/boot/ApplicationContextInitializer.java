package com.cyk.spring.boot;

import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.web.DispatcherServlet;
import com.cyk.spring.web.context.AnnotationConfigWebApplicationContext;
import com.cyk.spring.web.exception.ApplicationContextInitException;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

import java.util.Properties;
import java.util.Set;

/**
 * The class ApplicationContextInitializer
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
public class ApplicationContextInitializer implements ServletContainerInitializer {

    private final Class<?> configClass;

    private final Properties properties;

    public ApplicationContextInitializer(Class<?> configClass, Properties properties) {
        this.configClass = configClass;
        this.properties = properties;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) {
        servletContext.setRequestCharacterEncoding("UTF-8");
        servletContext.setResponseCharacterEncoding("UTF-8");

        var applicationContext = new AnnotationConfigWebApplicationContext(configClass, new PropertyResolver(properties));
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
}
