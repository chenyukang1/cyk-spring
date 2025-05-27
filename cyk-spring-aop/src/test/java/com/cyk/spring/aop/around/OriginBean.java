package com.cyk.spring.aop.around;

import com.cyk.spring.aop.annotation.Around;
import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Value;

@Component
@Around("aroundInvocationHandler")
public class OriginBean {

    @Value("${customer.name}")
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
