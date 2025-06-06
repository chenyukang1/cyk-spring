package com.cyk.spring.jdbc.tx.annotation;

import com.cyk.spring.jdbc.tx.Isolation;
import com.cyk.spring.jdbc.tx.Propagation;
import com.cyk.spring.jdbc.tx.TransactionDefinition;

import java.lang.annotation.*;

/**
 * The class Transactional
 *
 * @author yukang.chen
 * @date 2025/6/4
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    String value() default "transactionInvocationHandler";

    /**
     * 传播行为
     *
     * @return the propagation
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * 隔离级别
     *
     * @return the isolation
     */
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * 事务超时时间
     *
     * @return the int
     */
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
}
