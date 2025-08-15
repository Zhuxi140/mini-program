package com.zhuxi.service.Listener;


import com.zhuxi.Exception.MQException;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageUpdate;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.DeadMessageTXService;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.RedisUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.zhuxi.pojo.DTO.User.LoginMQDTO;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WechatListener {
    private final LoginRedisCache loginRedisCache;
    private final RedisUntil redisUntil;
    private final DeadMessageTXService deadMessageTXService;
    private final String deadKey = "deadMessageId:wechat:";
    private final String value = "messageId:dead:wechat:";

    public WechatListener(LoginRedisCache loginRedisCache, RedisUntil redisUntil, DeadMessageTXService deadMessageTXService) {
        this.loginRedisCache = loginRedisCache;
        this.redisUntil = redisUntil;
        this.deadMessageTXService = deadMessageTXService;
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

        if (redisUntil.hsaKey("messageId:wechat:login:"+ messageId)){
            log.error("重复消息-----messageId:{}",messageId);
            return;
        }

        try {
            UserBasicDTO userBasicDTO = new UserBasicDTO();
            userBasicDTO.setOpenId(openId);
            userBasicDTO.setName(loginMQDTO.getName());
            userBasicDTO.setAvatar(loginMQDTO.getAvatar());
            loginRedisCache.initUserBasic(userBasicDTO);
            String sessionKey = loginMQDTO.getSessionKey();
            if (sessionKey == null || sessionKey.isEmpty()) {
                log.error("sessionKey is null or empty");
                throw new MQException("sessionKey is null or empty");
            } else {
                loginRedisCache.saveSessionKey(openId, sessionKey);
            }
            redisUntil.setStringValue("messageId:wechat:login:" + messageId, "1", 5, TimeUnit.MINUTES);
        } catch (MQException e) {
            redisUntil.setStringValue(deadKey + "login:" + messageId, "type=MQException---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new MQException("微信登录异常");
        }catch (Exception e){
            redisUntil.setStringValue(deadKey + "login:" + messageId, "type=other Exception---{" + e.getMessage() + "}", 24, TimeUnit.HOURS);
            throw new AmqpRejectAndDontRequeueException(e.getMessage());
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
    public void handleLoginDeadLetter(@Payload LoginMQDTO loginMQDTO,
                                      @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                                      @Header(name = "x-death", required = false) List<Map<String, ?>> xDeath
                                      ) {
        String dead = deadKey + "login:";
        String valuee = value + "login:";
        if (redisUntil.hsaKey(valuee + messageId)){
            log.error("重复消息-----dead---messageId:{}",messageId);
            return;
        }
        try {
            durableDate(xDeath,messageId,loginMQDTO,dead,valuee);
        } catch (Exception e){
            log.error("----死信记录失败----日志兜底---- 死信记录错误{}",e.getMessage());
            HandlerException(xDeath,messageId,loginMQDTO,dead,valuee);
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
        redisUntil.setStringValue(Valuee+ messageId,"1",5, TimeUnit.MINUTES);
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
