package com.zhuxi.service.MessageService;

import com.zhuxi.pojo.DTO.Order.OrderMqDTO;
import com.zhuxi.service.Listener.MessageHandler.BizConfirmCallback;
import com.zhuxi.service.Listener.MessageHandler.BizCorrelationData;
import com.zhuxi.service.Listener.MessageHandler.ConfirmCallbackDispatcher;
import com.zhuxi.task.OrderTask;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OrderMessage {
        private final RabbitTemplate rabbitTemplate;
        private final ConfirmCallbackDispatcher dispatcher;
        private final OrderTask orderTask;

    public OrderMessage(@Qualifier("rabbitTemplate") RabbitTemplate rabbitTemplate, ConfirmCallbackDispatcher dispatcher, OrderTask orderTask) {
        this.rabbitTemplate = rabbitTemplate;
        this.dispatcher = dispatcher;
        this.orderTask = orderTask;
    }

    // 注册订单消息处理器
        @PostConstruct
        public void init() {
            dispatcher.registerCallback("ORDER", new BizConfirmCallback() {
                @Override
                public void handleSuccess(String messageId) {
                    log.info("消息发送成功,id:{}",messageId);
                }
                @Override
                public void handleFailure(String messageId, String cause, BizCorrelationData bizCorrelationData) {
                    Object data = bizCorrelationData.getData();
                    log.warn("消息发送失败,启用兜底方案");
                    orderTask.onMqFailure(data.toString());
                }
                @Override
                public void handleReturned(String messageId, ReturnedMessage returned) {
                    log.error("消息路由失败 id:{}",messageId);
                    // 记录日志或告警
                }
            });
        }

        public void sendOrderDelayMessage(OrderMqDTO orderMqDTO, String exchange, String routingKey) {
            BizCorrelationData correlationData = new BizCorrelationData(
                    UUID.randomUUID().toString(), "ORDER", orderMqDTO.getOrderSn()
            );
            rabbitTemplate.convertAndSend(
                    exchange,
                    routingKey,
                    orderMqDTO,
                    msg -> {
                        // 将correlationId存入消息头，用于Return回调识别
                        MessageProperties props = msg.getMessageProperties();
                        props.setHeader(
                                "spring_returned_message_correlation",
                                correlationData
                        );
                        props.setHeader("x-delay", 30*60*1000); // 延迟30分钟
                        msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return msg;
                    },
                    correlationData
            );
        }
}
