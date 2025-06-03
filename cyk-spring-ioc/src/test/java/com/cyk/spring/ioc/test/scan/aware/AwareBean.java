package com.cyk.spring.ioc.test.scan.aware;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.context.ApplicationContext;
import com.cyk.spring.ioc.context.ApplicationContextAware;
import com.cyk.spring.ioc.exception.BeansException;

/**
 * The class aware
 *
 * @author yukang.chen
 * @date 2025/5/22
 */
@Component
public class AwareBean implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
