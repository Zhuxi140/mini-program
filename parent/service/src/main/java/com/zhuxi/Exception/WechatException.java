package com.zhuxi.Exception;

public class WechatException extends LocatedException {
    private final int code;
    public WechatException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
