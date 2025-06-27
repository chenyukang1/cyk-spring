package com.cyk.spring.web;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class WebMvcConfiguration
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
@Configuration
public class WebMvcConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfiguration.class);

    @Bean
    public String test(@Value("${application.name}") String applicationName) {
        logger.info("WebMvcConfiguration test method called");
        logger.info("Application name from properties: {}", applicationName);
        return "test";
    }
}
