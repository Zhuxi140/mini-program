package com.zhuxi.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhuxi.mapper.snowflake;
import com.zhuxi.utils.IdSnowFLake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;


@SpringBootTest
class ServiceApplicationTests {

    private final IdSnowFLake idSnowFLake;
    private final snowflake  snowflake;

    @Autowired
    ServiceApplicationTests(IdSnowFLake idSnowFLake, com.zhuxi.mapper.snowflake snowflake) {
        this.idSnowFLake = idSnowFLake;
        this.snowflake = snowflake;
    }

    @Test
    void contextLoads() {

        for (long i = 1; i <= 50; i++){
            Long idInt = idSnowFLake.getIdInt();
            if (snowflake.getUserId(i) == null){
                snowflake.updateUserId(i,idInt);
            }
        }

    }






}
