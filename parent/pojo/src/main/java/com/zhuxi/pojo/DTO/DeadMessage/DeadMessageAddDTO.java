package com.zhuxi.pojo.DTO.DeadMessage;



public class DeadMessageAddDTO {

    private String messageId;
    private String originalQueue;
    private String exchange;
    private String routineKey;
    private String messageBody;
    private String failureReason;

    public DeadMessageAddDTO() {
    }

    public DeadMessageAddDTO(String messageId, String originalQueue, String exchange, String routineKey, String messageBody, String failureReason) {
        this.messageId = messageId;
        this.originalQueue = originalQueue;
        this.exchange = exchange;
        this.routineKey = routineKey;
        this.messageBody = messageBody;
        this.failureReason = failureReason;
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
}
