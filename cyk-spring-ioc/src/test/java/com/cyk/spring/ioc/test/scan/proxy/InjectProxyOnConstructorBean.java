package com.cyk.spring.ioc.test.scan.proxy;

import com.cyk.spring.ioc.annotation.Autowired;
import com.cyk.spring.ioc.annotation.Component;

@Component
public class InjectProxyOnConstructorBean {

    public final OriginBean injected;

    public InjectProxyOnConstructorBean(@Autowired OriginBean injected) {
        this.injected = injected;
    }
}
