package com.cyk.spring.ioc.test.scan.init;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;

@Configuration
public class SpecifyInitConfiguration {

    @Bean(initMethod = "init")
    SpecifyInitBean createSpecifyInitBean(@Value("${app.title}") String appTitle, @Value("${app.version}") String appVersion) {
        return new SpecifyInitBean(appTitle, appVersion);
    }
}
