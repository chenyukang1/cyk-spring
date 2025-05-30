/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.jdbc;

import com.cyk.spring.ioc.annotation.ComponentScan;

/**
 * The class JdbcTestApplication
 *
 * @author yukang.chen
 * @date 2025/5/30
 */
@ComponentScan
//@Configuration
public class JdbcTestApplication {

//    @Bean
//    public DataSource dataSource(
//            @Value("${spring.datasource.url}") String url,
//            @Value("${spring.datasource.username}") String username,
//            @Value("${spring.datasource.password}") String password,
//            @Value("${spring.datasource.driver-class-name:}") String driverClassName,
//            @Value("${spring.datasource.maximum-pool-size:20}") int maximumPoolSize,
//            @Value("${spring.datasource.minimum-pool-size:1}") int minimumPoolSize,
//            @Value("${spring.datasource.connection-timeout:30000}") int connTimeout
//    ) {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(url);
//        config.setUsername(username);
//        config.setPassword(password);
//        config.setDriverClassName(driverClassName);
//        config.setConnectionTimeout(connTimeout);
//        config.setMaximumPoolSize(maximumPoolSize);
//        config.setMinimumIdle(minimumPoolSize);
//        config.setAutoCommit(false);
//        return new HikariDataSource(config);
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
}
