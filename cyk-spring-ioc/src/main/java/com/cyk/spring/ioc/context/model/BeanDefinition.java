package com.cyk.spring.ioc.context.model;

import jakarta.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The class BeanDefinition.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/3
 */
public class BeanDefinition {

    private final Class<?> beanClass;
    private final String beanName;
    private final Constructor<?> constructor;
    private final String factoryBeanName;
    private final Method factoryMethod;
    private final int order;
    private final boolean primary;

    private String initMethodName;
    private String destroyMethodName;
    private Method initMethod;
    private Method destroyMethod;

    public BeanDefinition(Class<?> beanClass, String beanName, Constructor<?> constructor, int order, boolean primary,
                          Method initMethod, Method destroyMethod) {
        this.beanClass = beanClass;
        this.beanName = beanName;
        this.constructor = constructor;
        this.factoryBeanName = null;
        this.factoryMethod = null;
        this.order = order;
        this.primary = primary;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    public BeanDefinition(Class<?> beanClass, String beanName, String factoryBeanName, Method factoryMethod,
                          int order, boolean primary, String initMethodName, String destroyMethodName) {
        this.beanClass = beanClass;
        this.beanName = beanName;
        this.constructor = null;
        this.factoryBeanName = factoryBeanName;
        this.factoryMethod = factoryMethod;
        this.order = order;
        this.primary = primary;
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        return this.constructor;
    }

    @Nullable
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Nullable
    public Method getFactoryMethod() {
        return this.factoryMethod;
    }

    @Nullable
    public Method getInitMethod() {
        return this.initMethod;
    }

    @Nullable
    public Method getDestroyMethod() {
        return this.destroyMethod;
    }

    @Nullable
    public String getInitMethodName() {
        return this.initMethodName;
    }

    @Nullable
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }


}
