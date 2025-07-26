package com.zhuxi.ApplicationRunner;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Component
public class InitializationCoordinator implements ApplicationRunner {

    private final ConnectionPoolPreheater  SqlPreheater;
    private final List<DataInitializer> initializers;
    private final DruidDataSource dataSource;
    private final RedistPreheater noSqlPreheater;
    private final Executor asyncExecutor;

    public InitializationCoordinator(
            ConnectionPoolPreheater SqlPreheater,
            List<DataInitializer> initializers,
            DruidDataSource dataSource,
            RedistPreheater noSqlPreheater,
            @Qualifier("initAsyncExecutor") Executor asyncExecutor
    ) {
        this.SqlPreheater = SqlPreheater;
        this.initializers = initializers;
        this.dataSource = dataSource;
        this.noSqlPreheater = noSqlPreheater;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void run(ApplicationArguments args){
        try{
            CompletableFuture.allOf(
                     runAsync(this::preConnectionPool,asyncExecutor),
                    runAsync(this::preRedisPool,asyncExecutor)
            ).thenRun(this::execute)
             .exceptionally(ex->{
                 log.error("数据预热加载失败",ex);
                 return null;
             });

        }catch (Exception e){
            log.error("初始化数据源失败",e);
        }
    }

    private void preConnectionPool(){
        long now = System.currentTimeMillis();
        SqlPreheater.preheatDruidPool(dataSource,dataSource.getMinIdle(),"SELECT 1");
        log.info("数据库数据预热完成,耗时{}ms",System.currentTimeMillis()-now);
    }

    private void preRedisPool(){
        long now = System.currentTimeMillis();
        noSqlPreheater.ensureRedisReady();
        log.info("Redis数据预热完成,耗时{}ms",System.currentTimeMillis()-now);
    }

    private void execute(){
        initializers.sort(Comparator.comparingInt(DataInitializer::getOrder));
        for(DataInitializer initializer:initializers){
            String name = initializer.getClass().getName();
            try{
                initializer.initializeData();
            }catch (Exception e){
                log.error("初始化器: {} 执行失败",name,e);
            }
        }
    }
}
