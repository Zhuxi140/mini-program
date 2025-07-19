package com.zhuxi.ApplicationRunner;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class InitializationCoordinator implements ApplicationRunner {

    private final ConnectionPoolPreheater  SqlPreheater;
    private final List<DataInitializer> initializers;
    private final DruidDataSource dataSource;
    private final RedistPreheater noSqlPreheater;

    public InitializationCoordinator(ConnectionPoolPreheater SqlPreheater, List<DataInitializer> initializers, DruidDataSource dataSource, RedistPreheater noSqlPreheater) {
        this.SqlPreheater = SqlPreheater;
        this.initializers = initializers;
        this.dataSource = dataSource;
        this.noSqlPreheater = noSqlPreheater;
    }

    @Override
    public void run(ApplicationArguments args){
        try{
            preConnectionPool();
            preRedisPool();
            execute();

        }catch (Exception e){
            log.error("初始化数据源失败",e);
        }
    }

    private void preConnectionPool(){
        SqlPreheater.preheatDruidPool(dataSource,dataSource.getMinIdle(),"SELECT 1");
    }

    private void preRedisPool(){
        noSqlPreheater.ensureRedisReady();
    }

    private void execute(){
        initializers.sort(Comparator.comparingInt(DataInitializer::getOrder));

        for(DataInitializer initializer:initializers){
            String name = initializer.getClass().getName();
            log.debug("开始执行初始化器: {}",name);
            long startTime = System.currentTimeMillis();

            try{
                initializer.initializeData();
                long finishTime = System.currentTimeMillis();
                log.debug("初始化器: {} 执行完成，耗时: {}ms",name,finishTime - startTime);

            }catch (Exception e){
                log.error("初始化器: {} 执行失败",name,e);
            }
        }
    }
}
