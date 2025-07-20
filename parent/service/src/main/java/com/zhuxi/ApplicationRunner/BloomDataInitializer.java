package com.zhuxi.ApplicationRunner;

import com.zhuxi.service.TxService.OrderTxService;
import com.zhuxi.service.TxService.ProductTxService;
import com.zhuxi.service.TxService.UserTxService;
import lombok.extern.slf4j.Slf4j;
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
