package com.cyk.spring.web.handler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The interface HandlerMapping
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public interface HandlerMapping {

    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
