package com.zhuxi.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuxi.Exception.JsonException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JacksonUtils {

    private static  ObjectMapper objectMapper;

    private final ObjectMapper springObjectMapper;

    public JacksonUtils(ObjectMapper springObjectMapper) {
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

    //将list转换为对象
    public static  <T> T listToObject(List<?> list, Class<T> clazz){
        return objectMapper.convertValue(list, clazz);
    }

    public static  <T> T objectToObject(Object object, TypeReference<T> clazz){
        return objectMapper.convertValue(object, clazz);
    }

    //filterNullFields
   public static Map<String, Object> filterNullFields(Object obj){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>(){});
   }
}
