package com.zhuxi.pojo.VO.Admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class AdminUserVO {

    @Schema(description = "用户id")
    private Long id;
    @Schema(description = "昵称")
    private String name;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "是否被禁用")
    private Integer status;
    @Schema(description = "订单总量")
    private Long orderCount;
    @Schema(description = "最后一次登陆时间")
    private LocalDateTime lastTime;
    @Schema(description = "头像url")
    @JsonIgnore
    private String avatar;

    public AdminUserVO() {
    }

    public AdminUserVO(Long id, String name, String phone, Integer status, Long orderCount, LocalDateTime lastTime, String avatar) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.orderCount = orderCount;
        this.lastTime = lastTime;
        this.avatar = avatar;
    }

    // getter or setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(LocalDateTime lastTime) {
        this.lastTime = lastTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
