package com.zhuxi.config;

import com.google.common.hash.BloomFilter;
import com.zhuxi.Filter.XSSFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<XSSFilter> xssFilter(){
        FilterRegistrationBean<XSSFilter> xss = new FilterRegistrationBean<>();
        xss.setFilter(new XSSFilter());
        xss.addUrlPatterns("/*");
        xss.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        xss.setName("XSSFilter");

        return xss;
    }


}
