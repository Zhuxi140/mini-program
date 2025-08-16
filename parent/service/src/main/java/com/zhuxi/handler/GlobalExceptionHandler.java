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
        String message = e.getMessage();
        log.warn("error : {}",message);
        return Result.error(message);
    }

    @ExceptionHandler({JsonException.class,LoginException.class,WechatException.class,OSSException.class})
    public Result<Void> handleOtherException(Exception e){
        log.warn("error :{}",e.getMessage());
        return Result.error("error :" + e.getMessage());
    }
    @ExceptionHandler({BloomFilterRejectException.class,DefenseException.class,JwtException.class})
    public Result<Void> handlerSafeException(Exception e){
        log.warn("error : {}",e.getMessage());
        return Result.error("safe error :" + e.getMessage());
    }

}
