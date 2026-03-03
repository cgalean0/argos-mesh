package com.argos.orders.exceptions;

public class NotSuficientStockException extends RuntimeException{

    public NotSuficientStockException() {
    }

    public NotSuficientStockException(String message) {
        super(message);
    }

    public NotSuficientStockException(Throwable cause) {
        super(cause);
    }

    public NotSuficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSuficientStockException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
