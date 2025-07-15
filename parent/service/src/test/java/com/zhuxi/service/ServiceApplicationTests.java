package com.zhuxi.service;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


@SpringBootTest
class ServiceApplicationTests {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public ServiceApplicationTests(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Test
    void contextLoads() {

        ValueOperations<String, String> ValueOperations = stringRedisTemplate.opsForValue();
        ValueOperations.set("test", IdUtil.simpleUUID());
    }



}
