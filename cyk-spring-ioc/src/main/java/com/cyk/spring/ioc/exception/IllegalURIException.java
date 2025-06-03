package com.cyk.spring.ioc.exception;

/**
 * The class IllegalURIException
 *
 * @author yukang.chen
 * @date 2025/4/30
 */
public class IllegalURIException extends RuntimeException {

    public IllegalURIException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalURIException(String message) {
        super(message);
    }
}
