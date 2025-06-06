package com.cyk.spring.jdbc.tx;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;

/**
 * The class TransactionConfiguration
 *
 * @author yukang.chen
 * @date 2025/6/4
 */
@Configuration
public class TransactionConfiguration {

    @Bean
    public TransactionBeanPostProcessor transactionBeanPostProcessor() {
        return new TransactionBeanPostProcessor();
    }

    @Bean
    public TransactionInvocationHandler transactionInvocationHandler(PlatformTransactionManager transactionManager) {
        return new TransactionInvocationHandler(transactionManager);
    }
}
