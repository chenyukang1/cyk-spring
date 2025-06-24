package com.cyk.spring.ioc.annotation;

import java.lang.annotation.*;

/**
 * 标记是系统基础bean
 *
 * @author yukang.chen
 * @date 2025/6/20
 */

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Infrastructure {
}
