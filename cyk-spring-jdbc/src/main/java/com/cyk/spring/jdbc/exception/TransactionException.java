package com.cyk.spring.jdbc.exception;

/**
 * The class TransactionException
 *
 * @author yukang.chen
 * @date 2025/6/5
 */
public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
