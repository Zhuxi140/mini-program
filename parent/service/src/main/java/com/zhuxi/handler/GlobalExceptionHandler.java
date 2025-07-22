package com.zhuxi.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhuxi.Exception.*;
import com.zhuxi.Result.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({transactionalException.class})
    public Result<Void> handleException(transactionalException e) {
        log.warn("---- transactional error ----");
        String message = e.getMessage();
        return Result.error(message);
    }

    @ExceptionHandler({JwtException.class, JsonProcessingException.class })
    public Result<Void> handleJwtException(JwtException e){
        log.warn("---- Jwt error ----");
        log.warn("{}",e.getMessage());
        return Result.error("Jwt error :" + e.getMessage());
    }

    @ExceptionHandler(JsonException.class)
    public Result<Void> handlerJsonException(JsonException e){
        log.warn("---- Json error ----");
        log.warn("{}",e.getMessage());
        return Result.error("Json error :" + e.getMessage());
    }

    @ExceptionHandler(BloomFilterRejectException.class)
    public Result<Void> handlerBloomFilterRejectException(BloomFilterRejectException e){
        log.warn("---- BloomFilterReject Hinder ----");
        log.warn("{}",e.getMessage());
        return Result.error("BloomFilterReject error :" + e.getMessage());
    }

    @ExceptionHandler(DefenseException.class)
    public Result<Void> handlerDefenseException(DefenseException e){
        log.warn("---- Defense Hinder ----");
        log.warn("{}",e.getMessage());
        return Result.error("Defense error :" + e.getMessage());
    }
}
