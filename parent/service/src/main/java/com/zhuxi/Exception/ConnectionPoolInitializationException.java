package com.zhuxi.Exception;

public class ConnectionPoolInitializationException extends RuntimeException {
    public ConnectionPoolInitializationException(String message) {
        super(message);
    }
    public ConnectionPoolInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
