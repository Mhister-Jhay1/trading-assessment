package org.trading.system.common.exception;

public class ApiException extends RuntimeException{
    public ApiException(String message) {
        super(message);
    }
}
