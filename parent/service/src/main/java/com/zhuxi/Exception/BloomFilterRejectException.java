package com.zhuxi.Exception;

public class BloomFilterRejectException extends RuntimeException {
    private int code;
    public BloomFilterRejectException(String message) {
        super(message);
        this.code = 500;
    }
    public BloomFilterRejectException(String message, Throwable cause) {
        super(message, cause);
    }
}
