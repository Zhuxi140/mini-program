package com.zhuxi.service.Listener;


import com.zhuxi.Exception.MQException;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.User.LoginMQDTO;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WechatListener {
    private final LoginRedisCache loginRedisCache;
    private final RedisUntil redisUntil;

    public WechatListener(LoginRedisCache loginRedisCache, RedisUntil redisUntil) {
        this.loginRedisCache = loginRedisCache;
        this.redisUntil = redisUntil;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "wechat.login.queue",durable = "true",arguments = {
                   @Argument(name = "x-dead-letter-exchange",value = "dead.wechat.exchange"),
                   @Argument(name = "x-dead-letter-routing-key",value = "login")
            }),
            exchange = @Exchange(name = "wechat.Exchange"),
            key = "login"
    ))
    public void loginListener(@Payload LoginMQDTO loginMQDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        if (loginMQDTO == null){
            log.error("接收到的消息体为null");
            throw new MQException("消息体为null");
        }

        String openId = loginMQDTO.getOpenId();
        if (openId == null || openId.isEmpty()){
            log.error("openId is null or empty");
            throw new MQException("openId is null or empty");
        }

        if (redisUntil.hsaKey("messageId:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        redisUntil.setStringValue("messageId:"+ messageId,"1",24, TimeUnit.HOURS);
        UserBasicDTO userBasicDTO = new UserBasicDTO();
        userBasicDTO.setOpenId(openId);
        userBasicDTO.setName(loginMQDTO.getName());
        userBasicDTO.setAvatar(loginMQDTO.getAvatar());
        loginRedisCache.initUserBasic(userBasicDTO);
        String sessionKey = loginMQDTO.getSessionKey();
        if (sessionKey == null || sessionKey.isEmpty()){
            log.error("sessionKey is null or empty");
            throw new MQException("sessionKey is null or empty");
        }else{
            loginRedisCache.saveSessionKey(openId,sessionKey);
        }
    }





    // ---------------------------------------------死信----------------------------------------------------
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "dead.queue.wechat.login", durable = "true"),
                    exchange = @Exchange(name = "dead.wechat.exchange"),
                    key = "login"
            )
    )
    public void handleLoginDeadLetter(@Payload LoginMQDTO loginMQDTO, @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        log.error("死信队列接收到微信登录消息：{}",messageId);
    }


}
