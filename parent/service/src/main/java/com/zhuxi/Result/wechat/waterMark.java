package com.zhuxi.Result.wechat;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class waterMark {
    /*@JsonFormat(
            shape = JsonFormat.Shape.NUMBER,
            timezone = "GMT+8",
            pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
*/

    private long timestamp;
    public waterMark() {
    }

    public waterMark(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "waterMark{" +
                "timestamp=" + timestamp +
                '}';
    }
}
