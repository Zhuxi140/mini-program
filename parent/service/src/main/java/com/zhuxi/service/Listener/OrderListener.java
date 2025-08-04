package com.zhuxi.service.Listener;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.OrderTxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.io.IOException;
import java.util.Map;


@Component
@Slf4j
public class OrderListener {

    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;

    public OrderListener(OrderTxService orderTxService, OrderRedisCache orderRedisCache) {
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-delay-new-queue", durable = "true",
                    arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = "dead.order.queue"),
                    @Argument(name = "x-dead-letter-routing-key", value = "new"),
                    @Argument(name = "x-message-ttl", value = "10000")
            }
            ),
            exchange = @Exchange(value = "delay-exchange", type = "x-delayed-message"),
            key = "new"
    ),
            containerFactory = "manual"
    )
    public void orderNew(String orderSn,Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        try {
            Long orderId = orderRedisCache.getOrderIdBySn(orderSn);
            boolean isHit = (orderId != null);
            orderTxService.concealOrderL(orderId, orderSn, isHit);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                channel.basicNack(tag, false, false);
            } catch (IOException ex) {
                log.error("重试失败");
            }
        }
    }



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead.auto.order.queue", durable = "true"),
            exchange = @Exchange(value = "dead.order.queue"),
            key = "new"
    ))
    public void deadOrderNew(String orderSn) {
    }
}