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
        Long key1 = getKeyFromParams(joinPoint,bloomFilterCheck.key1());
        String bloomFilterName = bloomFilterCheck.BloomFilterName();
        if ("order".equals(bloomFilterName)){
            Long key2 = getKeyFromParams(joinPoint,bloomFilterCheck.key2());
            Long l = orderValidate(key2, key1);
            checkKeys(bloomFilterName,l,bloomFilterCheck);
            return joinPoint.proceed();
        }

        checkKeys(bloomFilterName,key1,bloomFilterCheck);
        return joinPoint.proceed();
    }



    // 获取参数
    private Long getKeyFromParams(ProceedingJoinPoint joinPoint, String key){
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


    private Long orderValidate(Long userId,Long orderId){
        if (orderId == null || userId == null){
            throw new BloomFilterRejectException("orderId/userId不能为空");
        }
        return userId + orderId;
    }

    private void checkKeys(String bloomFilterName, Long key,BloomFilterCheck bloomFilterCheck){
        boolean b = BFM.mightContainLong(bloomFilterName, key);
        if (!b && bloomFilterCheck.rejectIfMiss()){
            throw new BloomFilterRejectException("请求的资源不存在或无权访问");
        }
    }
}
