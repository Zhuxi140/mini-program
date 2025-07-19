package com.zhuxi.ApplicationRunner;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ProductInitializer implements DataInitializer {

    private final ProductDataLoader productDataLoader;

    public ProductInitializer(ProductDataLoader productDataLoader) {
        this.productDataLoader = productDataLoader;
    }

    @Override
    public void initializeData() {
        productDataLoader.initializeData();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
