package com.cyk.spring.jdbc;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * The class JdbcConfiguration
 *
 * @author yukang.chen
 * @date 2025/5/30
 */
@Configuration
public class JdbcConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.driver-class-name:}") String driverClassName,
            @Value("${spring.datasource.maximum-pool-size:20}") int maximumPoolSize,
            @Value("${spring.datasource.minimum-pool-size:1}") int minimumPoolSize,
            @Value("${spring.datasource.connection-timeout:30000}") int connTimeout
    ) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setConnectionTimeout(connTimeout);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumPoolSize);
        config.setAutoCommit(true);
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
