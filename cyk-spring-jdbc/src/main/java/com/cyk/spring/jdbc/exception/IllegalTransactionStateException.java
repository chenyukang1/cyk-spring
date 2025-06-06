package com.cyk.spring.jdbc.exception;

/**
 * The class IllegalTransactionStateException
 *
 * @author yukang.chen
 * @date 2025/6/5
 */
public class IllegalTransactionStateException extends RuntimeException {

    public IllegalTransactionStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTransactionStateException(String message) {
        super(message);
    }
}
