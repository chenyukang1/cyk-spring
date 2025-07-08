package com.cyk.spring.web.handler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The interface HandlerMapping
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public interface HandlerMapping {

    void init() throws Exception;

    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
