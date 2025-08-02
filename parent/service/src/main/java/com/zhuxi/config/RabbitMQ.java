package com.zhuxi.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    // 无确认模板（需单独配置ConnectionFactory）
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
}
