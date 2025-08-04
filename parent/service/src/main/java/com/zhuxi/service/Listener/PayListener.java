package com.zhuxi.service.Listener;


import com.zhuxi.service.Cache.PayRedisCache;
import com.zhuxi.service.Tx.PayTxService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PayListener {

    private final PayTxService payTxService;
    private final PayRedisCache payRedisCache;

    public PayListener(PayTxService payTxService, PayRedisCache payRedisCache) {
        this.payTxService = payTxService;
        this.payRedisCache = payRedisCache;
    }


/*    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "delay-pay-queue",durable = "true"),
                    exchange = @Exchange(value = "delay_exchange"),
                    key = "pay"
            )
    )
    public void payListener(String OrderSn){
    }*/
}
