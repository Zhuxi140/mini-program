package com.zhuxi.service.Listener;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import com.zhuxi.pojo.DTO.Cart.CartRedisDTO;
import com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageUpdate;
import com.zhuxi.service.Cache.CartRedisCache;
import com.zhuxi.service.Tx.CartTxService;
import com.zhuxi.service.Tx.DeadMessageTXService;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.Cart.MQdelete;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CartListener {
    private final CartRedisCache cartRedisCache;
    private final CartTxService cartTxService;
    private final RedisUntil redisUntil;
    private final DeadMessageTXService deadMessageTXService;
    private final String cartKey = "deadMessage:cart:";
    private final String value = "messageId:dead:cart:";

    public CartListener(CartRedisCache cartRedisCache, CartTxService cartTxService, RedisUntil redisUntil, DeadMessageTXService deadMessageTXService) {
        this.cartRedisCache = cartRedisCache;
        this.cartTxService = cartTxService;
        this.redisUntil = redisUntil;
        this.deadMessageTXService = deadMessageTXService;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.delete.queue",durable = "true",
            arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = "dead.cart.exchange"),
                    @Argument(name = "x-dead-letter-routing-key", value = "delete")
            }
            ),
            exchange = @Exchange(name = "cart.exchange"),
            key = "delete"
    ),
    containerFactory = "auto"
    )
    public void deleteCart(@Payload MQdelete mQdelete, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        if (redisUntil.hsaKey("messageId:cart:delete:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            Boolean b = cartRedisCache.deleteCart(mQdelete.getUserId(), mQdelete.getCartId());
            if (!b){
                throw new MQException(MessageReturn.REDIS_KEY_DELETE_ERROR);
            }
            redisUntil.setStringValue("messageId:cart:delete:" + messageId, "1", 5, TimeUnit.MINUTES);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((cartKey + "delete:" + messageId), "type=MQException---{" + e.getMessage() + "," + location + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((cartKey + "delete:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.deleteAll.queue",durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dead.cart.exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "deleteAll")
                    }
            ),
            exchange = @Exchange(name = "cart.exchange"),
            key = "deleteAll"
    ),
            containerFactory = "auto"
    )
    public void deleteAllCart(@Payload Long userId, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        if(redisUntil.hsaKey("messageId:cart:deleteAll:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            cartRedisCache.deleteAllCart(userId);
            redisUntil.setStringValue("messageId:cart:deleteAll:" + messageId, "1", 5, TimeUnit.MINUTES);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((cartKey + "deleteAll:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location +  "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((cartKey + "deleteAll:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.lack.queue",durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dead.cart.exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "lack")
                    }
            ),
            exchange = @Exchange(name = "cart.exchange"),
            key = "lack"
    ),
        containerFactory = "auto"
    )
    public void updateCart(@Payload Long userId, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        if(redisUntil.hsaKey("messageId:cart:lack:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try{
            List<CartRedisDTO> listCartOne = cartTxService.getListCartOne(userId);
            cartRedisCache.syncCartInit(listCartOne,userId);
            redisUntil.setStringValue("messageId:cart:lack:" + messageId, "1", 5, TimeUnit.MINUTES);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((cartKey + "lack:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location + "}", 24, TimeUnit.HOURS);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((cartKey + "lack:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.add.queue",durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dead.cart.exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "add")
                    }
            ),
            exchange = @Exchange(name = "cart.exchange"),
            key = "add"
    ),
            containerFactory = "auto"
    )
    public void initCart(@Payload CartAddDTO cartAddDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                         @Header("isExist") boolean isExist
                         )
    {
        if(redisUntil.hsaKey("messageId:cart:add:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try{
            log.info("{}",isExist);
            if (isExist) {
                log.info("bucunzai");
                Long productIdBySnowFlake = cartTxService.getProductSnowFlakeById(cartAddDTO.getProductId());
                cartRedisCache.addCartOne(cartAddDTO, productIdBySnowFlake);
            }else{
                log.info("存在");
                Long cartIdByUS = cartTxService.getCartIdByUS(cartAddDTO.getUserId(), cartAddDTO.getSpecId());
                cartAddDTO.setCartId(cartIdByUS);
                cartRedisCache.addLackOne(cartAddDTO);
            }
            redisUntil.setStringValue("messageId:cart:add:" + messageId, "1", 5, TimeUnit.MINUTES);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((cartKey + "add:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((cartKey + "add:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.update.queue",durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dead.cart.exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "update")
                    }
            ),
            exchange = @Exchange(name = "cart.exchange"),
            key = "update"
    ))
    public void updateCart(@Payload CartUpdateDTO cartUpdateDTO,
                           @Header(AmqpHeaders.MESSAGE_ID) String messageId
                           ){
        if (redisUntil.hsaKey("messageId:cart:update:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try{
             cartRedisCache.updateCart(cartUpdateDTO);
             redisUntil.setStringValue("messageId:cart:update:" + messageId, "1", 5, TimeUnit.MINUTES);
        }catch (MQException e){
            String location = e.getLocation();
            redisUntil.setStringValue((cartKey + "update:" + messageId), "type=MQException---{" + e.getMessage() +  "," + location + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue((cartKey + "update:" + messageId), "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }
    }

    //*************************************死信**************************************************************

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.delete.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "delete"
    ))
    public void deadCart(@Payload MQdelete mQdelete,
                         @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                         @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                         )
    {
        String deadKey = cartKey + "delete:";
        String valuee = value + "delete:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }


        try{
            durableDate(xDeath,messageId,mQdelete.getUserId(),deadKey,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,mQdelete.getUserId(),deadKey,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.deleteAll.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "deleteAll"
    ))
    public void deadAllCart(@Payload Long userId,
                            @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                            @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                            )
    {
        String deadKey = cartKey + "deleteAll:";
        String valuee = value + "deleteAll:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }

        try{
            durableDate(xDeath,messageId,userId,deadKey,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,userId,deadKey,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.lack.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "lack"
    ))
    public void deadUpdateCart(@Payload Long userId,
                               @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                               @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                               )
    {
        String deadKey = cartKey + "lack:";
        String valuee = value + "lack:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try{
            durableDate(xDeath,messageId,userId,deadKey,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,userId,deadKey,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.add.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "add"
    ))
    public void deadInitCart(@Payload CartAddDTO cartAddDTO,
                             @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                             @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                             )
    {
        String deadKey = cartKey + "add:";
        String valuee = value + "add:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try{
            durableDate(xDeath,messageId,cartAddDTO.getUserId(),deadKey,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,cartAddDTO.getUserId(),deadKey,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.update.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "update"
    ))
    public void deadUpdateCart(@Payload CartUpdateDTO cartUpdateDTO,
                               @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                               @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                               )
    {
        String deadKey = cartKey + "update:";
        String valuee = value + "update:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try{
            durableDate(xDeath,messageId,cartUpdateDTO.getUserId(),deadKey,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,cartUpdateDTO.getUserId(),deadKey,valuee);
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
        redisUntil.setStringValue(Valuee+ messageId,"1",5, TimeUnit.MINUTES);
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



