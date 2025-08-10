package com.zhuxi.ApplicationRunner.Data.Loader;

import com.zhuxi.service.Cache.CartRedisCache;
import com.zhuxi.service.Tx.CartTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.Cart.CartRedisDTO;

import java.util.List;

@Component
@Slf4j
public class CartDataLoader {

    private final CartRedisCache cartRedisCache;
    private final CartTxService cartTxService;
    @Value("${init-Data.page-size}")
    private int pageSize;

    public CartDataLoader(CartRedisCache cartRedisCache, CartTxService cartTxService) {
        this.cartRedisCache = cartRedisCache;
        this.cartTxService = cartTxService;
    }

    public void initializeData() {
        long now = System.currentTimeMillis();
        initData();
        log.info("Cart数据预加载成功,耗时:{}", System.currentTimeMillis() - now);
    }

    public void initData(){
        int batchSize = pageSize;
        Long lastId = 0L;
        while (true) {
            List<Long> userIds = cartTxService.getUserIds(lastId, batchSize + 1);
            if (userIds.size() == batchSize + 1){
                lastId = userIds.get(batchSize);
                userIds = userIds.subList(0, batchSize);
                getCarList(userIds);
            }else {
                if (userIds.isEmpty()){
                    break;
                }
                getCarList(userIds);
                break;
            }
        }
    }

    public void getCarList(List<Long> userIds){
        for (Long userId : userIds) {
            List<CartRedisDTO> listCartOne = cartTxService.getListCartOne(userId);
            if (listCartOne.isEmpty()){
                continue;
            }
            cartRedisCache.syncCartInit(listCartOne, userId);
        }
    }
}
