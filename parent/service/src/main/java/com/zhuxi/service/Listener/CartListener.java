package com.zhuxi.service.Listener;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.MQException;
import com.zhuxi.pojo.DTO.Cart.CartAddDTO;
import com.zhuxi.pojo.DTO.Cart.CartRedisDTO;
import com.zhuxi.pojo.DTO.Cart.CartUpdateDTO;
import com.zhuxi.service.Cache.CartRedisCache;
import com.zhuxi.service.Tx.CartTxService;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.Cart.MQdelete;
import com.zhuxi.pojo.VO.Car.CartVO;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CartListener {
    private final CartRedisCache cartRedisCache;
    private final CartTxService cartTxService;
    private final RedisUntil redisUntil;

    public CartListener(CartRedisCache cartRedisCache, CartTxService cartTxService, RedisUntil redisUntil) {
        this.cartRedisCache = cartRedisCache;
        this.cartTxService = cartTxService;
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
            exchange = @Exchange(name = "cart.exchange"),
            key = "deleteAll"
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
    public void updateCart(@Payload Long userId, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        if(redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try{
            List<CartRedisDTO> listCartOne = cartTxService.getListCartOne(userId);
            cartRedisCache.syncCartInit(listCartOne,userId);
            redisUntil.setStringValue("messageId:" + messageId, "1", 24, TimeUnit.HOURS);
        }catch (Exception e){
            redisUntil.delete("messageId:" + messageId);
            throw new MQException(e.getMessage());
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
    ))
    public void initCart(@Payload CartAddDTO cartAddDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                         @Header("isExist") boolean isExist
                         )
    {
        if(redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try{
            if (isExist) {
                Long productIdBySnowFlake = cartTxService.getProductSnowFlakeById(cartAddDTO.getProductId());
                cartRedisCache.addCartOne(cartAddDTO, productIdBySnowFlake);
            }else{
                Long cartIdByUS = cartTxService.getCartIdByUS(cartAddDTO.getUserId(), cartAddDTO.getSpecId());
                cartAddDTO.setCartId(cartIdByUS);
                cartRedisCache.addLackOne(cartAddDTO);
            }
            redisUntil.setStringValue("messageId:" + messageId, "1", 24, TimeUnit.HOURS);
        }catch (Exception e){
            redisUntil.delete("messageId:" + messageId);
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
    public void updateCart(@Payload CartUpdateDTO cartUpdateDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId){
        if (redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try{
             cartRedisCache.updateCart(cartUpdateDTO);
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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.add.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "add"
    ))
    public void deadInitCart(@Payload CartAddDTO cartAddDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        log.error("死信消息-----messageId:{}",messageId);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dead.cart.update.queue",durable = "true"),
            exchange = @Exchange(name = "dead.cart.exchange"),
            key = "update"
    ))
    public void deadUpdateCart(@Payload CartUpdateDTO cartUpdateDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId)
    {
        log.error("死信消息-----messageId:{}",messageId);
    }


}



