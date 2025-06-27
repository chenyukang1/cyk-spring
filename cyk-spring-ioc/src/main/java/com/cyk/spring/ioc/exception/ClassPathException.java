package com.cyk.spring.ioc.exception;

import java.io.Serial;

/**
 * The class ClassPathException
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class ClassPathException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClassPathException() {
        super();
    }

    public ClassPathException(String message) {
        super(message);
    }

    public ClassPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassPathException(Throwable cause) {
        super(cause);
    }
}
