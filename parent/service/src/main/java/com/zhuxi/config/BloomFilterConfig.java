package com.zhuxi.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "bloom-filter")
@SuppressWarnings("UnstableApiUsage")
public class BloomFilterConfig {
    private final ProductConfig productConfig = new ProductConfig();
    private final UserConfig userConfig = new UserConfig();
    private final OrderConfig orderConfig = new OrderConfig();

    // 商品过滤器
    @Bean("productBloomFilter")
    public BloomFilter<Long> productBloomFilter() {
        return BloomFilter.create(
                Funnels.longFunnel(),
                productConfig.expectedElements,
                productConfig.fpp
        );
    }

    // 用户过滤器
    @Bean("userBloomFilter")
    public BloomFilter<Long> userBloomFilter() {
        return BloomFilter.create(
                Funnels.longFunnel(),
                userConfig.expectedElements,
                userConfig.fpp
        );
    }

    // 订单过滤器
    @Bean("orderBloomFilter")
    public BloomFilter<String> orderBloomFilter() {
        return BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                orderConfig.expectedElements,
                orderConfig.fpp
        );
    }


    public static class ProductConfig {
        private long expectedElements;
        private double fpp;

        public long getExpectedElements() {
            return expectedElements;
        }

        public void setExpectedElements(long expectedElements) {
            this.expectedElements = expectedElements;
        }

        public double getFpp() {
            return fpp;
        }

        public void setFpp(double fpp) {
            this.fpp = fpp;
        }
    }
    public static class UserConfig {
        private long expectedElements;
        private double fpp;

        public long getExpectedElements() {
            return expectedElements;
        }

        public void setExpectedElements(long expectedElements) {
            this.expectedElements = expectedElements;
        }

        public double getFpp() {
            return fpp;
        }

        public void setFpp(double fpp) {
            this.fpp = fpp;
        }
    }
    public static class OrderConfig {
        private long expectedElements;
        private double fpp;

        public long getExpectedElements() {
            return expectedElements;
        }

        public void setExpectedElements(long expectedElements) {
            this.expectedElements = expectedElements;
        }

        public double getFpp() {
            return fpp;
        }

        public void setFpp(double fpp) {
            this.fpp = fpp;
        }
    }


    public ProductConfig getProductConfig() {
        return productConfig;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public OrderConfig getOrderConfig() {
        return orderConfig;
    }
}
