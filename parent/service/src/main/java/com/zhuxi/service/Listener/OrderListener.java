package com.zhuxi.service.Listener;


import com.rabbitmq.client.Channel;
import com.zhuxi.Exception.MQException;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageUpdate;
import com.zhuxi.pojo.DTO.Order.OrderMqDTO;
import com.zhuxi.pojo.DTO.Order.OrderRedisDTO;
import com.zhuxi.service.Cache.OrderRedisCache;
import com.zhuxi.service.Tx.DeadMessageTXService;
import com.zhuxi.service.Tx.OrderTxService;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class OrderListener {

    private final OrderTxService orderTxService;
    private final OrderRedisCache orderRedisCache;
    private final TransactionTemplate transactionTemplate;
    private final RedisUntil redisUntil;
    private final DeadMessageTXService deadMessageTXService;
    @Value("${init-Data.page-size}")
    private int batchSize;
    private final String deadKey = "deadMessageId:order:";
    private final String vale = "messageId:dead:order:";

    public OrderListener(OrderTxService orderTxService, OrderRedisCache orderRedisCache, TransactionTemplate transactionTemplate, RedisUntil redisUntil, DeadMessageTXService deadMessageTXService) {
        this.orderTxService = orderTxService;
        this.orderRedisCache = orderRedisCache;
        this.transactionTemplate = transactionTemplate;
        this.redisUntil = redisUntil;
        this.deadMessageTXService = deadMessageTXService;
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
    public void orderNew(OrderMqDTO orderMqDTO,Channel channel,@Header(AmqpHeaders.MESSAGE_ID) String messageId,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag)    {
        if (redisUntil.hsaKey("messageId:order:new:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try {
            String orderSn = orderMqDTO.getOrderSn();
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
                redisUntil.setStringValue("messageId:order:new:"+ messageId,"1",1, TimeUnit.HOURS);
                try {
                    channel.basicAck(tag, false);
                } catch (IOException e) {
                    log.error("重试失败");
                }
            } else {
                log.info("订单延迟消息处理失败 进入死信");
                try {
                    redisUntil.setStringValue(deadKey + "new:"+ messageId,"1",24, TimeUnit.HOURS);
                    channel.basicNack(tag, false, false);
                } catch (IOException ex) {
                    log.error("重试失败");
                }
            }
        }catch (Exception e){
            log.error("订单延迟消息处理失败 进入死信");
            try {
                redisUntil.setStringValue(deadKey + "new:"+ messageId,"1",24, TimeUnit.HOURS);
                channel.basicNack(tag, false, false);
            } catch (IOException ex) {
                log.error("重试失败");
            }
        }
        }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.neww.queue", durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dead.order.exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "neww"),
                    }
            ),
            exchange = @Exchange(value = "order.exchange"),
            key = "neww"
    ),
            containerFactory = "auto"
    )
    public void orderNew(OrderMqDTO orderMqDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId)    {
        if (redisUntil.hsaKey("messageId:order:new:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }
        try {
            OrderRedisDTO orderRedis = orderTxService.getOrderRedis(orderMqDTO.getUserId(), orderMqDTO.getOrderSn());
            orderRedisCache.syncOrderData(Collections.singletonList(orderRedis), orderMqDTO.getUserId());
            redisUntil.setStringValue("messageId:order:new:"+ messageId,"1",1, TimeUnit.HOURS);
        }catch (MQException e){
            redisUntil.setStringValue(deadKey + "new:" + messageId, "type=MQException---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException(e.getMessage());
        }catch (Exception e){
            redisUntil.setStringValue(deadKey + "new:" + messageId, "type=MQException---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.sync.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange"),
            key = "sync"
    ),
        containerFactory = "auto"
    )
    public void orderSync(Long userId,@Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        if (redisUntil.hsaKey("messageId:order:sync:" + messageId)) {
            log.error("重复消息-----messageId:{}", messageId);
            return;
        }

        try {
            Long orderId = 0L;
            while (true) {
                List<OrderRedisDTO> listOrder = orderTxService.getOrderRedisList(userId, orderId, batchSize + 1);
                if (listOrder.isEmpty()) {
                    break;
                } else if (listOrder.size() <= batchSize) {
                    orderRedisCache.syncOrderData(listOrder, userId);
                    break;
                } else {
                    orderId = listOrder.get(listOrder.size() - 1).getId();
                    listOrder = listOrder.subList(0, batchSize);
                    orderRedisCache.syncOrderData(listOrder, userId);
                }
            }
                redisUntil.setStringValue("messageId:order:sync:" + messageId, "1", 24, TimeUnit.HOURS);
            }catch(MQException e){
                redisUntil.setStringValue(deadKey + "sync:" + messageId, "type=MQException---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
                throw new AmqpRejectAndDontRequeueException(e.getMessage());
            }catch (Exception e){
                redisUntil.setStringValue(deadKey + "sync:" + messageId, "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
                throw new AmqpRejectAndDontRequeueException(e.getMessage());
            }
    }

// ___________________________________________________死信监听器_______________________________________________________________
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead.auto.order.queue", durable = "true"),
            exchange = @Exchange(value = "dead.order.exchange"),
            key = "new"
    ))
    public void deadOrderNew(OrderMqDTO orderMqDTO,
                             @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                             @Header(name = "x-death",required = false) List<Map<String, ?>> xDeath
                             ) {
        String dead = deadKey + "new:";
        String valuee = vale + "new:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try{
            durableDate(xDeath,messageId,orderMqDTO,dead,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,orderMqDTO,dead,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead.sync.order.queue", durable = "true"),
            exchange = @Exchange(value = "dead.order.exchange"),
            key = "sync"
    ))
    public void deadOrderSync(Long userId,
                              @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                              @Header(name = "x-death",required = false) List<Map<String, ?>> xDeath
                              ) {
        String dead = deadKey + "sync:";
        String valuee = vale + "sync:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try{
            durableDate(xDeath,messageId,userId,dead,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,userId,dead,valuee);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "dead.neww.order.queue", durable = "true"),
            exchange = @Exchange(value = "dead.order.exchange"),
            key = "neww"
    ))
    public void deadOrderNeww(OrderMqDTO orderMqDTO,
                             @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                             @Header(name = "x-death",required = false) List<Map<String, ?>> xDeath
    ) {
        String dead = deadKey + "new:";
        String valuee = vale + "new:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try{
            durableDate(xDeath,messageId,orderMqDTO,dead,valuee);
        }catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,orderMqDTO,dead,valuee);
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





