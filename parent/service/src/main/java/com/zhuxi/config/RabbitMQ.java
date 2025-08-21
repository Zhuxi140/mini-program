package com.zhuxi.config;

import com.zhuxi.service.Listener.MessageHandler.ConfirmCallbackDispatcher;
import com.zhuxi.task.OrderTask;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitMQ {
    // 无确认
    @Bean("noConfirm")
    public ConnectionFactory noConfirmConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.NONE);
        factory.setVirtualHost("mini_program");
        factory.setUsername("mini_program");
        factory.setPassword("123456");
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setPublisherReturns(false);
        return factory;
    }

    @Bean("rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,ConfirmCallbackDispatcher confirmCallbackDispatcher) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setConfirmCallback(confirmCallbackDispatcher);
        rabbitTemplate.setReturnsCallback(confirmCallbackDispatcher);
        return rabbitTemplate;
    }

    @Bean("expressTemplate")
    public RabbitTemplate expressTemplate(ConfirmCallbackDispatcher confirmCallbackDispatcher) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(noConfirmConnectionFactory());
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setReturnsCallback(confirmCallbackDispatcher);
        rabbitTemplate.setConfirmCallback(confirmCallbackDispatcher);
        return rabbitTemplate;
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
                "delay.exchange",
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
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConnectionFactory(cf);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
