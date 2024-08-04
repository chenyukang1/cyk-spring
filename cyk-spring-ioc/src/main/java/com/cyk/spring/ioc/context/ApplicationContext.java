package com.cyk.spring.ioc.context;

import java.util.List;

/**
 * The interface ApplicationContext.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/3
 */
public interface ApplicationContext extends AutoCloseable {

    /**
     * 是否存在指定name的Bean
     *
     * @param name the name
     * @return the boolean
     */
    boolean containsBean(String name);

    /**
     * 根据name返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
     *
     * @param <T>  the type parameter
     * @param name the name
     * @return the bean
     */
    <T> T getBean(String name);

    /**
     * 根据name返回唯一Bean，未找到抛出NoSuchBeanDefinitionException，
     * 找到但type不符抛出BeanNotOfRequiredTypeException
     *
     * @param <T>          the type parameter
     * @param name         the name
     * @param requiredType the required type
     * @return the bean
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * 根据type返回唯一Bean，未找到抛出NoSuchBeanDefinitionException
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @return the bean
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * 根据type返回一组Bean，未找到返回空List
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @return the beans
     */
    <T> List<T> getBeans(Class<T> requiredType);

    /**
     * 关闭并执行所有bean的destroy方法
     */
    void close();

}