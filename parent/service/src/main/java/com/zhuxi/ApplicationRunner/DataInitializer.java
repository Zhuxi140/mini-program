package com.zhuxi.ApplicationRunner;

import org.springframework.core.Ordered;

public interface DataInitializer {

    void initializeData();

    default  int getOrder(){
        return Ordered.LOWEST_PRECEDENCE;
    }
}
