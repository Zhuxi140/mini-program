package src.main.java.com.zhuxi.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import src.main.java.com.zhuxi.pojo.entity.Role;

import java.time.LocalDateTime;

public class AdminLoginVO {
    private Integer id;
    private String token;
    private String username;
    private Role role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLogin;

    public AdminLoginVO(Integer id, String token, String username, LocalDateTime lastLogin, Role role) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.lastLogin = lastLogin;
        this.role = role;
    }

    public AdminLoginVO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
