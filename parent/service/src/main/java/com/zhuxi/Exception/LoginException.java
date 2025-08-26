package com.zhuxi.Exception;

public class LoginException extends LocatedException {
    private final int code;
    public LoginException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
