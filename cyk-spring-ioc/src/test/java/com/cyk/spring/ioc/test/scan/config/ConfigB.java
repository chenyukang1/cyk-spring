package com.cyk.spring.ioc.test.scan.config;

import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;

/**
 * The class ConfigB
 *
 * @author yukang.chen
 * @date 2025/4/30
 */
@Configuration
public class ConfigB {

    public ConfigB(@Value("${app.title}") String title,
                   @Value("${app.version}") String version) {
    }
}
