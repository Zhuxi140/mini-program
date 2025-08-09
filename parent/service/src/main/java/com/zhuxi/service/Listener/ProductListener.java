package com.zhuxi.service.Listener;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.product.*;
import src.main.java.com.zhuxi.pojo.VO.Product.ProductDetailVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProductListener {
    private final ProductRedisCache productRedisCache;
    private final ProductTxService productTxService;
    private final RedisUntil redisUntil;

    public ProductListener(ProductRedisCache productRedisCache, ProductTxService productTxService, RedisUntil redisUntil) {
        this.productRedisCache = productRedisCache;
        this.productTxService = productTxService;
        this.redisUntil = redisUntil;
    }



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "product.spec.New.queue", durable = "true",
            arguments = {
                    @Argument(name = "x-dead-letter-exchange",value = "dead.queue.product.exchange"),
                    @Argument(name = "x-dead-letter-routing-key",value = "new")
            }
            ),
            exchange = @Exchange(name = "product.spec.exchange"),
            key = "new"
    ),
            containerFactory = "auto")
    public void NewProductSpecListener(Long productId,@Header(AmqpHeaders.MESSAGE_ID) String messageId){
        if (redisUntil.hsaKey("messageId:"+ messageId)) {
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        redisUntil.setStringValue("messageId:"+ messageId,"1",24, TimeUnit.HOURS);

        ProductDetailVO product = productTxService.getListProductMQ(productId);
        productRedisCache.syncProductOne(product);
        Long saleProductSnowFlake = productTxService.getSaleProductIdOne(productId);
        List<SpecRedisDTO> spec = productTxService.getSpec(List.of(productId));
        PIdSnowFlake pIdSnowFlake = new PIdSnowFlake(saleProductSnowFlake, saleProductSnowFlake);
        productRedisCache.syncSpecInit(spec,List.of(pIdSnowFlake));
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "product.spec.Already.queue", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange",value = "dead.queue.product.exchange"),
                            @Argument(name = "x-dead-letter-routing-key",value = "Already")
                    }
            ),
            exchange = @Exchange(name = "product.spec.exchange"),
            key = "Already"
    ),
            containerFactory = "auto"
    )
    public void alreadyProductSpecListener(ProductUpdateDTO productUpdateDTO,
                                           @Header("spec-is-null") boolean specIsNull,
                                           @Header(AmqpHeaders.MESSAGE_ID) String messageId
                                           ){

        if (redisUntil.hsaKey("messageId:"+ messageId)) {
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        redisUntil.setStringValue("messageId:"+ messageId,"1",24, TimeUnit.HOURS);
        ProductBaseUpdateDTO base = productUpdateDTO.getBase();
        Long productId = base.getId();
        if (productId == null){
            throw new MQException(MessageReturn.PRODUCT_ID_IS_NULL);
        }
        Long snowFlakeById = productTxService.getProductSnowFlakeById(productId);
        Map<String, Object> Map = JacksonUtils.filterNullFields(base);
        Map.remove("id");
        Map.remove("supplierId");
        productRedisCache.SyncProductMQ(Map,snowFlakeById);

        if (!specIsNull){
            List<ProductSpecUpdateDTO> spec = productUpdateDTO.getSpec();
            for (ProductSpecUpdateDTO specD : spec){
                Long id = specD.getId();
                if (id == null){
                    throw new MQException(MessageReturn.PRODUCT_SPEC_ID_IS_NULL);
                }
                Long specSnowFlakeById = productTxService.getSpecSnowFlakeById(id);
                Map<String, Object> Mapp;
                Integer stock = null;
                Mapp = JacksonUtils.filterNullFields(specD);
                if (Mapp.containsKey("stock")){
                    stock = (Integer) Mapp.get("stock");
                    Mapp.remove("stock");
                }
                Mapp.remove("id");
                productRedisCache.SyncSpecMQ(Mapp,specSnowFlakeById,stock);
            }
        }

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "product.spec.delete.queue", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange",value = "dead.queue.product.exchange"),
                            @Argument(name = "x-dead-letter-routing-key",value = "delete")
                    }
            ),
            exchange = @Exchange(name = "product.spec.exchange"),
            key = "delete"
    ),
            containerFactory = "auto"
    )
    public void deleteProductSpecListener(PSsnowFlake pSsnowFlake,@Header(AmqpHeaders.MESSAGE_ID) String messageId){

        if (redisUntil.hsaKey("messageId:"+ messageId)) {
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        redisUntil.setStringValue("messageId:"+ messageId,"1",24, TimeUnit.HOURS);
        productRedisCache.deleteProduct(pSsnowFlake.getProductSnowflake(),pSsnowFlake.getSpecSnowflake());
    }


//  ______________________________----------------死信队列监听器------------------------______________________________
//  ______________________________----------------死信队列监听器------------------------______________________________
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "dead.queue.product.new", durable = "true"),
                    exchange = @Exchange(name = "dead.queue.product.exchange"),
                    key = "new"
            )
    )
    public void handleAlreadyDeadLetter(Long productId){
        System.out.println("死信队列提示:::::----productId = " + productId);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "dead.queue.product.Already", durable = "true"),
                    exchange = @Exchange(name = "dead.queue.product.exchange"),
                    key = "Already"
            )
    )
    public void handleNewDeadLetter(ProductUpdateDTO productUpdateDTO,
                                    @Header("spec-is-null") boolean specIsNull){
        System.out.println("死信队列提示:::::----productUpdateDTO = " + productUpdateDTO);
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "dead.queue.product.delete", durable = "true"),
                    exchange = @Exchange(name = "dead.queue.product.exchange"),
                    key = "delete"
            )
    )
    public void handleDeleteDeadLetter(Long productId){
        System.out.println("死信队列提示:::::----productId = " + productId);
    }
}
