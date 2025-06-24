package com.cyk.spring.jdbc.tx;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Order;

import javax.sql.DataSource;

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
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Order(10000)
    public TransactionInvocationHandler transactionInvocationHandler(PlatformTransactionManager transactionManager) {
        return new TransactionInvocationHandler(transactionManager);
    }
}
