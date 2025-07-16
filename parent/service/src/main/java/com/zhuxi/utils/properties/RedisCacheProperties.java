package com.zhuxi.utils.properties;


import com.zhuxi.config.YamlFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "redis.cache")
@PropertySource(
        value="classpath:redis-cache.yaml",
        factory = YamlFactory.class
)
public class RedisCacheProperties {

    private ProductCache productCache = new ProductCache();




    public class ProductCache{
        private String KeyPrefix;
        private Long TTL;

        public String getKeyPrefix() {
            return KeyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            KeyPrefix = keyPrefix;
        }

        public Long getTTL() {
            return TTL;
        }

        public void setTTL(Long TTL) {
            this.TTL = TTL;
        }
    }

    public ProductCache getProductCache() {
        return productCache;
    }

    public void setProductCache(ProductCache productCache) {
        this.productCache = productCache;
    }
}
