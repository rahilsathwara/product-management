package com.spring.task.exception;

import java.io.Serial;

public class JwtTokenExpiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
