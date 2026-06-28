package com.wajahat.ordersaga.common.exception;

public class UnauthorizedException extends BusinessException {
    private static final String ERROR_CODE = "UNAUTHORIZED";

    public UnauthorizedException(String message) {
        super(ERROR_CODE, message);
    }
}
