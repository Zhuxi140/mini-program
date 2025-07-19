package com.zhuxi.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuxi.Exception.JsonException;
import jakarta.annotation.PostConstruct;
import org.apache.catalina.connector.Response;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonUtils {

    private static  ObjectMapper objectMapper;

    private final ObjectMapper springObjectMapper;

    public JsonUtils( ObjectMapper springObjectMapper) {
        this.springObjectMapper = springObjectMapper;
    }

    @PostConstruct
    public void init(){
        objectMapper = springObjectMapper;
    }





    //将Json字符串转换成对象
    public static <T> T jsonToObject(String jsonRaw, Class<T> clazz){
        try {
            return objectMapper.readValue(jsonRaw, clazz);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }

    public static <T> T jsonToObject(String jsonRaw, TypeReference<T> Type){
        try {
            return objectMapper.readValue(jsonRaw, Type);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }


    //将对象转换成Json字符串
    public static String objectToJson(Object obj){
        try {
            return  objectMapper.writeValueAsString( obj);
        } catch (JsonProcessingException e) {
            throw new JsonException(e.getMessage());
        }
    }

    //将map转换为对象
    public static <T> T mapToObject(Map<?,?> map, Class<T> clazz){
        return objectMapper.convertValue(map, clazz);
    }



}
