package com.cyk.spring.ioc.test.scan.destroy;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Value;
import jakarta.annotation.PreDestroy;

@Component
public class AnnotationDestroyBean {

    @Value("${app.title}")
    public String appTitle;

    @PreDestroy
    void destroy() {
        this.appTitle = null;
    }
}
