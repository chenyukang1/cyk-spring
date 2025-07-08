package com.cyk.spring.web;

import com.cyk.spring.ioc.context.ConfigurableApplicationContext;
import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.web.context.AnnotationConfigWebApplicationContext;
import com.cyk.spring.web.exception.ApplicationContextInitException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The class WebContext.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2025/7/9
 */
public class WebContext {

    private static final Logger log = LoggerFactory.getLogger(WebContext.class);

    private static ConfigurableApplicationContext context;

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static void initialize(Class<?> configClass,
                                  ServletContext servletContext) {
        if (!initialized.compareAndSet(false, true)) {
            log.warn("WebContext has already been initialized.");
            return;
        }
        if (servletContext == null) {
            throw new IllegalArgumentException("ServletContext cannot be null.");
        }
        Properties properties = ConfigLoader.load();

        var applicationContext = new AnnotationConfigWebApplicationContext(configClass, new PropertyResolver(properties));
        applicationContext.setServletContext(servletContext);
        context = applicationContext;
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

    public static ConfigurableApplicationContext getApplicationContext() {
        return context;
    }
}
