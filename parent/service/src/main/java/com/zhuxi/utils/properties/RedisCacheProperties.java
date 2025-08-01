package com.zhuxi.utils.properties;


import com.zhuxi.config.YamlFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
    private OrderCache orderCache = new OrderCache();

    private int maxAttempts;
    private long baseWaitMs;



    public static class ProductCache{
        private String StringPrefix;
        private String HashPrefix;
        private String SpecDetailPrefix;
        private String SpecMapProductPrefix;
        private String StockPrefix;
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

        public String getStockPrefix() {
            return StockPrefix;
        }

        public void setStockPrefix(String stockPrefix) {
            StockPrefix = stockPrefix;
        }

        public String getHashPrefix() {
            return HashPrefix;
        }

        public void setHashPrefix(String hashPrefix) {
            HashPrefix = hashPrefix;
        }

        public String getSpecDetailPrefix() {
            return SpecDetailPrefix;
        }

        public void setSpecDetailPrefix(String specDetailPrefix) {
            SpecDetailPrefix = specDetailPrefix;
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

        public String getSpecMapProductPrefix() {
            return SpecMapProductPrefix;
        }

        public void setSpecMapProductPrefix(String specMapProductPrefix) {
            SpecMapProductPrefix = specMapProductPrefix;
        }
    }

    public static  class AddressCache{
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

    public static class  OrderCache{
        private String OrderLockPrefix;
        private String OrderSortSetPrefix;
        private String OrderDetailHashPrefix;
        private String OrderGroupPrefix;
        private int OrderLockTTL;
        private int OrderSortSetTTL;
        private int OrderDetailHashTTL;
        private int OrderGroupTTL;

        public int getOrderLockTTL() {
            return OrderLockTTL;
        }

        public void setOrderLockTTL(int orderLockTTL) {
            OrderLockTTL = orderLockTTL;
        }

        public int getOrderSortSetTTL() {
            return OrderSortSetTTL;
        }

        public void setOrderSortSetTTL(int orderSortSetTTL) {
            OrderSortSetTTL = orderSortSetTTL;
        }

        public int getOrderDetailHashTTL() {
            return OrderDetailHashTTL;
        }

        public void setOrderDetailHashTTL(int orderDetailHashTTL) {
            OrderDetailHashTTL = orderDetailHashTTL;
        }

        public int getOrderGroupTTL() {
            return OrderGroupTTL;
        }

        public void setOrderGroupTTL(int orderGroupTTL) {
            OrderGroupTTL = orderGroupTTL;
        }

        public String getOrderSortSetPrefix() {
            return OrderSortSetPrefix;
        }

        public void setOrderSortSetPrefix(String orderSortSetPrefix) {
            OrderSortSetPrefix = orderSortSetPrefix;
        }

        public String getOrderDetailHashPrefix() {
            return OrderDetailHashPrefix;
        }

        public void setOrderDetailHashPrefix(String orderDetailHashPrefix) {
            OrderDetailHashPrefix = orderDetailHashPrefix;
        }

        public String getOrderGroupPrefix() {
            return OrderGroupPrefix;
        }

        public void setOrderGroupPrefix(String orderGroupPrefix) {
            OrderGroupPrefix = orderGroupPrefix;
        }

        public String getOrderLockPrefix() {
            return OrderLockPrefix;
        }

        public void setOrderLockPrefix(String orderLockPrefix) {
            OrderLockPrefix = orderLockPrefix;
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

    public OrderCache getOrderCache() {
        return orderCache;
    }

    public void setOrderCache(OrderCache orderCache) {
        this.orderCache = orderCache;
    }
}
