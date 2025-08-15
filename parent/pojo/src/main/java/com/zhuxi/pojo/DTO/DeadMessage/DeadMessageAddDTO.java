package com.zhuxi.pojo.DTO.DeadMessage;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public class DeadMessageAddDTO {


    @JsonIgnore
    private Long id;
    @Schema(description = "消息id",requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageId;
    @Schema(description = "原始队列",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String originalQueue;
    @Schema(description = "交换机",requiredMode = Schema.RequiredMode.REQUIRED)
    private String exchange;
    @Schema(description = "路由键",requiredMode = Schema.RequiredMode.REQUIRED)
    private String routineKey;
    @Schema(description = "消息体",requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageBody;
    @JsonIgnore
    private String failureReason;
    @Schema(description = "执行操作（重试、修改并重试）",requiredMode = Schema.RequiredMode.REQUIRED)
    private String actionTaken;

    public DeadMessageAddDTO() {
    }

    public DeadMessageAddDTO(Long id, String messageId, String originalQueue, String exchange, String routineKey, String messageBody, String failureReason, String actionTaken) {
        this.id = id;
        this.messageId = messageId;
        this.originalQueue = originalQueue;
        this.exchange = exchange;
        this.routineKey = routineKey;
        this.messageBody = messageBody;
        this.failureReason = failureReason;
        this.actionTaken = actionTaken;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getOriginalQueue() {
        return originalQueue;
    }

    public void setOriginalQueue(String originalQueue) {
        this.originalQueue = originalQueue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutineKey() {
        return routineKey;
    }

    public void setRoutineKey(String routineKey) {
        this.routineKey = routineKey;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }
}
