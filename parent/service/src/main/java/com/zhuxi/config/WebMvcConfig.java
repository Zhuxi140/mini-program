package com.zhuxi.config;

import com.zhuxi.Interceptor.JwtInterceptor;
import com.zhuxi.Interceptor.JwtInterceptorProperties;
import com.zhuxi.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebMvcConfig {

    @Bean
    public JwtInterceptor jwtInterceptor(JwtInterceptorProperties jwtInterceptorProperties, JwtUtils jwtUtils)
    {
        return new JwtInterceptor(jwtInterceptorProperties,jwtUtils);
    }

}
