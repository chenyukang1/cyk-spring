package com.cyk.spring.ioc.context.support.bean.definition.assemble.impl;

import com.cyk.spring.ioc.context.annotation.Bean;
import com.cyk.spring.ioc.context.exception.BeanDefinitionException;
import com.cyk.spring.ioc.context.model.BeanDefinition;
import com.cyk.spring.ioc.context.support.bean.definition.assemble.IBeanDefinitionAssemble;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
/**
 * The class A.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/4
 */
public abstract class AbstractBeanDefinitionAssembler implements IBeanDefinitionAssemble {

    @Override
    public final void assembleBean(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        String beanName = getBeanName(clazz);
        Constructor<?> constructor = getConstructor(clazz);
        int order = getOrder(clazz);
        boolean primary = IsPrimary(clazz);
        Method initMethod = getInitMethod(clazz);
        Method destroyMethod = getDestroyMethod(clazz);
        if (initMethod != null) {
            initMethod.setAccessible(true);
        }
        if (destroyMethod != null) {
            destroyMethod.setAccessible(true);
        }

        BeanDefinition beanDefinition = new BeanDefinition(clazz, beanName, constructor, order, primary, initMethod, destroyMethod);
        if (beanDefinitions.put(beanDefinition.getBeanName(), beanDefinition) != null) {
            throw new BeanDefinitionException("Duplicate bean name: " + beanDefinition.getBeanName());
        }
    }

    @Override
    public void assembleFactoryBeans(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions) {
        for (Method method : clazz.getDeclaredMethods()) {
            Bean bean = method.getAnnotation(Bean.class);
            if (bean != null) {
                int modifiers = method.getModifiers();
                if (Modifier.isAbstract(modifiers)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be abstract.");
                }
                if (Modifier.isFinal(modifiers)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be final.");
                }
                if (Modifier.isPrivate(modifiers)) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not be private.");
                }
                Class<?> beanClass = method.getReturnType();
                if (beanClass.isPrimitive()) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return primitive type.");
                }
                if (beanClass == void.class || beanClass == Void.class) {
                    throw new BeanDefinitionException("@Bean method " + clazz.getName() + "." + method.getName() + " must not return void.");
                }
                String beanName = getBeanName(method);
                String factoryBeanName = getBeanName(clazz);
                int order = getOrder(method);
                boolean primary = IsPrimary(method);
                String initMethodName = getInitMethodName(bean);
                String destroyMethodName = getDestroyMethodName(bean);
                BeanDefinition beanDefinition = new BeanDefinition(clazz, beanName, factoryBeanName, method, order,
                        primary, initMethodName, destroyMethodName);
                if (beanDefinitions.put(beanName, beanDefinition) != null)
                    throw new BeanDefinitionException("Duplicate bean name: " + beanDefinition.getBeanName());
            }
        }
    }

    /**
     * Gets bean name.
     *
     * @param clazz the clazz
     * @return the bean name
     */
    protected abstract String getBeanName(Class<?> clazz);

    /**
     * Gets constructor.
     *
     * @param clazz the clazz
     * @return the constructor
     */
    protected abstract Constructor<?> getConstructor(Class<?> clazz);

    /**
     * Gets order.
     *
     * @param clazz the clazz
     * @return the order
     */
    protected abstract int getOrder(Class<?> clazz);

    /**
     * Is primary boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    protected abstract boolean IsPrimary(Class<?> clazz);

    /**
     * Gets init method.
     *
     * @param clazz the clazz
     * @return the init method
     */
    protected abstract Method getInitMethod(Class<?> clazz);

    /**
     * Gets destroy method.
     *
     * @param clazz the clazz
     * @return the destroy method
     */
    protected abstract Method getDestroyMethod(Class<?> clazz);

    /**
     * Gets bean name.
     *
     * @param method the method
     * @return the bean name
     */
    protected abstract String getBeanName(Method method);

    /**
     * Gets bean name.
     *
     * @param method the method
     * @return the bean name
     */
    protected abstract int getOrder(Method method);

    /**
     * Is primary boolean.
     *
     * @param method the method
     * @return the boolean
     */
    protected abstract boolean IsPrimary(Method method);

    /**
     * Gets init method name.
     *
     * @param bean the bean
     * @return the init method name
     */
    protected abstract String getInitMethodName(Bean bean);

    /**
     * Gets destroy method name.
     *
     * @param bean the bean
     * @return the destroy method name
     */
    protected abstract String getDestroyMethodName(Bean bean);
}
