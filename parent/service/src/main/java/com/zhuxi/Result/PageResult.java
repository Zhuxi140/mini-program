package com.zhuxi.Result;

import com.zhuxi.utils.JsonUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class PageResult<T,C> {
    @Schema(description = "当前页数据")
    private List<T> items;
    @Schema(description = "下一页游标")
    private C nextCursor;
    @Schema(description = "是否有上一页")
    private boolean hasPrevious;
    @Schema(description = "是否有下一页")
    private boolean hasNext;

    public PageResult(List<T> items, C nextCursor, boolean hasPrevious, boolean hasNext) {
        this.items = items;
        this.nextCursor = nextCursor;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public C getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(C nextCursor) {
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
