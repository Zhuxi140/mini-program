package com.zhuxi.handler;


import com.zhuxi.Result.Result;
import com.zhuxi.Exception.transactionalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({transactionalException.class})
    public Result<Void> handleException(transactionalException e) {
        String message = e.getMessage();
        return Result.error(message);
    }
}
