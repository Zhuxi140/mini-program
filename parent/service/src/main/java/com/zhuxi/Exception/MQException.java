package com.zhuxi.Exception;

public class MQException extends RuntimeException {
    public MQException(String message) {
        super(message);
    }

    public MQException(String message, Throwable cause) {
        super(message, cause);
    }
}
