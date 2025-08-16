package com.zhuxi.handler;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BloomLoading {
    private final Set<Long> loadingUsers = ConcurrentHashMap.newKeySet();

    public boolean markLoading(Long userId) {
        return loadingUsers.add(userId);
    }

    public void completeLoading(Long userId) {
        loadingUsers.remove(userId);
    }

    public boolean isLoading(Long userId) {
        return loadingUsers.contains(userId);
    }
}