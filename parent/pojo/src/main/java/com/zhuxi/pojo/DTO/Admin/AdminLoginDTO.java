package com.zhuxi.pojo.DTO.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import com.zhuxi.pojo.entity.Role;

public class AdminLoginDTO {
    @Schema(description = "管理员id",hidden = true)
    private Integer  id;
    @Schema(description = "用户名",requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "权限(ADMIN 或 SUPER_ADMIN)", hidden = true)
    private Role role;

    public AdminLoginDTO() {
    }

    public AdminLoginDTO(Integer id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
