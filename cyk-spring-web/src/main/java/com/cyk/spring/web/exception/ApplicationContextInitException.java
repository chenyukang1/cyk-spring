package com.cyk.spring.web.exception;

import java.io.Serial;

/**
 * The class IocContextInitException
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class ApplicationContextInitException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ApplicationContextInitException() {
        super();
    }

    public ApplicationContextInitException(String message) {
        super(message);
    }

    public ApplicationContextInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationContextInitException(Throwable cause) {
        super(cause);
    }
}
