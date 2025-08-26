package com.zhuxi.Exception;

public class BloomFilterRejectException extends LocatedException {
    private final int code;
    public BloomFilterRejectException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
