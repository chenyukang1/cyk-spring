package com.cyk.spring.web.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class AbstractHandlerMapping
 *
 * @author yukang.chen
 * @date 2025/6/30
 */
public abstract class AbstractHandlerMapping implements HandlerMapping {

    private final List<Object> interceptors = new ArrayList<>();

    public void setInterceptors(Object... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }
}
