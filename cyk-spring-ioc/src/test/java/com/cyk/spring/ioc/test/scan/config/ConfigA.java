/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.ioc.test.scan.config;

import com.cyk.spring.ioc.annotation.Configuration;

/**
 * The class ConfigA
 *
 * @author yukang.chen
 * @date 2025/4/30
 */
@Configuration
public class ConfigA {

    public ConfigA(ConfigB configB) {
    }
}
