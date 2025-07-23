package com.zhuxi.Exception;

public class SnycException extends RuntimeException {
    public SnycException(String message) {
        super(message);
    }
    public SnycException(String message, Throwable cause) {
        super(message, cause);
    }
}
