package com.cyk.spring.ioc.test.scan.destroy;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;

@Configuration
public class SpecifyDestroyConfiguration {

    @Bean(destroyMethod = "destroy")
    SpecifyDestroyBean createSpecifyDestroyBean(@Value("${app.title}") String appTitle) {
        return new SpecifyDestroyBean(appTitle);
    }
}
