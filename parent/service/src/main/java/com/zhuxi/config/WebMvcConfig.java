package com.zhuxi.config;

import com.zhuxi.ApplicationRunner.Data.Loader.ReBuildBloom;
import com.zhuxi.Interceptor.JwtInterceptor;
import com.zhuxi.Interceptor.JwtInterceptorProperties;
import com.zhuxi.Interceptor.UserAccessInterceptor;
import com.zhuxi.handler.AdminIdArgumentResolver;
import com.zhuxi.handler.BloomFilterManager;
import com.zhuxi.handler.BloomLoading;
import com.zhuxi.handler.UserIdArgumentResolver;
import com.zhuxi.service.Cache.AdminCache;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import com.zhuxi.utils.JwtUtils;
import com.zhuxi.utils.properties.UserAccessPathProperties;
import org.springframework.beans.factory.annotation.Value;
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
    private final AdminCache adminCache;
    private final BloomFilterManager bloomFilterManager;
    private final ReBuildBloom reBuildBloom;
    private final BloomLoading bloomLoading;
    private final UserAccessPathProperties userAccessPathProperties;


    public WebMvcConfig(JwtInterceptorProperties jwtInterceptorProperties, JwtUtils jwtUtils, WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache, AdminCache adminCache, BloomFilterManager bloomFilterManager, ReBuildBloom reBuildBloom, BloomLoading bloomLoading, UserAccessPathProperties userAccessPathProperties) {
        this.jwtInterceptorProperties = jwtInterceptorProperties;
        this.jwtUtils = jwtUtils;
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
        this.adminCache = adminCache;
        this.bloomFilterManager = bloomFilterManager;
        this.reBuildBloom = reBuildBloom;
        this.bloomLoading = bloomLoading;
        this.userAccessPathProperties = userAccessPathProperties;
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor(jwtUtils, loginRedisCache, adminCache);
    }

    @Bean
    public UserAccessInterceptor UserAccessInterceptor() {
        return new UserAccessInterceptor(bloomFilterManager, wechatAuthTxService, loginRedisCache, reBuildBloom, bloomLoading);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(jwtInterceptorProperties.getExcludePaths());

        registry.addInterceptor(UserAccessInterceptor())
                .addPathPatterns(userAccessPathProperties.getPaths());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AdminIdArgumentResolver(jwtUtils));
        resolvers.add(new UserIdArgumentResolver(jwtUtils, wechatAuthTxService,loginRedisCache));
    }





}