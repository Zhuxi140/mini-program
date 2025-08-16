package com.zhuxi.task;

import com.zhuxi.handler.BloomFilterManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class BloomTask {
    private final BloomFilterManager bloomFilterManager;

    private  boolean firstExecution = true;
    private  long lastExecutionTime = 0L;

    public BloomTask(BloomFilterManager bloomFilterManager) {
        this.bloomFilterManager = bloomFilterManager;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void checkAndRebuildBloomFilter() {
        long currentTime = System.currentTimeMillis();

        if (firstExecution) {
            // 第一次执行，记录时间
            lastExecutionTime = currentTime;
            firstExecution = false;
            return;
        }

        // 检查是否过了31天
        long THIRTY_ONE_DAYS_IN_MILLIS = 31L * 24 * 60 * 60 * 1000;
        if (currentTime - lastExecutionTime >= THIRTY_ONE_DAYS_IN_MILLIS) {
            log.info("开始重建布隆过滤器");
            bloomFilterManager.scheduledRebuild();
            lastExecutionTime = currentTime;
            log.info("结束重建布隆过滤器");
        }
    }
}
