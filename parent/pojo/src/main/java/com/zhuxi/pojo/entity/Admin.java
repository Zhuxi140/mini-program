package src.main.java.com.zhuxi.pojo.entity;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class Admin {
    @Schema(description = "管理员id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer id;
    @Schema(description = "用户名",requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "密码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    private Role role = Role.ADMIN;
    @Schema(description = "真实名字",requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;
    @Schema(description = "是否启用（默认启用）",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status = 1;
    @Schema(description = "上一次登录时间",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime lastLogin;

    public Admin() {
    }

    public Admin(Integer id, String username, String password, String realName, int status, LocalDateTime lastLogin, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.status = status;
        this.lastLogin = lastLogin;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}
