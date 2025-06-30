package com.cyk.spring.web.annotation;

import com.cyk.spring.ioc.annotation.Component;

import java.lang.annotation.*;

/**
 * The annotation RestController
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {

    String value() default "";
}
