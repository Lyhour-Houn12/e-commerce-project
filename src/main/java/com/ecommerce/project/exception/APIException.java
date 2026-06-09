package com.ecommerce.project.exception;

import java.io.Serial;

public class APIException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }
    public APIException() {
    }
}
