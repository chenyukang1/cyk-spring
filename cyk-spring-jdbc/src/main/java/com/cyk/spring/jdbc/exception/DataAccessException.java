package com.cyk.spring.jdbc.exception;

import com.cyk.spring.ioc.exception.NestedRuntimeException;

public class DataAccessException extends NestedRuntimeException {

    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
