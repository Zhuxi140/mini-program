package com.zhuxi.Exception;

public class DataInitException extends RuntimeException {
    public DataInitException(String message) {
        super(message);
    }
    public DataInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
