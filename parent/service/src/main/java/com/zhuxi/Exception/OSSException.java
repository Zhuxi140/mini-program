package com.zhuxi.Exception;

public class OSSException extends RuntimeException {
    private int code;
    public OSSException(String message) {
        super(message);
        this.code = 500;
    }
    public OSSException(String message, Throwable cause) {
        super(message, cause);
    }
}
