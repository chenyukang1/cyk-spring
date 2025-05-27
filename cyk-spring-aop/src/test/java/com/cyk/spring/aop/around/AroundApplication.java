package com.cyk.spring.aop.around;

import com.cyk.spring.aop.AroundProxyBeanPostProcessor;
import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.ComponentScan;
import com.cyk.spring.ioc.annotation.Configuration;

@Configuration
@ComponentScan
public class AroundApplication {

    @Bean
    public AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor() {
        return new AroundProxyBeanPostProcessor();
    }
}
