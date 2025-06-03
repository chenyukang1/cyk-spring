package com.cyk.spring.ioc.context;

import com.cyk.spring.ioc.exception.BeansException;

/**
 * The interface ApplicationContextAware
 *
 * @author yukang.chen
 * @date 2025/5/16
 */
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
