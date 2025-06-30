package com.cyk.spring.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The class HandlerExecutionChain
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public class HandlerExecutionChain {

    private static final Logger logger = LoggerFactory.getLogger(HandlerExecutionChain.class);

    private Object handler;

    private List<HandlerInterceptor> interceptorList = new ArrayList<>();

    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList) {
        if (handler instanceof HandlerExecutionChain originalChain) {
            this.handler = originalChain.getHandler();
            this.interceptorList.addAll(originalChain.interceptorList);
        }
        else {
            this.handler = handler;
        }
        this.interceptorList.addAll(interceptorList);
    }

    public Object getHandler() {
        return handler;
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    public void addInterceptor(int index, HandlerInterceptor interceptor) {
        this.interceptorList.add(index, interceptor);
    }

    boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (int i = 0; i < interceptorList.size(); i++) {
            HandlerInterceptor interceptor = interceptorList.get(i);
            if (!interceptor.preHandle(request, response, handler)) {
                logger.debug("PreHandle interceptor {} returned false, stopping execution", interceptor.getClass().getName());
                return false; // Stop execution if any interceptor returns false
            }
            interceptorIndex = i; // Update the index of the last successful interceptor
        }
        return true;
    }

    void applyPostHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (int i = interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptorList.get(i);
            interceptor.postHandle(request, response, handler);
        }
    }

}
