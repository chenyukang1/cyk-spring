package com.cyk.spring.web.annotation;

import com.cyk.spring.ioc.annotation.Component;

import java.lang.annotation.*;

/**
 * The annotation Controller
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {

    String value() default "";
}
