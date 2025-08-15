package com.zhuxi.service.Impl;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageSync;
import com.zhuxi.service.Tx.DeadMessageTXService;
import com.zhuxi.service.business.DeadMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DeadMessageServiceImpl implements DeadMessageService {
    private final DeadMessageTXService deadMessageTXService;
    private final RabbitTemplate rabbitTemplate;

    public DeadMessageServiceImpl(DeadMessageTXService deadMessageTXService, RabbitTemplate rabbitTemplate) {
        this.deadMessageTXService = deadMessageTXService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 获取死信消息列表
     */
    @Override
    public Result<PageResult<DeadMessageAddDTO, Long>> getListDeadMessages(Long lastId, Integer pageSize) {
        boolean first = lastId == null || lastId < 0;
        boolean hasMore = false;
        boolean hasPrevious = !first;
        if (first){
            lastId = Long.MAX_VALUE;
        }

        List<DeadMessageAddDTO> listDeadMessages = deadMessageTXService.getListDeadMessages(lastId, pageSize + 1);
        if (listDeadMessages.size() == pageSize + 1){
            hasMore = true;
            lastId = listDeadMessages.get(pageSize).getId();
            listDeadMessages = listDeadMessages.subList(0, pageSize);
        }
        PageResult<DeadMessageAddDTO, Long> pageResult = new PageResult<>(listDeadMessages, lastId, hasPrevious, hasMore);
        return Result.success(MessageReturn.OPERATION_SUCCESS,pageResult);
    }

    /**
     * 重试死信消息
     */
    @Override
    @Transactional
    public Result<Void> retryDeadMessages(DeadMessageAddDTO dead, Map<String,Object> AdminData) {
        if (dead == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }

        String messageId = dead.getMessageId();
        String messageBody = dead.getMessageBody();
        String exchange = dead.getExchange();
        String routineKey = dead.getRoutineKey();
        String actionTaken = dead.getActionTaken();
        if (messageId == null || messageBody == null
          ||  exchange== null || routineKey == null ||
                actionTaken == null
        ){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }
        String userName = (String) AdminData.get("username");
        deadMessageTXService.UpdateHinder(userName, messageId,actionTaken);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitTemplate.convertAndSend(exchange,routineKey,messageBody,
                        Message->{
                            MessageProperties props = Message.getMessageProperties();
                            props.setMessageId(messageId);
                            props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            return Message;
                        }
                );
            }
        });
        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }

    @Override
    public Result<DeadMessageSync> syncDeadMessages(String messageId) {
        if (messageId == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }

        DeadMessageSync deadMessage = deadMessageTXService.getDeadMessage(messageId);
        return Result.success(MessageReturn.OPERATION_SUCCESS,deadMessage);
    }
}
