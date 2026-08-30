package com.ecommerce.project.exception;

import java.io.Serial;
import java.util.Map;

public class AuthException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public AuthException(String message) {
        super(message);
    }
}
