package com.zhuxi.Result;

import com.zhuxi.utils.JsonUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductPageResult<T>{
    @Schema(description = "当前页数据")
    private List<T> items;
    @Schema(description = "下一页游标(redis缓存使用)")
    private Double nextCursor;
    @Schema(description = "是否有上一页")
    private boolean hasPrevious;
    @Schema(description = "是否有下一页")
    private boolean hasNext;
    @Schema(description = "本页最后一条数据的id(用于未命中时兜底)",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long lastId;

    public ProductPageResult(List<T> items, Double nextCursor, boolean hasPrevious, boolean hasNext,Long lastId) {
        this.items = items;
        this.nextCursor = nextCursor;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.lastId = lastId;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Double getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Double nextCursor) {
        this.nextCursor = nextCursor;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "items=" + JsonUtils.objectToJson(items) +
                ", nextCursor=" + nextCursor +
                ", hasPrevious=" + hasPrevious +
                ", hasNext=" + hasNext +
                '}';
    }


}
