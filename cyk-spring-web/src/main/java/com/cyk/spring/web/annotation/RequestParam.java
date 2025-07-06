package com.cyk.spring.web.annotation;

import java.lang.annotation.*;

/**
 * The annotation RequestParam
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value();
}
