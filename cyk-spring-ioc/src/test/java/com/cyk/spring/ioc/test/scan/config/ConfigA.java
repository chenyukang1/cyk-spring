package com.cyk.spring.ioc.test.scan.config;

import com.cyk.spring.ioc.annotation.Configuration;

/**
 * The class ConfigA
 *
 * @author yukang.chen
 * @date 2025/4/30
 */
@Configuration
public class ConfigA {

    public ConfigA(ConfigB configB) {
    }
}
