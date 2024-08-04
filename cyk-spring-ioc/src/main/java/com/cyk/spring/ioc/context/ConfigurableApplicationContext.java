package com.cyk.spring.ioc.context;

import com.cyk.spring.ioc.context.model.BeanDefinition;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * The interface ConfigurableApplicationContext.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/3
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * Find bean definitions list.
     *
     * @param type the type
     * @return the list
     */
    List<BeanDefinition> findBeanDefinitions(Class<?> type);

    /**
     * Find bean definition.
     *
     * @param type the type
     * @return the bean definition
     */
    @Nullable
    BeanDefinition findBeanDefinition(Class<?> type);

    /**
     * Find bean definition.
     *
     * @param name the name
     * @return the bean definition
     */
    @Nullable
    BeanDefinition findBeanDefinition(String name);

    /**
     * Find bean definition.
     *
     * @param name         the name
     * @param requiredType the required type
     * @return the bean definition
     */
    @Nullable
    BeanDefinition findBeanDefinition(String name, Class<?> requiredType);

    /**
     * Create bean as early singleton object.
     *
     * @param def the def
     * @return the object
     */
    Object createBeanAsEarlySingleton(BeanDefinition def);
}
