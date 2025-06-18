package com.zhuxi.Result;

import java.util.List;

public class PageResult<T> {
    private List<T> items;
    private Long nextCursor;
    private boolean hasPrevious;
    private boolean hasNext;

    public PageResult(List<T> items, Long nextCursor, boolean hasPrevious, boolean hasNext) {
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

    public Long getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(Long nextCursor) {
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
}
