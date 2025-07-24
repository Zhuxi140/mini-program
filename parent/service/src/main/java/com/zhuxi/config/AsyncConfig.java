package com.zhuxi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "orderAsyncExecutor")
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 线程池维护的线程数，默认10
        executor.setMaxPoolSize(50); //
        executor.setQueueCapacity(200);// 线程池所使用的缓冲队列，默认为1000
        executor.setThreadNamePrefix("Async-");
        executor.initialize();

        return executor;
    }

    @Bean("initAsyncExecutor")
    public Executor initAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 根据初始化任务量调整
        executor.setMaxPoolSize(8);  // 避免占用过多资源
        executor.setQueueCapacity(50); // 防止积压
        executor.setThreadNamePrefix("Async-Init-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); // 初始化失败应直接报错
        executor.initialize();
        return executor;
    }

}
