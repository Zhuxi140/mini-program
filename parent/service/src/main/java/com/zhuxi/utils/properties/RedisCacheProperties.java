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
    private AddressCache addressCache = new AddressCache();

    private int maxAttempts;
    private long baseWaitMs;



    public class ProductCache{
        private String StringPrefix;
        private String HashPrefix;
        private String SetPrefix;
        private String ListPrefix;
        private String ZSetPrefix;
        private int DetailTTL;
        private int CreateTTL;
        private int priceTTL;

        public String getStringPrefix() {
            return StringPrefix;
        }

        public void setStringPrefix(String stringPrefix) {
            StringPrefix = stringPrefix;
        }

        public String getHashPrefix() {
            return HashPrefix;
        }

        public void setHashPrefix(String hashPrefix) {
            HashPrefix = hashPrefix;
        }

        public String getSetPrefix() {
            return SetPrefix;
        }

        public void setSetPrefix(String setPrefix) {
            SetPrefix = setPrefix;
        }

        public String getListPrefix() {
            return ListPrefix;
        }

        public void setListPrefix(String listPrefix) {
            ListPrefix = listPrefix;
        }

        public String getZSetPrefix() {
            return ZSetPrefix;
        }

        public void setZSetPrefix(String ZSetPrefix) {
            this.ZSetPrefix = ZSetPrefix;
        }

        public int getDetailTTL() {
            return DetailTTL;
        }

        public void setDetailTTL(int detailTTL) {
            DetailTTL = detailTTL;
        }

        public int getCreateTTL() {
            return CreateTTL;
        }

        public void setCreateTTL(int createTTL) {
            CreateTTL = createTTL;
        }

        public int getPriceTTL() {
            return priceTTL;
        }

        public void setPriceTTL(int priceTTL) {
            this.priceTTL = priceTTL;
        }
    }

    public class AddressCache{
        private String StringPrefix;
        private String HashPrefix;
        private String SetPrefix;
        private String ListPrefix;
        private String ZSetPrefix;


        public String getStringPrefix() {
            return StringPrefix;
        }

        public void setStringPrefix(String stringPrefix) {
            StringPrefix = stringPrefix;
        }

        public String getHashPrefix() {
            return HashPrefix;
        }

        public void setHashPrefix(String hashPrefix) {
            HashPrefix = hashPrefix;
        }

        public String getSetPrefix() {
            return SetPrefix;
        }

        public void setSetPrefix(String setPrefix) {
            SetPrefix = setPrefix;
        }

        public String getListPrefix() {
            return ListPrefix;
        }

        public void setListPrefix(String listPrefix) {
            ListPrefix = listPrefix;
        }

        public String getZSetPrefix() {
            return ZSetPrefix;
        }

        public void setZSetPrefix(String ZSetPrefix) {
            this.ZSetPrefix = ZSetPrefix;
        }

    }

    public ProductCache getProductCache() {
        return productCache;
    }

    public void setProductCache(ProductCache productCache) {
        this.productCache = productCache;
    }

    public AddressCache getAddressCache() {
        return addressCache;
    }

    public void setAddressCache(AddressCache addressCache) {
        this.addressCache = addressCache;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getBaseWaitMs() {
        return baseWaitMs;
    }

    public void setBaseWaitMs(long baseWaitMs) {
        this.baseWaitMs = baseWaitMs;
    }
}
