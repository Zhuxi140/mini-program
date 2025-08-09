package com.zhuxi.service.Listener;


import com.rabbitmq.client.Channel;
import com.zhuxi.Exception.MQException;
import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class OrderListener {

    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;
    private final TransactionTemplate transactionTemplate;
    private final RedisUntil redisUntil;

    public OrderListener(OrderTxService orderTxService, OrderRedisCache orderRedisCache, TransactionTemplate transactionTemplate, RedisUntil redisUntil) {
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
        this.transactionTemplate = transactionTemplate;
        this.redisUntil = redisUntil;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.delay.new.queue", durable = "true",
                    arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = "dead.order.exchange"),
                    @Argument(name = "x-dead-letter-routing-key", value = "new"),
            }
            ),
            exchange = @Exchange(value = "delay.exchange", type = "x-delayed-message"),
            key = "new"
    ),
            containerFactory = "manual"
    )
    public void orderNew(String orderSn,Channel channel,@Header(AmqpHeaders.MESSAGE_ID) String messageId,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag)    {
        if (redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try {
            if (orderSn == null || orderSn.isEmpty()){
                log.error("orderSn is null or empty");
                throw new MQException("orderSn is null or empty");
            }


            String orderSn1 = orderSn.trim().replaceAll("^\"|\"$", "");
            Boolean execute = transactionTemplate.execute(status -> {
                    try {
                    Long orderId = orderRedisCache.getOrderIdBySn(orderSn1);
                    if (orderId == null) {
                        orderId = orderTxService.getOrderIdByOrderSn(orderSn1);
                    }
                        int orderStatus = orderTxService.getOrderStatus(orderId);
                    if (orderStatus != 0) {
                        log.info("订单状态发生变更（非待付款），停止取消");
                        return true;
                    }
                        orderTxService.concealOrderL(orderId);
                    return true;
                    }catch (Exception e) {
                        status.setRollbackOnly();
                        throw e;
                    }
            });

            if (Boolean.TRUE.equals(execute)) {
                log.info("订单延迟消息处理成功");
                orderRedisCache.syncOrderStatus(orderSn1,4);
                redisUntil.setStringValue("messageId:"+ messageId,"1",24, TimeUnit.HOURS);
                try {
                    channel.basicAck(tag, false);
                } catch (IOException e) {
                    log.error("重试失败");
                }
            } else {
                log.info("订单延迟消息处理失败 进入死信");
                try {
                    channel.basicNack(tag, false, false);
                } catch (IOException ex) {
                    log.error("重试失败");
                }
            }
        }catch (Exception e){
            log.error("订单延迟消息处理失败 进入死信");
            try {
                channel.basicNack(tag, false, false);
            } catch (IOException ex) {
                log.error("重试失败");
            }
        }
        }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead.auto.order.queue", durable = "true"),
            exchange = @Exchange(value = "dead.order.exchange"),
            key = "new"
    ))
    public void deadOrderNew(String orderSn) {
        log.info("死信---orderSn: {}", orderSn);
    }
}





