package com.zhuxi.Exception;

public class OSSException extends LocatedException {
    private final int code;
    public OSSException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
