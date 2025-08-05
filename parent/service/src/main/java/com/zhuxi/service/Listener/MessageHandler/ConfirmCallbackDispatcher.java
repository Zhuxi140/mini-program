package com.zhuxi.service.Listener.MessageHandler;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConfirmCallbackDispatcher implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback{
    private final Map<String , BizConfirmCallback> callbackMap = new ConcurrentHashMap<>();

    public ConfirmCallbackDispatcher() {
    }

    public void registerCallback(String type, BizConfirmCallback callback){
        callbackMap.put(type, callback);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (correlationData instanceof BizCorrelationData){
            BizCorrelationData Biz = (BizCorrelationData) correlationData;
            String type = Biz.getType();
            BizConfirmCallback callback =  callbackMap.get(type);
            if (callback != null){
                if (ack){
                    callback.handleSuccess(correlationData.getId());
                }else{
                    callback.handleFailure(correlationData.getId(), cause, Biz);
                }
            }
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        Object raw = returnedMessage.getMessage().getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        if (raw instanceof BizCorrelationData){
            BizCorrelationData correlationData = (BizCorrelationData) raw;
            BizConfirmCallback callback =  callbackMap.get(correlationData.getType());
            if (callback != null){
                callback.handleReturned(correlationData.getId(), returnedMessage);
            }
        }
    }

   /* public Map<String, BizConfirmCallback> getCallbackMap(String id) {
    }*/
}
