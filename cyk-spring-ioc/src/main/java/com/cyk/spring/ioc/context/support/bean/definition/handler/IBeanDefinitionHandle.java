package com.cyk.spring.ioc.context.support.bean.definition.handler;

import com.cyk.spring.ioc.context.model.BeanDefinition;

import java.util.Map;
import java.util.Set;

/**
 * The interface IBeanDefinitionHandle.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/4
 */
public interface IBeanDefinitionHandle {

    /**
     * Scan for class names set.
     *
     * @param configClass the config class
     * @return the set
     */
    Set<String> scanForClassNames(Class<?> configClass);

    /**
     * Create bean definitions map.
     *
     * @param beanClassNames the bean class names
     * @return the map
     */
    Map<String, BeanDefinition> createBeanDefinitions(Set<String> beanClassNames);
}
