package com.zhuxi.ApplicationRunner.Data.Loader;

import com.google.common.hash.BloomFilter;
import com.zhuxi.handler.BloomFilterManager;
import com.zhuxi.service.Tx.OrderTxService;
import com.zhuxi.service.Tx.ProductTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.Order.BloomOrderDTO;
import java.util.List;

@Slf4j
@Component
@SuppressWarnings("UnstableApiUsage")
public class ReBuildBloom {
    private final ProductTxService productTxService;
    private final OrderTxService orderTxService;
    private final BloomFilterManager bloomFilterManager;
    private BloomFilter<Long> productBloomFilter;
    private BloomFilter<String> orderBloomFilter;


    public ReBuildBloom(
            ProductTxService productTxService,
            OrderTxService orderTxService,
            @Lazy BloomFilterManager bloomFilterManager
    )
    {
        this.productTxService = productTxService;
        this.orderTxService = orderTxService;
        this.bloomFilterManager = bloomFilterManager;
    }

    public void loadProductData(){
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



    public void loadOrderData(){
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

    public BloomFilter<Long> getProductBloomFilter() {
        return productBloomFilter;
    }

    public void setProductBloomFilter(BloomFilter<Long> productBloomFilter) {
        this.productBloomFilter = productBloomFilter;
    }

    public void setOrderBloomFilter(BloomFilter<String> orderBloomFilter) {
        this.orderBloomFilter = orderBloomFilter;
    }
}
