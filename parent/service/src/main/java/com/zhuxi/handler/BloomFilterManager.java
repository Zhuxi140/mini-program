package com.zhuxi.handler;

import com.google.common.hash.BloomFilter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@SuppressWarnings("UnstableApiUsage")
public class BloomFilterManager {
    private final Map<String, BloomFilter<Long>> filters = new ConcurrentHashMap<>();

    public void addFilter(String filterName, BloomFilter<Long> filter){
        filters.put(filterName, filter);
    }

    public boolean mightContain(String filterName, Long value){  // 检测元素是否存在
        BloomFilter<Long> filter = filters.get(filterName);
        return  filter !=null && filter.mightContain(value);
    }

    public void put(String filterName, Long value){
        BloomFilter<Long> filter = filters.get(filterName);
        if (filter !=null)
            filter.put(value);
    }

}
