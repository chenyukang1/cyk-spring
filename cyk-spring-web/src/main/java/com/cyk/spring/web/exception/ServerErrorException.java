package com.cyk.spring.web.exception;

import java.io.Serial;

/**
 * The class ServerErrorException
 *
 * @author yukang.chen
 * @date 2025/6/28
 */
public class ServerErrorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ServerErrorException() {
        super();
    }

    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerErrorException(Throwable cause) {
        super(cause);
    }
}
