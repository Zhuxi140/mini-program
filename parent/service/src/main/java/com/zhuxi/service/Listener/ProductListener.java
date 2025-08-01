package com.zhuxi.service.Listener;

import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.product.*;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;

import java.util.List;

@Slf4j
@Component
public class ProductListener {
    private final ProductRedisCache productRedisCache;
    private final ProductTxService productTxService;

    public ProductListener(ProductRedisCache productRedisCache, ProductTxService productTxService) {
        this.productRedisCache = productRedisCache;
        this.productTxService = productTxService;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "product.spec.New.queue", durable = "true"),
            exchange = @Exchange(name = "product.spec.exchange"),
            key = "new"
    ))
    public void NewProductSpecListener(Long productId){

        ProductDetailVO product = productTxService.getListProduct(productId);
        productRedisCache.syncProductOne(product);
        SpecRedisDTO spec = productTxService.getSpec(productId);
        productRedisCache.syncSpecInit(List.of(spec));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "product.spec.Already.queue", durable = "true"),
            exchange = @Exchange(name = "product.spec.exchange"),
            key = "Already"
    ))
    public void alreadyProductSpecListener(ProductUpdateDTO productUpdateDTO){


    }


}
