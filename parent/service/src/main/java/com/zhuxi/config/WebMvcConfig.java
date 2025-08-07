package com.zhuxi.config;

import com.zhuxi.Interceptor.JwtInterceptor;
import com.zhuxi.Interceptor.JwtInterceptorProperties;
import com.zhuxi.handler.UserIdArgumentResolver;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import com.zhuxi.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptorProperties jwtInterceptorProperties;
    private final JwtUtils jwtUtils;
    private final WechatAuthTxService wechatAuthTxService;
    private final LoginRedisCache loginRedisCache;

    public WebMvcConfig(JwtInterceptorProperties jwtInterceptorProperties, JwtUtils jwtUtils, WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache) {
        this.jwtInterceptorProperties = jwtInterceptorProperties;
        this.jwtUtils = jwtUtils;
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor(jwtInterceptorProperties, jwtUtils, loginRedisCache);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(jwtInterceptorProperties.getExcludePaths());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserIdArgumentResolver(jwtUtils, wechatAuthTxService,loginRedisCache));
    }



}