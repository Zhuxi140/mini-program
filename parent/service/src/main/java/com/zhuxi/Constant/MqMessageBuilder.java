/*
package com.zhuxi.Constant;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class MqMessageBuilder {

    private final Jackson2JsonMessageConverter converter;

    @Autowired
    public MqMessageBuilder(Jackson2JsonMessageConverter converter) {
        this.converter = converter;
    }

    public  Message build(Object body){
        MessageProperties props = new MessageProperties();
        props.setMessageId(UUID.randomUUID().toString());
        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        props.setContentType("application/json");
        return converter.toMessage(body, props);
    }


    public  Message build(Object body, Consumer<MessageProperties> consumer){
        MessageProperties props = new MessageProperties();
        consumer.accept(props);
        return converter.toMessage(body, props);
    }
}
*/
