/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
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
