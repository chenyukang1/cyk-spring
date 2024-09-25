package com.cyk.spring.ioc.context.support.bean.definition.assemble;

import com.cyk.spring.ioc.context.model.BeanDefinition;

import java.util.Map;

/**
 * The class BeanDefinitionConverter.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/3
 */
public interface IBeanDefinitionAssemble {

    /**
     * To bean definition.
     *
     * @param clazz           the clazz
     * @param beanDefinitions the bean definitions
     */
    void assembleBean(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions);

    /**
     * Assemble bean methods.
     *
     * @param clazz           the clazz
     * @param beanDefinitions the bean definitions
     */
    void assembleFactoryBeans(Class<?> clazz, Map<String, BeanDefinition> beanDefinitions);
}
