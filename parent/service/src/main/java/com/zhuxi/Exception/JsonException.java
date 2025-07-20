package com.zhuxi.Exception;

public class JsonException extends RuntimeException{

    private final int code;

    public JsonException(String message) {
        super(message);
        this.code = 500;
    }

    public JsonException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

}
