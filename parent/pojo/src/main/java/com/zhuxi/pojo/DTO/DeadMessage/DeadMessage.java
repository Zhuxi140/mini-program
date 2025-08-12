package com.zhuxi.pojo.DTO.DeadMessage;

public class DeadMessage {

    private Object Body;
    private String failureDetails;

    public DeadMessage() {
    }

    public DeadMessage(Object body, String failureDetails) {
        Body = body;
        this.failureDetails = failureDetails;
    }

    public Object getBody() {
        return Body;
    }

    public void setBody(Object body) {
        Body = body;
    }

    public String getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(String failureDetails) {
        this.failureDetails = failureDetails;
    }
}
