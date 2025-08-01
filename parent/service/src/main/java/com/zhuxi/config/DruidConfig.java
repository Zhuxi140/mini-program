package com.zhuxi.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;

@Configuration
public class DruidConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DruidDataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setInitialSize(5); // 初始化连接数
        druidDataSource.setMaxActive(20); // 最大连接数
        druidDataSource.setMinIdle(5); // 最小连接数
        druidDataSource.setMaxWait(60000); // 获取连接等待超时时间
        druidDataSource.setAsyncInit( true);
        try {
            druidDataSource.setFilters("stat,wall");
        } catch (SQLException e) {
            throw new RuntimeException("Druid DataSource 配置 Filters 失败",e);
        }

        return druidDataSource;
    }
}
