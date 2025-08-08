package com.zhuxi.Exception;

public class WechatException extends RuntimeException {
    private int code;
    public WechatException(String message) {
        super(message);
        this.code = 500;
    }
    public WechatException(String message, Throwable cause) {
        super(message, cause);

    }
}
