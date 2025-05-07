package com.cyk.spring.ioc.definition;

import com.cyk.spring.ioc.exception.BeanCreationException;
import jakarta.annotation.Nonnull;
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
public class BeanDefinition implements Comparable<BeanDefinition> {

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
    private Object instance;

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

    public Object getRequiredInstance() {
        if (this.instance == null) {
            throw new BeanCreationException(String.format(
                    "Instance of bean with name '%s' and type '%s' is not instantiated during current stage.",
                    this.getBeanName(), this.getBeanClass().getName()));
        }
        return this.instance;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public boolean isPrimary() {
        return primary;
    }

    public int getOrder() {
        return order;
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

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClass=" + beanClass +
                ", beanName='" + beanName + '\'' +
                ", constructor=" + constructor +
                ", factoryBeanName='" + factoryBeanName + '\'' +
                ", factoryMethod=" + factoryMethod +
                ", order=" + order +
                ", primary=" + primary +
                ", initMethodName='" + initMethodName + '\'' +
                ", destroyMethodName='" + destroyMethodName + '\'' +
                ", initMethod=" + initMethod +
                ", destroyMethod=" + destroyMethod +
                ", instance=" + instance +
                '}';
    }

    @Override
    public int compareTo(@Nonnull BeanDefinition definition) {
        int cmp = Integer.compare(this.order, definition.order);
        if (cmp != 0) {
            return cmp;
        }
        return this.beanName.compareTo(definition.beanName);
    }
}
