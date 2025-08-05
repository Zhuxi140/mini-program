package com.zhuxi.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhuxi.Exception.MQException;
import com.zhuxi.mapper.snowflake;
import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.utils.IdSnowFLake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;

import javax.sql.DataSource;
import java.util.List;


@SpringBootTest
class ServiceApplicationTests {
    private final TaskScheduler taskScheduler;
    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;

    @Autowired
    public ServiceApplicationTests(TaskScheduler taskScheduler, OrderTxService orderTxService, OrderRedisCache orderRedisCache) {
        this.taskScheduler = taskScheduler;
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
    }

    @Test
    void contextLoads() {

                Long orderId = orderRedisCache.getOrderIdBySn("OD1952616585969668097");
                if (orderId == null) {
                    orderId = orderTxService.getOrderIdByOrderSn("OD1952616585969668097");
                }
                boolean orderStatus = orderTxService.getOrderStatus(orderId);
                if (orderStatus) {
                    return;
                }else{
                    orderTxService.concealOrderL(orderId);
                }


    }






}
