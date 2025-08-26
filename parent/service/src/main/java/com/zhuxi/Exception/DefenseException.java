package com.zhuxi.Exception;

public class DefenseException extends LocatedException {
    private final int code;
    public DefenseException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
