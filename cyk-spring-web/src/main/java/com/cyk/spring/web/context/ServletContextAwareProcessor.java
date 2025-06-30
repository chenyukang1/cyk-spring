package com.cyk.spring.web.context;

import com.cyk.spring.ioc.context.BeanPostProcessor;
import jakarta.servlet.ServletContext;

/**
 * The class ServletContextAwareProcessor
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public class ServletContextAwareProcessor implements BeanPostProcessor {

    private final ServletContext servletContext;

    public ServletContextAwareProcessor(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof ServletContextAware servletContextAware) {
            servletContextAware.setServletContext(servletContext);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
