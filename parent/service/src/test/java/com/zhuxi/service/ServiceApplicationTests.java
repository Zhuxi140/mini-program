package com.zhuxi.service;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ServiceApplicationTests {

    @Test
    void contextLoads() {

        long l = IdUtil.getSnowflake(1, 1).nextId();
        String format = String.format("%s%d", "OD", l);
        System.out.println(format);
    }



}
