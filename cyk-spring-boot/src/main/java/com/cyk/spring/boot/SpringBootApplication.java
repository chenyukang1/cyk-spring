package com.cyk.spring.boot;

import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Import;
import com.cyk.spring.jdbc.JdbcConfiguration;
import com.cyk.spring.web.WebMvcConfiguration;

import java.lang.annotation.*;

/**
 * The interface SpringBootApplication
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import({JdbcConfiguration.class, WebMvcConfiguration.class})
public @interface SpringBootApplication {

}
