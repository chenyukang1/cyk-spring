package com.cyk.spring.ioc.context.exception;

/**
 * The class BeanDefinitionException.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/3
 */
public class BeanDefinitionException extends BeansException {

    /**
     * Instantiates a new Bean definition exception.
     */
    public BeanDefinitionException() {
    }

    /**
     * Instantiates a new Bean definition exception.
     *
     * @param message the message
     */
    public BeanDefinitionException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Bean definition exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Bean definition exception.
     *
     * @param cause the cause
     */
    public BeanDefinitionException(Throwable cause) {
        super(cause);
    }
}
