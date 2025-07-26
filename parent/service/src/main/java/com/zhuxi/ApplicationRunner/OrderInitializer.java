package com.zhuxi.ApplicationRunner;

import org.springframework.stereotype.Component;

@Component
public class OrderInitializer implements DataInitializer{
    private final OrderDataLoader orderDataLoader;

    public OrderInitializer(OrderDataLoader orderDataLoader) {
        this.orderDataLoader = orderDataLoader;
    }

    @Override
    public void initializeData() {
        orderDataLoader.initializeData();
    }

    @Override
    public int getOrder() {
        return DataInitializer.super.getOrder();
    }
}
