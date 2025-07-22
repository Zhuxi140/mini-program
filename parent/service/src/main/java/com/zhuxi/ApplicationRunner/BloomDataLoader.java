package com.zhuxi.ApplicationRunner;

import com.google.common.hash.BloomFilter;
import com.zhuxi.handler.BloomFilterManager;
import com.zhuxi.service.TxService.OrderTxService;
import com.zhuxi.service.TxService.ProductTxService;
import com.zhuxi.service.TxService.UserTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.Order.BloomOrderDTO;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class BloomDataLoader {
    private final ProductTxService productTxService;
    private final UserTxService userTxService;
    private final OrderTxService orderTxService;
    private final BloomFilterManager bloomFilterManager;
    private final BloomFilter<Long> productBloomFilter;
    private final BloomFilter<Long> userBloomFilter;
    private final BloomFilter<Long> orderBloomFilter;

    public BloomDataLoader(
            ProductTxService productTxService,
            UserTxService userTxService,
            OrderTxService orderTxService,
            BloomFilterManager bloomFilterManager,
            @Qualifier("productBloomFilter") BloomFilter<Long> productBloomFilter,
            @Qualifier("userBloomFilter") BloomFilter<Long> userBloomFilter,
            @Qualifier("orderBloomFilter") BloomFilter<Long> orderBloomFilter
            )

    {
        this.productTxService = productTxService;
        this.userTxService = userTxService;
        this.orderTxService = orderTxService;
        this.productBloomFilter = productBloomFilter;
        this.bloomFilterManager = bloomFilterManager;
        this.orderBloomFilter = orderBloomFilter;
        this.userBloomFilter = userBloomFilter;
    }

    public void loadData() {
        loadProductData();
        loadOrderData();
        loadUserData();
        log.debug("所有布隆过滤器加载完成");
    }

    private void loadProductData(){
        bloomFilterManager.addFilterLong("product",productBloomFilter);
        int pageSize = 1000;
        Long lastId = 0L;
        while(true){
            List<Long> allProductId = productTxService.getAllProductId(lastId, pageSize + 1);
            if(allProductId.size() == pageSize + 1){
                allProductId = allProductId.subList(0, pageSize);
                lastId = allProductId.get(allProductId.size() - 1);
            }else{
                allProductId.forEach( p-> {
                    bloomFilterManager.putLong("product",p);
                });
                break;
            }
            allProductId.forEach( p-> {
                bloomFilterManager.putLong("product",p);
            });
        }

    }

    private void loadUserData(){
        bloomFilterManager.addFilterLong("user",userBloomFilter);
        int pageSize = 1000;
        Long lastId = 0L;
        while(true) {
            List<Long> allUserId = userTxService.getAllUserId(lastId, pageSize+1);
            if(allUserId.size() == pageSize + 1){
                allUserId = allUserId.subList(0, pageSize);
                lastId = allUserId.get(allUserId.size() - 1);
            }else{
                allUserId.forEach( p-> {
                    bloomFilterManager.putLong("user",p);
                });
                break;
            }
            allUserId.forEach( p-> {
                bloomFilterManager.putLong("user",p);
            });
        }
    }


    private void loadOrderData(){
        bloomFilterManager.addFilterLong("order",orderBloomFilter);
        int pageSize = 1000;
        Long lastId = 0L;
        while(true){
            List<BloomOrderDTO> allOrderId = orderTxService.getAllOrderId(lastId, pageSize+1);
            if(allOrderId.size() == pageSize + 1){
                allOrderId = allOrderId.subList(0, pageSize);
                lastId = allOrderId.get(allOrderId.size() - 1).getId();
            }else{
                allOrderId.forEach( p-> {
                    Long orderId = p.getId();
                    Long userId = p.getUserId();
                    bloomFilterManager.putLong("order",userId + orderId);
                });
                break;
            }
            //添加到布隆过滤器
            allOrderId.forEach( p-> {
                Long orderId = p.getId();
                Long userId = p.getUserId();
                bloomFilterManager.putLong("order",userId + orderId);
            });
        }
    }

}
