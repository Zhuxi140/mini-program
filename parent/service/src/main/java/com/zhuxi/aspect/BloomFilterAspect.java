package com.zhuxi.aspect;

import com.google.common.hash.BloomFilter;
import com.zhuxi.Exception.BloomFilterRejectException;
import com.zhuxi.annotation.BloomFilterCheck;
import com.zhuxi.handler.BloomFilterManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Aspect
public class BloomFilterAspect {


    private final BloomFilterManager BFM;

    public BloomFilterAspect(BloomFilterManager BFM) {
        this.BFM = BFM;
    }

    @Around("@annotation(bloomFilterCheck)")
    public Object checkBloomFilter(ProceedingJoinPoint joinPoint, BloomFilterCheck bloomFilterCheck) throws Throwable {
        String bloomFilterName = bloomFilterCheck.BloomFilterName();
        if ("order".equals(bloomFilterName)){
            String key1 = getKeyFromParamsToString(joinPoint,bloomFilterCheck.key1());
            Long key2 = getKeyFromParamsToLong(joinPoint,bloomFilterCheck.key2());
            String l = orderValidate(key2, key1);
            checkKeys(bloomFilterName,l,bloomFilterCheck);
            return joinPoint.proceed();
        }
        Long key1 = getKeyFromParamsToLong(joinPoint,bloomFilterCheck.key1());
        checkKeys(bloomFilterName,key1,bloomFilterCheck);
        return joinPoint.proceed();
    }



    // 获取参数
    private Long getKeyFromParamsToLong(ProceedingJoinPoint joinPoint, String key){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取方法签名
        String[] parameterNames = signature.getParameterNames(); // 获取参数名称
        Object[] args = joinPoint.getArgs(); // 获取参数值

        for (int i = 0; i < parameterNames.length; i++){
            if (parameterNames[i].equals(key)){
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                } else if (arg instanceof Number) {
                    return ((Number) arg).longValue();
                } else if (arg instanceof String) {
                    return Long.parseLong((String) arg);
                } else {
                    throw new IllegalArgumentException("无法转换为Long: " + arg.getClass());
                }
            }
        }
        throw new IllegalArgumentException("No parameter named " + key);
    }

    private String getKeyFromParamsToString(ProceedingJoinPoint joinPoint, String key){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取方法签名
        String[] parameterNames = signature.getParameterNames(); // 获取参数名称
        Object[] args = joinPoint.getArgs(); // 获取参数值
        for (int i = 0; i < parameterNames.length; i++){
            if (parameterNames[i].equals(key)){
                Object arg = args[i];
                if (arg instanceof String) {
                    return (String) arg;
                } else {
                    throw new IllegalArgumentException("无法转换为Long: " + arg.getClass());
                }
            }
        }
        throw new IllegalArgumentException("No parameter named " + key);
    }


    private String orderValidate(Long userId,String OrderSn){
        if (OrderSn == null || userId == null){
            throw new BloomFilterRejectException("orderId/userId不能为空");
        }
        return userId + OrderSn;
    }

    private void checkKeys(String bloomFilterName, Object key,BloomFilterCheck bloomFilterCheck){
        boolean b = false;
        if (key instanceof String){
            b = BFM.mightContainString(bloomFilterName, (String) key);
        }else if (key instanceof Long){
            b = BFM.mightContainLong(bloomFilterName, (Long) key);
        }
        if (!b && bloomFilterCheck.rejectIfMiss()){
            throw new BloomFilterRejectException("请求的资源不存在或无权访问");
        }
    }
}
