package com.zhuxi.ApplicationRunner.Data.Loader;

import com.google.common.hash.BloomFilter;
import com.zhuxi.handler.BloomFilterManager;
import com.zhuxi.service.Tx.OrderTxService;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.service.Tx.UserTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.Order.BloomOrderDTO;
import java.util.List;

@Slf4j
@Component
@SuppressWarnings("UnstableApiUsage")
public class BloomDataLoader {
    private final ProductTxService productTxService;
    private final OrderTxService orderTxService;
    private final BloomFilterManager bloomFilterManager;
    private final BloomFilter<Long> productBloomFilter;
    private final BloomFilter<String> orderBloomFilter;
    private final BloomFilter<Long> userBloomFilter;


    @Autowired
    public BloomDataLoader(
            ProductTxService productTxService,
            OrderTxService orderTxService,
            BloomFilterManager bloomFilterManager,
            @Qualifier("productBloomFilter") BloomFilter<Long> productBloomFilter,
            @Qualifier("orderBloomFilter") BloomFilter<String> orderBloomFilter, BloomFilter<Long> userBloomFilter
    )
    {
        this.productTxService = productTxService;
        this.orderTxService = orderTxService;
        this.productBloomFilter = productBloomFilter;
        this.bloomFilterManager = bloomFilterManager;
        this.orderBloomFilter = orderBloomFilter;
        this.userBloomFilter = userBloomFilter;
    }


    public void loadData() {
        loadProductData();
        loadUserData();
        loadOrderData();
        log.info("所有布隆过滤器加载完成");
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

    public void loadUserData(){
        bloomFilterManager.addFilterLong("user",userBloomFilter);
        int pageSize = 1000;
        Long lastId = 0L;
        while(true){
            List<Long> userIdList = orderTxService.getUserIdList(lastId, pageSize + 1);
            if(userIdList.size() == pageSize + 1){
                lastId = userIdList.get(pageSize);
                userIdList = userIdList.subList(0, pageSize);
            }else{
                userIdList.forEach( p-> {
                    bloomFilterManager.putLong("user",p);
                });
                break;
            }
            userIdList.forEach( p-> {
                bloomFilterManager.putLong("user",p);
            });
        }
    }



    private void loadOrderData(){
        bloomFilterManager.addFilterString("order",orderBloomFilter);
        int pageSize = 1000;
        Long lastId = 0L;
        while(true){
            List<BloomOrderDTO> allOrderId = orderTxService.getAllOrderId(lastId, pageSize+1);
            if(allOrderId.size() == pageSize + 1){
                lastId = allOrderId.get(pageSize).getId();
                allOrderId = allOrderId.subList(0, pageSize);
            }else{
                allOrderId.forEach( p-> {
                    String orderSn = p.getOrderSn();
                    Long userId = p.getUserId();
                    bloomFilterManager.putString("order",userId + orderSn);
                });
                break;
            }
            //添加到布隆过滤器
            allOrderId.forEach( p-> {
                String orderSn = p.getOrderSn();
                Long userId = p.getUserId();
                bloomFilterManager.putString("order",userId + orderSn);
            });
        }
    }

}
