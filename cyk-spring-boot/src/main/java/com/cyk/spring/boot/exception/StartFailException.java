package com.cyk.spring.boot.exception;

import java.io.Serial;

/**
 * The class StartFailException.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2025/7/7
 */
public class StartFailException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public StartFailException(String message) {
        super(message);
    }

    public StartFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartFailException(Throwable cause) {
        super(cause);
    }
}
