package com.cyk.spring.aop;

import com.cyk.spring.aop.exception.AopConfigException;
import com.cyk.spring.ioc.context.ApplicationContext;
import com.cyk.spring.ioc.context.ApplicationContextAware;
import com.cyk.spring.ioc.context.BeanPostProcessor;
import com.cyk.spring.ioc.exception.BeansException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * The class AnnotationProxyBeanPostProcessor
 *
 * @author yukang.chen
 * @date 2025/5/22
 */
public abstract class AnnotationProxyBeanPostProcessor<T extends Annotation> implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationProxyBeanPostProcessor.class);
    private final ProxyResolver proxyResolver = new ProxyResolver();
    private final Map<String, Object> beanMap = new HashMap<>();
    private final Class<T> annotationType = getParameterizedType();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        T annotation = beanClass.getAnnotation(annotationType);
        if (annotation != null) {
            String value;
            try {
                 value = (String) annotation.annotationType().getMethod("value").invoke(annotation);
            } catch (Exception e) {
                logger.error("Cannot get annotation value.", e);
                throw new AopConfigException("invocation handler not found: " + beanClass.getSimpleName());
            }
            InvocationHandler invocationHandler = applicationContext.getBean(value, InvocationHandler.class);
            beanMap.put(beanName, bean);
            return proxyResolver.createProxy(bean, invocationHandler);
        }
        return bean;
    }

    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        return beanMap.getOrDefault(beanName, bean);
    }

    @SuppressWarnings("unchecked")
    private Class<T> getParameterizedType() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType pt)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type.");
        }
        Type[] types = pt.getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " has more than 1 parameterized types.");
        }
        Type r = types[0];
        if (!(r instanceof Class<?>)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type of class.");
        }
        return (Class<T>) r;
    }
}
