package com.zhuxi.Exception;

public class JsonException extends LocatedException{

    private final int code;

    public JsonException(String message) {
        super(message);
        this.code = 500;
    }


    public int getCode() {
        return code;
    }
}
