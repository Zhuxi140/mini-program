package com.zhuxi.handler;

import com.google.common.hash.BloomFilter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@SuppressWarnings("UnstableApiUsage")
public class BloomFilterManager {
    private final Map<String, BloomFilter<Long>> filters = new ConcurrentHashMap<>();
    private final Map<String,BloomFilter<String>> stringFilters = new ConcurrentHashMap<>();

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
        if (filter !=null)
            filter.put(value);
    }

    public void putString(String filterName, String value){
        BloomFilter<String> filter = stringFilters.get(filterName);
        if (filter !=null)
            filter.put(value);
    }

}
