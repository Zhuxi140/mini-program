package com.zhuxi.Result;

import com.zhuxi.Constant.MessageReturn;
import io.swagger.v3.oas.annotations.media.Schema;

public class Result<T> {
    @Schema(description = "响应信息")
    private String msg;
    @Schema(description = "状态码，200成功，500失败")
    private int code;  // 200成功 500失败
    @Schema(description = "响应数据")
    private T data;


    public Result(String msg, int code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public Result(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public Result() {
    }

    // 快速返回错误
    public static Result error(){
        return new Result(MessageReturn.OPERATION_ERROR,500);
    }

    //快速返回成功
    public static Result success(){
        return new Result(MessageReturn.OPERATION_SUCCESS,200);
    }

    //返回带数据的成功
    public static <T> Result<T> success(String message, T data){
        return new Result(MessageReturn.OPERATION_SUCCESS,200,data);
    }

    public static <T> Result<T> success(T data){
        return new Result(MessageReturn.OPERATION_SUCCESS,200,data);
    }

    // 返回带数据的错误
    public static <T> Result<T> error(String message, T data){
        return new Result(message,500,data);
    }

    //返回仅带错误信息
    public static <T> Result<T> error(String message){
        return new Result(message,500);
    }

    //返回仅带成功信息
    public static <T> Result<T> success(String message){
        return new Result(message,200);
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
