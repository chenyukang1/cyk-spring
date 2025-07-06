package com.cyk.spring.web.test;

import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * The class WebMvcTest
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
public class WebMvcTest {

    @Test
    public void test_webMvc() {
        // 打war包后请求/hello?toName=world
    }

    private Properties properties() {
        var properties = new Properties();
        properties.put("spring.datasource.url", "jdbc:sqlite:test.db");
        properties.put("spring.datasource.username", "sa");
        properties.put("spring.datasource.password", "test");
        properties.put("spring.datasource.driver-class-name", "org.sqlite.JDBC");
        return properties;
    }
}
