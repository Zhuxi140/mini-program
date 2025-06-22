package src.main.java.com.zhuxi.pojo.VO.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.time.LocalDateTime;

public class AdminVO {
    @Schema(description = "管理员id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer id;
    @Schema(description = "用户名",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;
    @Schema(description = "真实名字",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String realName;
    @Schema(description = "权限",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Role role;
    @Schema(description = "是否启用",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;
    @Schema(description = "上次登录时间",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime lastLogin;

    public AdminVO() {
    }

    public AdminVO(String username, String realName, int status, LocalDateTime lastLogin, Role role) {
        this.username = username;
        this.realName = realName;
        this.status = status;
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
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
