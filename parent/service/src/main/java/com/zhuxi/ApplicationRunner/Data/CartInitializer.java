package com.zhuxi.ApplicationRunner.Data;

import com.zhuxi.ApplicationRunner.Data.Loader.CartDataLoader;
import com.zhuxi.ApplicationRunner.DataInitializer;
import org.springframework.stereotype.Component;

@Component
public class CartInitializer implements DataInitializer {
    private final CartDataLoader cartDataLoader;

    public CartInitializer(CartDataLoader cartDataLoader) {
        this.cartDataLoader = cartDataLoader;
    }

    @Override
    public void initializeData() {
        cartDataLoader.initializeData();
    }

    @Override
    public int getOrder() {
        return DataInitializer.super.getOrder();
    }
}
