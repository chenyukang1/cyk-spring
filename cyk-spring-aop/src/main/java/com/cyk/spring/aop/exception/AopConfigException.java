package com.cyk.spring.aop.exception;

import com.cyk.spring.ioc.exception.NestedRuntimeException;

public class AopConfigException extends NestedRuntimeException {

    public AopConfigException() {
        super();
    }

    public AopConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public AopConfigException(String message) {
        super(message);
    }

    public AopConfigException(Throwable cause) {
        super(cause);
    }
}
