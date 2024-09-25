package com.cyk.spring.ioc.context.impl;

import com.cyk.spring.ioc.context.ConfigurableApplicationContext;
import com.cyk.spring.ioc.context.model.BeanDefinition;
import com.cyk.spring.ioc.context.support.bean.definition.handler.IBeanDefinitionHandle;
import com.cyk.spring.ioc.context.support.bean.definition.handler.impl.DefaultBeanDefinitionHandler;
import com.cyk.spring.ioc.context.support.bean.instance.IBeanInstanceCreate;
import com.cyk.spring.ioc.context.support.bean.instance.impl.DefaultBeanInstanceCreator;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class AnnotationConfigApplicationContext.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/3
 */
public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    protected final IBeanDefinitionHandle beanDefinitionHandler;
    protected final IBeanInstanceCreate beanInstanceCreator;
    protected Map<String, BeanDefinition> beanDefinitions;

    public AnnotationConfigApplicationContext(IBeanDefinitionHandle beanDefinitionHandler,
                                              IBeanInstanceCreate beanInstanceCreator,
                                              Class<?> configClass) {
        this.beanDefinitionHandler = beanDefinitionHandler;
        this.beanInstanceCreator = beanInstanceCreator;

        // init
        init(configClass);

        // refresh
        refresh(beanDefinitions);
    }

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        this(new DefaultBeanDefinitionHandler(), new DefaultBeanInstanceCreator(), configClass);
    }

    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        return null;
    }

    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return null;
    }

    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        return null;
    }

    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition def) {
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public <T> T getBean(String name) {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> List<T> getBeans(Class<T> requiredType) {
        return Collections.emptyList();
    }

    @Override
    public void close() {

    }

    private void init(Class<?> configClass) {
        // 1.扫描获取所有Bean的Class类型
        Set<String> beanClassNames = beanDefinitionHandler.scanForClassNames(configClass);

        // 2.创建Bean的定义
        this.beanDefinitions = beanDefinitionHandler.createBeanDefinitions(beanClassNames);
    }

    private void refresh(Map<String, BeanDefinition> beanDefinitions) {
        beanInstanceCreator.createBean(beanDefinitions);
    }
}
