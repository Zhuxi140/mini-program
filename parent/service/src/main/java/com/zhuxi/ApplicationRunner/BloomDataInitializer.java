package com.zhuxi.ApplicationRunner;

import org.springframework.stereotype.Component;

@Component
public class BloomDataInitializer implements DataInitializer{

    private final BloomDataLoader bloomDataLoader;

    public BloomDataInitializer(BloomDataLoader bloomDataLoader) {
        this.bloomDataLoader = bloomDataLoader;
    }

    @Override
    public void initializeData() {
        bloomDataLoader.loadData();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
