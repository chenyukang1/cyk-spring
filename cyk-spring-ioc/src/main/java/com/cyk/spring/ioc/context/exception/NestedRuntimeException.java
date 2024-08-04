package com.cyk.spring.ioc.context.exception;

/**
 * The class NestedRuntimeException.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/3
 */
public class NestedRuntimeException extends RuntimeException {

    /**
     * Instantiates a new Nested runtime exception.
     */
    public NestedRuntimeException() {
    }

    /**
     * Instantiates a new Nested runtime exception.
     *
     * @param message the message
     */
    public NestedRuntimeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Nested runtime exception.
     *
     * @param cause the cause
     */
    public NestedRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Nested runtime exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NestedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

