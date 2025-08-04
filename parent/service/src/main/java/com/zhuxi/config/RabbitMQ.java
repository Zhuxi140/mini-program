package com.zhuxi.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitMQ {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack){
                System.out.println("消息发送成功");
            }else {
                System.out.println("消息发送失败" + cause);
            }
        });

        rabbitTemplate.setReturnsCallback(returnMessage -> {
            System.out.println("消息路由失败" + returnMessage.getMessage());
        });

        return rabbitTemplate;
    }

    // 无确认
    @Bean
    public ConnectionFactory noConfirmConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.NONE); // 关闭Confirm
        return factory;
    }

    @Bean
    public RabbitTemplate expressTemplate() {
        return new RabbitTemplate(noConfirmConnectionFactory());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CustomExchange delayExchange(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("x-delayed-type", "direct");
        return new CustomExchange(
                "delay_exchange",
                "x-delayed-message",
                true,
                false,
                hashMap
        );
    }

    @Bean("manual")
    public SimpleRabbitListenerContainerFactory manualAckFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 手动 ACK
        factory.setPrefetchCount(1); // 严格单条处理
        return factory;
    }

    @Bean("auto")
    public SimpleRabbitListenerContainerFactory autoFactory(ConnectionFactory cf) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
