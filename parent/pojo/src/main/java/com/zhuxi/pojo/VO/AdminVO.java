package src.main.java.com.zhuxi.pojo.VO;

import src.main.java.com.zhuxi.pojo.entity.Role;

import java.time.LocalDateTime;

public class AdminVO {
    private Integer id;
    private String username;
    private String realName;
    private Role role;
    private Integer status;
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
