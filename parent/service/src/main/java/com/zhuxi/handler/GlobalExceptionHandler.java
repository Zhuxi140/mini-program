package com.zhuxi.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhuxi.Result.Result;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Exception.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({transactionalException.class})
    public Result<Void> handleException(transactionalException e) {
        String message = e.getMessage();
        return Result.error(message);
    }

    @ExceptionHandler({JwtException.class, JsonProcessingException.class })
    public String handleJwtException(JwtException e){
        log.warn("---- Jwt error ----");
        return "Jwt error :" + e.getMessage();
    }
}
