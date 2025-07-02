package com.zhuxi.config;

import com.zhuxi.Interceptor.JwtInterceptor;
import com.zhuxi.Interceptor.JwtInterceptorProperties;
import com.zhuxi.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public JwtInterceptor jwtInterceptor(JwtInterceptorProperties jwtInterceptorProperties, JwtUtils jwtUtils)
    {
        return new JwtInterceptor(jwtInterceptorProperties,jwtUtils);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

}

