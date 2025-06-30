package com.cyk.spring.web.context;

import com.cyk.spring.ioc.context.ApplicationContext;
import jakarta.servlet.ServletContext;

/**
 * The interface WebApplicationContext
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public interface WebApplicationContext extends ApplicationContext {

    ServletContext getServletContext();
}
