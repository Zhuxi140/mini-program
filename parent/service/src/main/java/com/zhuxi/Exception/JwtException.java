package com.zhuxi.Exception;

public class JwtException extends LocatedException{
    private final int code;
    public JwtException(String message)
    {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
