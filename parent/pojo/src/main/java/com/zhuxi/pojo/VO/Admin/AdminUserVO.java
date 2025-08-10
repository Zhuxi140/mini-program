package com.zhuxi.pojo.VO.Admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class AdminUserVO {

    @Schema(description = "用户id")
    private Long id;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "昵称")
    private String displayName;
    @Schema(description = "头像url")
    private String avatar;
    @Schema(description = "是否启用")
    private Integer status;
    @Schema(description = "订单总量")
    private Long orderCount;
    @Schema(description = "最后一次订单时间")
    private LocalDateTime lastOrderTime;

    public AdminUserVO(Long id, String phone, String displayName, String avatar, Integer status, Long orderCount, LocalDateTime lastOrderTime) {
        this.id = id;
        this.phone = phone;
        this.displayName = displayName;
        this.avatar = avatar;
        this.status = status;
        this.orderCount = orderCount;
        this.lastOrderTime = lastOrderTime;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public LocalDateTime getLastOrderTime() {
        return lastOrderTime;
    }

    public void setLastOrderTime(LocalDateTime lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }
}
