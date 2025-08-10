package com.zhuxi.pojo.DTO.User;

import io.swagger.v3.oas.annotations.media.Schema;
import com.zhuxi.pojo.entity.Role;

public class UserLoginDTO {

    @Schema(description = "用户id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;
    @Schema(description = "微信openId",requiredMode = Schema.RequiredMode.REQUIRED)
    private String openId;
    @Schema(description = "权限",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Role role;
    @Schema(description = "jwt-token",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String token;

    public UserLoginDTO() {
    }

    public UserLoginDTO(String openId, Role role, String token, Long id) {
        this.openId = openId;
        this.role = role;
        this.token = token;
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
