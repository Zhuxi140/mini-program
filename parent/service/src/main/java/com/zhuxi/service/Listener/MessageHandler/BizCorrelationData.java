package com.zhuxi.service.Listener.MessageHandler;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public class BizCorrelationData extends CorrelationData {
    private String type;
    private Object data;

    public BizCorrelationData() {
    }

    public BizCorrelationData(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public BizCorrelationData(String id, String type, Object data) {
        super(id);
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
