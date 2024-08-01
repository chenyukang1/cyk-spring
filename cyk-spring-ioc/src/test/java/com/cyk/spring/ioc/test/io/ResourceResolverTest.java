package com.cyk.spring.ioc.test.io;

import com.cyk.spring.ioc.io.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * The class ResourceResolverTest.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/1
 */
public class ResourceResolverTest {

    @Test
    public void testFileScan() {
        ResourceResolver resourceResolver = new ResourceResolver("com.cyk.spring.ioc.test.io.scan");
        List<String> list = resourceResolver.scan();
        Assertions.assertEquals(17, list.size());
    }

    @Test
    public void testJarFileScan() {
        ResourceResolver resourceResolver = new ResourceResolver("ch.qos.logback.classic");
        List<String> list = resourceResolver.scan();
        System.out.println(list);

        Assertions.assertEquals(154, list.size());
    }
}
