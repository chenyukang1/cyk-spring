package com.cyk.spring.ioc.test.scan.proxy;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Value;

@Component
public class OriginBean {

    @Value("${app.title}")
    public String name;

    public String version;

    @Value("${app.version}")
    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return this.version;
    }

}
