package com.zhuxi.pojo.DTO.DeadMessage;

public class DeadMessageSync {
    private String messageBody;
    private String failureReason;

    public DeadMessageSync() {
    }

    public DeadMessageSync(String messageBody, String failureReason) {
        this.messageBody = messageBody;
        this.failureReason = failureReason;
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
