package com.cyk.spring.ioc.test.scan.custom.annotation;

import com.cyk.spring.ioc.annotation.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CustomAnnotation {

    String value() default "";

}
