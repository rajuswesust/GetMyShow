package com.raju.getmyshow.shared.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String errorType, String message) {
        super(errorType + ": " + message);
    }

    public String getErrorCode() {
        return null;
    }
}
