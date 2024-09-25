package com.cyk.spring.ioc.context.support.bean.instance;

import com.cyk.spring.ioc.context.model.BeanDefinition;

import java.util.Map;

/**
 * The interface IBeanInstanceCreate.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/9/7
 */
public interface IBeanInstanceCreate {

    void createBean(Map<String, BeanDefinition> beanDefinitions);
}
