package com.zhuxi.handler;

import com.google.common.hash.BloomFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
// 忽略不稳定警告
@SuppressWarnings({"UnstableApiUsage","unchecked"})
public class BloomFilterFactory {
    private final ApplicationContext applicationContext;

    public BloomFilterFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public BloomFilter<Long> createDefaultProductBloomFilter() {
        return (BloomFilter<Long>)applicationContext.getBean("productBloomFilter",BloomFilter.class);
    }

    public BloomFilter<Long> createDefaultUserBloomFilter() {
        return (BloomFilter<Long>)applicationContext.getBean("userBloomFilter",BloomFilter.class);
    }

    // 使用默认配置创建订单布隆
    public BloomFilter<String> createDefaultOrderBloomFilter() {
        return (BloomFilter<String>)applicationContext.getBean("orderBloomFilter",BloomFilter.class);
    }
}
