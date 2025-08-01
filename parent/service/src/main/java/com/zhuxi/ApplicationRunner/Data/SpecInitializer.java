package com.zhuxi.ApplicationRunner.Data;

import com.zhuxi.ApplicationRunner.Data.Loader.SpecDataLoader;
import com.zhuxi.ApplicationRunner.DataInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpecInitializer implements DataInitializer {
    private final SpecDataLoader specDataLoader;

    public SpecInitializer(SpecDataLoader specDataLoader) {
        this.specDataLoader = specDataLoader;
    }

    @Override
    public void initializeData() {
        long now = System.currentTimeMillis();
        specDataLoader.initializeData();
        log.info("Spec数据预加载成功,耗时:{}", System.currentTimeMillis() - now);
    }

    @Override
    public int getOrder() {
        return DataInitializer.super.getOrder();
    }
}
