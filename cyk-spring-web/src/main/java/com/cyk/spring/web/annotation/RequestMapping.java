package com.cyk.spring.web.annotation;

import com.cyk.spring.web.handler.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    /**
     * URL mapping.
     */
    String value();

    HttpMethod method() default HttpMethod.GET;

}
