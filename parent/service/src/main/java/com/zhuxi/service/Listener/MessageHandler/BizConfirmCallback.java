package com.zhuxi.service.Listener.MessageHandler;

import org.springframework.amqp.core.ReturnedMessage;

public interface BizConfirmCallback {
    // 消息发送成功
    void handleSuccess(String messageId);
    // 消息发送失败
    void handleFailure(String messageId, String cause,BizCorrelationData bizCorrelationData);
    // 消息发送成功路由失败
    void handleReturned(String messageId, ReturnedMessage returned);
}
