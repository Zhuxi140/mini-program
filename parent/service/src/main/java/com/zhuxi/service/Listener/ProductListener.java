package com.zhuxi.service.Listener;

import com.zhuxi.ApplicationRunner.Data.Loader.ReBuildBloom;
import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageUpdate;
import com.zhuxi.pojo.VO.Product.ProductDetailVO;
import com.zhuxi.service.Cache.ProductRedisCache;
import com.zhuxi.service.Tx.DeadMessageTXService;
import com.zhuxi.service.Tx.ProductTxService;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.product.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProductListener {
    private final ProductRedisCache productRedisCache;
    private final ProductTxService productTxService;
    private final RedisUntil redisUntil;
    private final DeadMessageTXService deadMessageTXService;
    private final String deadKey = "deadMessage:product.spec:";
    private final String value = "messageId:dead:product.spec:";
    private final ReBuildBloom reBuildBloom;

    public ProductListener(ProductRedisCache productRedisCache, ProductTxService productTxService, RedisUntil redisUntil, DeadMessageTXService deadMessageTXService, ReBuildBloom reBuildBloom) {
        this.productRedisCache = productRedisCache;
        this.productTxService = productTxService;
        this.redisUntil = redisUntil;
        this.deadMessageTXService = deadMessageTXService;
        this.reBuildBloom = reBuildBloom;
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
    public void NewProductSpecListener(Long productId,
                                       @Header(AmqpHeaders.MESSAGE_ID) String messageId
    ){

        if (redisUntil.hsaKey("messageId:product.spec:new:"+ messageId)) {
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            ProductDetailVO product = productTxService.getListProductMQ(productId);
            productRedisCache.syncProductOne(product);
            Long saleProductSnowFlake = productTxService.getSaleProductIdOne(productId);
            List<SpecRedisDTO> spec = productTxService.getSpec(List.of(productId));
            PIdSnowFlake pIdSnowFlake = new PIdSnowFlake(saleProductSnowFlake, saleProductSnowFlake);
            productRedisCache.syncSpecInit(spec, List.of(pIdSnowFlake));
            reBuildBloom.addProductData(productId);
            redisUntil.setStringValue("messageId:product.spec:new:" + messageId, "1", 5, TimeUnit.MILLISECONDS);
        }catch(MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((deadKey + "new:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch(Exception e){
            redisUntil.setStringValue((deadKey + "new:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }
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

        if (redisUntil.hsaKey("messageId:product.spec:Already:"+ messageId)) {
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            ProductBaseUpdateDTO base = productUpdateDTO.getBase();
            Long productId = base.getId();
            if (productId == null) {
                throw new MQException(MessageReturn.PRODUCT_ID_IS_NULL);
            }
            Long snowFlakeById = productTxService.getProductSnowFlakeById(productId);
            Map<String, Object> Map = JacksonUtils.filterNullFields(base);
            Map.remove("id");
            Map.remove("supplierId");
            productRedisCache.SyncProductMQ(Map, snowFlakeById);

            if (!specIsNull) {
                List<ProductSpecUpdateDTO> spec = productUpdateDTO.getSpec();
                for (ProductSpecUpdateDTO specD : spec) {
                    Long id = specD.getId();
                    if (id == null) {
                        throw new MQException(MessageReturn.PRODUCT_SPEC_ID_IS_NULL);
                    }
                    Long specSnowFlakeById = productTxService.getSpecSnowFlakeById(id);
                    Map<String, Object> Mapp;
                    Integer stock = null;
                    Mapp = JacksonUtils.filterNullFields(specD);
                    if (Mapp.containsKey("stock")) {
                        stock = (Integer) Mapp.get("stock");
                        Mapp.remove("stock");
                    }
                    Mapp.remove("id");
                    productRedisCache.SyncSpecMQ(Mapp, specSnowFlakeById, stock);
                }
            }
        redisUntil.setStringValue("messageId:product.spec:Already:" + messageId, "1", 5, TimeUnit.MILLISECONDS);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((deadKey + "Already:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location +"}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((deadKey + "Already:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "product.spec.delete.queue", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange",value = "dead.queue.product.exchange"),
                            @Argument(name = "x-dead-letter-routing-key",value = "delete.stopSale")
                    }
            ),
            exchange = @Exchange(name = "product.spec.exchange"),
            key = {"delete","stopSale"}
    ),
            containerFactory = "auto"
    )
    public void deleteProductSpecListener(Object data,@Header(AmqpHeaders.MESSAGE_ID) String messageId){

        if (redisUntil.hsaKey("messageId:product.spec:delete.stopSale:"+ messageId)) {
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try{
            if (data instanceof PSsnowFlake pSsnowFlake){
                productRedisCache.deleteProduct(pSsnowFlake.getProductSnowflake(),pSsnowFlake.getSpecSnowflake());
            }else if(data instanceof Long id){
                PSsnowFlake pSsnowFlake = new PSsnowFlake();
                pSsnowFlake.setProductSnowflake(productTxService.getProductSnowFlakeById(id));
                pSsnowFlake.setSpecSnowflake(productTxService.getSpecSnowFlakeByIdList(id));
                productRedisCache.deleteProduct(pSsnowFlake.getProductSnowflake(),pSsnowFlake.getSpecSnowflake());
            }
        redisUntil.setStringValue("messageId:product.spec:delete.stopSale:"+ messageId,"1",1, TimeUnit.HOURS);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((deadKey + "dS:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((deadKey + "dS:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }
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
    public void handleAlreadyDeadLetter(Long productId,
                                        @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                                        @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
    ){
        String dead = deadKey + "new:";
        String valuee = value + "new:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }

        try {
            durableDate(xDeath,messageId,productId,dead,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,productId,dead,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "dead.queue.product.Already", durable = "true"),
                    exchange = @Exchange(name = "dead.queue.product.exchange"),
                    key = "Already"
            )
    )
    public void handleNewDeadLetter(ProductUpdateDTO productUpdateDTO,
                                    @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                                    @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
    ){
        String dead = deadKey  + "Already:";
        String valuee = value + "Already:";

        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }

        try{
            durableDate(xDeath,messageId,productUpdateDTO,dead,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,productUpdateDTO,dead, valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "dead.queue.product.delete", durable = "true"),
                    exchange = @Exchange(name = "dead.queue.product.exchange"),
                    key = "delete.stopSale"
            )
    )
    public void handleDeleteDeadLetter(Long productId,
                                       @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                                       @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                                       ){
        String dead = deadKey + "dS:";
        String valuee = value + "dS:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }

        try{
            durableDate(xDeath,messageId,productId,dead,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,productId,dead,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }



    private String getQueue(List<Map<String, ?>> xDeath){
        if (xDeath != null &&  !xDeath.isEmpty()){
            return (String) xDeath.get(0).get("queue");
        }
        return null;
    }

    private String getExchange(List<Map<String, ?>> xDeath){
        if (xDeath != null &&  !xDeath.isEmpty()){
            return (String) xDeath.get(0).get("exchange");
        }
        return null;
    }

    private String getRoutingKey(List<Map<String, ?>> xDeath){
        if (xDeath != null &&  !xDeath.isEmpty()){
            List<String> routingKeys = (List<String>) xDeath.get(0).get("routing-keys");
            if (routingKeys != null && !routingKeys.isEmpty()){
                return routingKeys.get(0);
            }
        }
        return null;
    }


    private void durableDate(List<Map<String, ?>> xDeath, String messageId, Object body,String dead, String Valuee){
        Object failureDetails = redisUntil.getStringValue(dead + messageId);
        String boddy = JacksonUtils.objectToJson(body);
        if (deadMessageTXService.isExist(messageId)){
            Long version = deadMessageTXService.getVersion(messageId);
            DeadMessageUpdate deadMessageUpdate = new DeadMessageUpdate();
            deadMessageUpdate.setMessageId(messageId);
            deadMessageUpdate.setMessageBody(boddy);
            deadMessageUpdate.setFailureReason((String) failureDetails);
            deadMessageTXService.update(deadMessageUpdate, version);
            return;
        }
        DeadMessageAddDTO deadd = new DeadMessageAddDTO();
        deadd.setMessageId(messageId);
        deadd.setMessageBody(boddy);
        deadd.setRoutineKey(getRoutingKey(xDeath));
        deadd.setExchange(getExchange(xDeath));
        deadd.setOriginalQueue(getQueue(xDeath));
        deadd.setFailureReason((String) failureDetails);
        deadMessageTXService.insert(deadd);
        redisUntil.setStringValue(Valuee+ messageId,"1",5, TimeUnit.MILLISECONDS);
        redisUntil.delete(dead + messageId);
        log.warn("已记录----死信::----messageId = " + messageId);
    }
    private void HandlerException(List<Map<String, ?>> xDeath, String messageId, Object body,String dead, String Valuee){
        log.error(
                """
                messageId--{},
                FailureReason--{},
                exchange--{}，
                OriginalQueue--{},
                RoutineKey--{},
                MessageBody--{}，
                """,
                messageId,
                redisUntil.getStringValue(dead + messageId),
                getExchange(xDeath),
                getQueue(xDeath),
                getRoutingKey(xDeath),
                JacksonUtils.objectToJson(body)
        );
        redisUntil.delete(Valuee + messageId);
        redisUntil.delete(dead + messageId);
    }

}
