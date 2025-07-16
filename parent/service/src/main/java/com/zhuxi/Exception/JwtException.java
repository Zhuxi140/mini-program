package com.zhuxi.Exception;

public class JwtException extends RuntimeException{
    private final int code;

    public JwtException(String message)
    {
        super(message);
        this.code = 500;
    }
    public JwtException(String message, Throwable cause, int code)
    {
        super(message, cause);
        this.code = code;
    }
}
