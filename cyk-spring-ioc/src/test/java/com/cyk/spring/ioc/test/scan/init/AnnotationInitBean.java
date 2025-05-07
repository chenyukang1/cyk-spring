package com.cyk.spring.ioc.test.scan.init;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Value;
import jakarta.annotation.PostConstruct;

@Component
public class AnnotationInitBean {

    @Value("${app.title}")
    String appTitle;

    @Value("${app.version}")
    String appVersion;

    public String appName;

    @PostConstruct
    public void init() {
        this.appName = this.appTitle + " / " + this.appVersion;
    }
}
