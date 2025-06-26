package com.zhuxi.handler;


import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import com.zhuxi.Exception.transactionalException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(transactionalException.class)
    public Result<Void> handleException(transactionalException e) {
        String message = e.getMessage();
        return Result.error(message);
    }
}
