package com.cyk.spring.web.context;

import com.cyk.spring.ioc.context.AnnotationConfigApplicationContext;
import com.cyk.spring.ioc.context.BeanPostProcessor;
import com.cyk.spring.ioc.io.PropertyResolver;
import jakarta.servlet.ServletContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The class AnnotationConfigWebApplicationContext
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public class AnnotationConfigWebApplicationContext extends AnnotationConfigApplicationContext implements WebApplicationContext {

    private ServletContext servletContext;

    public AnnotationConfigWebApplicationContext(Class<?> configClass, PropertyResolver resolver) {
        super(configClass, resolver);
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    protected List<BeanPostProcessor> postBeanPostProcessors() {
        List<BeanPostProcessor> processors = new ArrayList<>();
        processors.add(new ServletContextAwareProcessor(servletContext));
        return processors;
    }
}
