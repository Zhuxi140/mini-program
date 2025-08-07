package com.zhuxi.Exception;

public class LoginException extends RuntimeException {
    private int code;
    public LoginException(String message) {
        super(message);
        this.code = 500;
    }
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
