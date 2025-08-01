package com.zhuxi.ApplicationRunner.Data;

import com.zhuxi.ApplicationRunner.Data.Loader.BloomDataLoader;
import com.zhuxi.ApplicationRunner.DataInitializer;
import org.springframework.stereotype.Component;

@Component
public class BloomDataInitializer implements DataInitializer {

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
