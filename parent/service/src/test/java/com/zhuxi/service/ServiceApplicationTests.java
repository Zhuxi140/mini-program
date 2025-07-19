package com.zhuxi.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.zhuxi.service.RedisCache.ProductRedisCache;
import com.zhuxi.utils.IdSnowFLake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductOverviewVO;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class ServiceApplicationTests {
    private DataSource dataSource;

    @Autowired
    public ServiceApplicationTests(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Test
    void contextLoads() {

        String name = dataSource.getClass().getName();
        DruidDataSource source = (DruidDataSource) dataSource;
        int minIdle = source.getMinIdle();
        System.out.println("minIdle = " + minIdle);
    }






}
