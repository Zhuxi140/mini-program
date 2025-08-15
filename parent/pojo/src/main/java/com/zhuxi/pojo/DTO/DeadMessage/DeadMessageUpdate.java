package com.zhuxi.pojo.DTO.DeadMessage;

public class DeadMessageUpdate {

    private String messageId;
    private String messageBody;
    private String failureReason;

    public DeadMessageUpdate() {
    }

    public DeadMessageUpdate(String messageId, String messageBody, String failureReason) {
        this.messageId = messageId;
        this.messageBody = messageBody;
        this.failureReason = failureReason;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
