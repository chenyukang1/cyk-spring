package com.cyk.spring.ioc.test.scan.nested;

import com.cyk.spring.ioc.annotation.Component;

@Component
public class OuterBean {

    @Component
    public static class NestedBean {

    }
}
