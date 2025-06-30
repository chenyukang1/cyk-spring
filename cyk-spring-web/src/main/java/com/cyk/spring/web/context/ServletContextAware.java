package com.cyk.spring.web.context;

import jakarta.servlet.ServletContext;

/**
 * The interface ServletContextAware
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public interface ServletContextAware {

    void setServletContext(ServletContext servletContext);

}
