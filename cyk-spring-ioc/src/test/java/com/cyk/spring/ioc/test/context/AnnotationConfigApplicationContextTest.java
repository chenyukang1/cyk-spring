package com.cyk.spring.ioc.test.context;

import com.cyk.spring.ioc.context.AnnotationConfigApplicationContext;
import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.ioc.test.scan.ScanApplication;
import com.cyk.spring.ioc.test.scan.config.ConfigA;
import com.cyk.spring.ioc.test.scan.config.ConfigB;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The class AnnotationConfigApplicationContextTest
 *
 * @author yukang.chen
 * @date 2024/9/29
 */
public class AnnotationConfigApplicationContextTest {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigApplicationContextTest.class);

    @Test
    public void test_configuration() {
        try (var ctx = new AnnotationConfigApplicationContext(createPropertyResolver(), ScanApplication.class)) {
            assertNotNull(ctx.getBean(ConfigA.class));
            assertNotNull(ctx.getBean(ConfigB.class));
        }
    }

    PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        ps.put("app.title", "Scan App");
        ps.put("app.version", "v1.0");
        ps.put("jdbc.url", "jdbc:hsqldb:file:testdb.tmp");
        ps.put("jdbc.username", "sa");
        ps.put("jdbc.password", "");
        ps.put("convert.boolean", "true");
        ps.put("convert.byte", "123");
        ps.put("convert.short", "12345");
        ps.put("convert.integer", "1234567");
        ps.put("convert.long", "123456789000");
        ps.put("convert.float", "12345.6789");
        ps.put("convert.double", "123456789.87654321");
        ps.put("convert.localdate", "2023-03-29");
        ps.put("convert.localtime", "20:45:01");
        ps.put("convert.localdatetime", "2023-03-29T20:45:01");
        ps.put("convert.zoneddatetime", "2023-03-29T20:45:01+08:00[Asia/Shanghai]");
        ps.put("convert.duration", "P2DT3H4M");
        ps.put("convert.zoneid", "Asia/Shanghai");
        return new PropertyResolver(ps);
    }
}
