package com.cyk.spring.ioc.exception;

/**
 * The type Beans exception.
 */
public class BeansException extends NestedRuntimeException {

    /**
     * Instantiates a new Beans exception.
     */
    public BeansException() {
    }

    /**
     * Instantiates a new Beans exception.
     *
     * @param message the message
     */
    public BeansException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Beans exception.
     *
     * @param cause the cause
     */
    public BeansException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Beans exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
