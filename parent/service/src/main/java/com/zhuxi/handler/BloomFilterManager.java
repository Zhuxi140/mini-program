package com.zhuxi.handler;

import com.google.common.hash.BloomFilter;
import com.zhuxi.ApplicationRunner.Data.Loader.BloomDataLoader;
import com.zhuxi.ApplicationRunner.Data.Loader.ReBuildBloom;
import com.zhuxi.config.BloomFilterConfig;
import com.zhuxi.handler.BloomFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Component
@SuppressWarnings("UnstableApiUsage")
public class BloomFilterManager {
    private final Map<String, BloomFilter<Long>> filters = new ConcurrentHashMap<>();
    private final Map<String, BloomFilter<String>> stringFilters = new ConcurrentHashMap<>();
    private final ReBuildBloom reBuildBloom;
    private final BloomFilterConfig bloomFilterConfig;

    // 新增工厂依赖
    private final BloomFilterFactory bloomFilterFactory;

    // 重建锁（防止并发重建）
    private final ReentrantLock rebuildLock = new ReentrantLock();

    // 容量阈值
    private static final double CAPACITY_THRESHOLD = 0.8;

    @Autowired
    public BloomFilterManager(BloomDataLoader bloomDataLoader, ReBuildBloom reBuildBloom, BloomFilterConfig bloomFilterConfig, BloomFilterFactory bloomFilterFactory) {
        this.reBuildBloom = reBuildBloom;
        this.bloomFilterConfig = bloomFilterConfig;
        this.bloomFilterFactory = bloomFilterFactory;
    }

    // 原有方法保持不变
    public void addFilterLong(String filterName, BloomFilter<Long> filter){
        filters.put(filterName, filter);
    }

    public void addFilterString(String filterName, BloomFilter<String> filter){
        stringFilters.put(filterName, filter);
    }

    public boolean mightContainLong(String filterName, Long value){
        BloomFilter<Long> filter = filters.get(filterName);
        return  filter !=null && filter.mightContain(value);
    }

    public boolean mightContainString(String filterName, String value){
        BloomFilter<String> filter = stringFilters.get(filterName);
        return  filter !=null && filter.mightContain(value);
    }

    public void putLong(String filterName, Long value){
        BloomFilter<Long> filter = filters.get(filterName);
        if (filter != null) {
            filter.put(value);
            checkAndRebuild(filterName, filter);
        }
    }

    public void putString(String filterName, String value){
        BloomFilter<String> filter = stringFilters.get(filterName);
        if (filter != null) {
            filter.put(value);
            checkAndRebuild(filterName, filter);
        }
    }

    //检查是否需要重建
    private void checkAndRebuild(String filterName, BloomFilter<?> filter) {
        // 计算当前负载率
        double loadFactor = (double) filter.approximateElementCount() /
                getExpectedCapacity(filterName);

        if (loadFactor > CAPACITY_THRESHOLD) {
            asyncRebuildFilter(filterName);
        }
    }

    @Async
    public void asyncRebuildFilter(String filterName) {
        rebuildFilter(filterName);
    }

    //重建过滤器
    public void rebuildFilter(String filterName) {
        // 获取重建锁
        if (!rebuildLock.tryLock()) {
            return;
        }

        try {
            // 判断是Long类型还是String类型
            if (filters.containsKey(filterName)) {
                // 创建新的布隆过滤器实例
                BloomFilter<Long> newFilter = bloomFilterFactory.createDefaultProductBloomFilter();

                reBuildBloom.setProductBloomFilter(newFilter);
                reBuildBloom.loadProductData();
                // 替换旧的过滤器
                filters.put(filterName, newFilter);
            } else if (stringFilters.containsKey(filterName)) {
                // 创建新的布隆过滤器实例
                BloomFilter<String> newFilter = bloomFilterFactory.createDefaultOrderBloomFilter();

                reBuildBloom.setOrderBloomFilter(newFilter);
                reBuildBloom.loadOrderData();

                // 替换旧的过滤器
                stringFilters.put(filterName, newFilter);
            }
        } finally {
            rebuildLock.unlock();
        }
    }


    private long getExpectedCapacity(String filterName) {
        // 这里可以根据filterName返回不同的预期容量
        // 默认返回10万容量
        if("product".equals(filterName)){
            return bloomFilterConfig.getProductConfig().getExpectedElements();
        }else if("order".equals(filterName)){
            return bloomFilterConfig.getOrderConfig().getExpectedElements();
        }
        return 100000;
    }

    //定时重建
    public void scheduledRebuild() {
        // 重建所有过滤器
        filters.keySet().forEach(this::rebuildFilter);
        stringFilters.keySet().forEach(this::rebuildFilter);
    }
}