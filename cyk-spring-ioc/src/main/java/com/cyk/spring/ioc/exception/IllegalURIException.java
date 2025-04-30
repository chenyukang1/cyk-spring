/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.ioc.exception;

/**
 * The class IllegalURIException
 *
 * @author yukang.chen
 * @date 2025/4/30
 */
public class IllegalURIException extends RuntimeException {

    public IllegalURIException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalURIException(String message) {
        super(message);
    }
}
