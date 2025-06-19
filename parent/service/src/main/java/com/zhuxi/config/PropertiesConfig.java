package com.zhuxi.config;

import com.zhuxi.Interceptor.JwtInterceptorProperties;
import com.zhuxi.utils.properties.BCryptProperties;
import com.zhuxi.utils.properties.JwtProperties;
import com.zhuxi.utils.properties.OssProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(
        {
                JwtProperties.class,
                OssProperties.class,
                BCryptProperties.class,
                JwtInterceptorProperties.class
        }
)
@Configuration
public class PropertiesConfig {
}
