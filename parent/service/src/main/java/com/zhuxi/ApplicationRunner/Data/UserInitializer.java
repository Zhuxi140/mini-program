package com.zhuxi.ApplicationRunner.Data;

import com.zhuxi.ApplicationRunner.Data.Loader.UserDataLoader;
import com.zhuxi.ApplicationRunner.DataInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserInitializer implements DataInitializer {
    private UserDataLoader userDataLoader;

    public UserInitializer(UserDataLoader userDataLoader) {
        this.userDataLoader = userDataLoader;
    }

    @Override
    public void initializeData() {
        long now = System.currentTimeMillis();
        userDataLoader.init();
        log.info("user数据预加载成功,耗时:{}", System.currentTimeMillis() - now);
    }

    @Override
    public int getOrder() {
        return DataInitializer.super.getOrder();
    }
}
