package com.zhuxi.service.Listener;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.service.Cache.CartRedisCache;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import src.main.java.com.zhuxi.pojo.DTO.Cart.MQdelete;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CartListener {
    private final CartRedisCache cartRedisCache;
    private final RedisUntil redisUntil;

    public CartListener(CartRedisCache cartRedisCache, RedisUntil redisUntil) {
        this.cartRedisCache = cartRedisCache;
        this.redisUntil = redisUntil;
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
        if (redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            Boolean b = cartRedisCache.deleteCart(mQdelete.getUserId(), mQdelete.getCartId());
            if (!b){
                throw new MQException(MessageReturn.REDIS_KEY_DELETE_ERROR);
            }
            redisUntil.setStringValue("messageId:" + messageId, "1", 24, TimeUnit.HOURS);
        }catch (Exception e){
            redisUntil.delete("messageId:" + messageId);
            throw new MQException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart.deleteAll.queue",durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dead.cart.all.exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "deleteAll")
                    }
            ),
            exchange = @Exchange(name = "cart.exchange")
    ),
            containerFactory = "auto"
    )
    public void deleteAllCart(@Payload Long userId, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        if(redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            cartRedisCache.deleteAllCart(userId);
            redisUntil.setStringValue("messageId:" + messageId, "1", 24, TimeUnit.HOURS);
        }catch (Exception e){
            redisUntil.delete("messageId:" + messageId);
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
    ))
    public void updateCart(@Payload List<CartVO> cartVOs, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        if(redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try{
                cartRedisCache.syncCartLack(cartVOs);
                redisUntil.setStringValue("messageId:" + messageId, "1", 24, TimeUnit.HOURS);
        }catch (Exception e){
            redisUntil.delete("messageId:" + messageId);
            throw new MQException(e.getMessage());
        }
    }

    //*************************************死信**************************************************************

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.delete.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "delete"
    ))
    public void deadCart(@Payload MQdelete mQdelete, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        log.error("死信消息-----messageId:{}",messageId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.deleteAll.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "deleteAll"
    ))
    public void deadAllCart(@Payload Long userId, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        log.error("死信消息-----messageId:{}",messageId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.lack.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "lack"
    ))
    public void deadUpdateCart(@Payload List<CartVO> cartVOs, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        log.error("死信消息-----messageId:{}",messageId);
    }


}



