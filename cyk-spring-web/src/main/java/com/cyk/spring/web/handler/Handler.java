package com.cyk.spring.web.handler;

import com.cyk.spring.ioc.context.ConfigurableApplicationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The interface Handler
 *
 * @author yukang.chen
 * @date 2025/7/5
 */
public interface Handler {

    void init(ConfigurableApplicationContext context) throws Exception;

    void doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
