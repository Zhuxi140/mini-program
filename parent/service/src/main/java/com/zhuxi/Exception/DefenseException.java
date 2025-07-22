package com.zhuxi.Exception;

public class DefenseException extends RuntimeException {
    private int code;
    public DefenseException(String message) {
        super(message);
        this.code = 500;
    }
    public DefenseException(String message, Throwable cause) {
        super(message, cause);
    }


}
