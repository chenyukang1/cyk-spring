package com.cyk.spring.web.handler;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.context.ApplicationContext;
import com.cyk.spring.ioc.context.ApplicationContextAware;
import com.cyk.spring.ioc.context.ConfigurableApplicationContext;
import com.cyk.spring.ioc.exception.BeansException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class DefaultHandlerMapping
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
@Component
public class DefaultHandlerMapping implements HandlerMapping, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(DefaultHandlerMapping.class);
    private Handler defaultHandler;

    private Object handler;

    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        defaultHandler = new DefaultHandler();
        try {
            defaultHandler.init((ConfigurableApplicationContext) applicationContext);
        } catch (Exception e) {
            log.error("Failed to initialize default handler", e);
            throw new BeansException("Failed to initialize default handler", e);
        }
        log.info("DefaultHandlerMapping initialized with default handler: {}", defaultHandler.getClass().getName());
    }

    @Override
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = getHandler();
        if (handler == null) {
            handler = defaultHandler;
        }
        HandlerExecutionChain executionChain = (handler instanceof HandlerExecutionChain chain) ?
                chain : new HandlerExecutionChain(handler);
        for (HandlerInterceptor interceptor : interceptors) {
            executionChain.addInterceptor(interceptor);
        }
        return executionChain;
    }

    public void setInterceptors(HandlerInterceptor... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }
}
