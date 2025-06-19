package com.zhuxi.Interceptor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "jwt-properties")
public class JwtInterceptorProperties {
    private List<String> excludePaths;

    public JwtInterceptorProperties(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }
}
