package com.zhuxi.pojo.DTO.product;

public class RebackResult<T> {
    private int affectRows;
    private T data;

    public RebackResult()
    {
    }

    public int getAffectRows() {
        return affectRows;
    }

    public void setAffectRows(int affectRows) {
        this.affectRows = affectRows;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
